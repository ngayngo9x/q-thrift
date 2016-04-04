package com.pheu.thrift.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.exception.NoBackendException;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public class QThriftClientFactory<P> {

	private QThriftConnectionProvider<P> provider;

	public QThriftClientFactory(QThriftConnectionProvider<P> provider) {
		this.provider = provider;
	}

	public <T> T create(Class<T> serviceInterface, QProtocolFactory<T> clientFactory) {
		ThriftCaller<T, P> caller = new ThriftCaller<>(provider, clientFactory);

		@SuppressWarnings("unchecked")
		T instance = (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[] { serviceInterface },
				new ThriftHandler<>(caller));
		return instance;
	}

	private static class ThriftCaller<T, P> {
		private QThriftConnectionProvider<P> provider;
		private QProtocolFactory<T> clientFactory;

		public ThriftCaller(QThriftConnectionProvider<P> provider, QProtocolFactory<T> clientFactory) {
			this.provider = provider;
			this.clientFactory = clientFactory;
		}

		public Object call(Method method, Object[] args) {
			List<ThriftServerInfo> servers = provider.getServiceDiscoverProvider().allServices();
			if (servers == null || servers.isEmpty()) {
				throw new NoBackendException();
			}
			ThriftServerInfo selected = provider.getSelectorStrategy().choose(servers);

			TTransport transport = provider.getPoolProvider().getConnection(selected);
			T protocol = clientFactory.getProtocol(transport);

			boolean success = false;
			Object result = null;
			try {
				result = invoke(protocol, method, args);
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

	private static class ThriftHandler<T, P> implements InvocationHandler {

		private ThriftCaller<T, P> caller;

		public ThriftHandler(ThriftCaller<T, P> caller) {
			this.caller = caller;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return caller.call(method, args);
		}

	}

}
