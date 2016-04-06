package com.pheu.thrift.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.exception.NoBackendException;
import com.pheu.common.ThriftServerInfo;

public class QThriftClientFactory {

	private QThriftConnectionProvider provider;

	public QThriftClientFactory(QThriftConnectionProvider provider) {
		this.provider = provider;
	}

	public <T> T create(Class<T> serviceInterface, QClientFactory<T> protocolFactory) {
		ThriftCaller<T> caller = new ThriftCaller<>(serviceInterface, provider, protocolFactory);

		@SuppressWarnings("unchecked")
		T instance = (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[] { serviceInterface },
				new ThriftHandler<>(caller));
		return instance;
	}

	private static class ThriftCaller<T> {
		private QThriftConnectionProvider provider;
		private QClientFactory<T> protocolFactory;
		
		public ThriftCaller(Class<T> serviceInterface, QThriftConnectionProvider provider, QClientFactory<T> protocolFactory) {
			this.provider = provider;
			this.protocolFactory = protocolFactory;
		}

		public Object call(Method method, Object[] args) {
			List<ThriftServerInfo> servers = provider.getServiceDiscoverProvider().allServices();
			if (servers == null || servers.isEmpty()) {
				throw new NoBackendException();
			}
			ThriftServerInfo selected = provider.getSelectorStrategy().choose(servers);

			TTransport transport = provider.getPoolProvider().getConnection(selected);
			T t = protocolFactory.getClient(transport);

			boolean success = false;
			Object result = null;
			try {
				result = invoke(t, method, args);
				success = true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.out.println(e.getMessage());
			} finally {
				if (success) {
					provider.getPoolProvider().returnConnection(selected, transport);
				} else {
					provider.getPoolProvider().returnBrokenConnection(selected, transport);
				}
			}
			return result;

		}
		
		public static Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return method.invoke(proxy, args);
		}

	}

	private static class ThriftHandler<T> implements InvocationHandler {

		private ThriftCaller<T> caller;

		public ThriftHandler(ThriftCaller<T> caller) {
			this.caller = caller;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return caller.call(method, args);
		}

	}

}
