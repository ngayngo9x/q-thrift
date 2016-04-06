package com.pheu.thrift.client;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.github.phantomthief.thrift.client.pool.impl.DefaultThriftConnectionPoolImpl;
import com.github.phantomthief.thrift.client.utils.FailoverCheckingStrategy;
import com.pheu.common.ThriftServerInfo;
import com.pheu.service.QServiceDiscover;
import com.pheu.service.ServiceDiscoveryException;

public class QFailoverPoolProvider implements ThriftConnectionPoolProvider, QServiceDiscover {
	private QServiceDiscover discover;

	private ThriftConnectionPoolProvider connectionPoolProvider;

	private FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy;

	public QFailoverPoolProvider(QServiceDiscover discover, ThriftConnectionPoolProvider connectionPoolProvider,
			FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy) {
		this.discover = discover;
		this.connectionPoolProvider = connectionPoolProvider;
		this.failoverCheckingStrategy = failoverCheckingStrategy;
	}

	public QFailoverPoolProvider(QServiceDiscover discover, DefaultThriftConnectionPoolImpl instance) {
		this(discover, instance,
				new FailoverCheckingStrategy<>(10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1)));
	}

	public QFailoverPoolProvider(QServiceDiscover discover) {
		this(discover, DefaultThriftConnectionPoolImpl.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider#
	 * getConnection(com.github.phantomthief.thrift.client.pool.
	 * ThriftServerInfo)
	 */
	@Override
	public TTransport getConnection(ThriftServerInfo thriftServerInfo) {
		return connectionPoolProvider.getConnection(thriftServerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider#
	 * returnConnection(com.github.phantomthief.thrift.client.pool.
	 * ThriftServerInfo, org.apache.thrift.transport.TTransport)
	 */
	@Override
	public void returnConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
		connectionPoolProvider.returnConnection(thriftServerInfo, transport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider#
	 * returnBrokenConnection(com.github.phantomthief.thrift.client.pool.
	 * ThriftServerInfo, org.apache.thrift.transport.TTransport)
	 */
	@Override
	public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
		failoverCheckingStrategy.fail(thriftServerInfo);
		connectionPoolProvider.returnBrokenConnection(thriftServerInfo, transport);
	}

	@Override
	public void start() throws ServiceDiscoveryException {
		discover.start();
	}

	@Override
	public void close() throws ServiceDiscoveryException {
		discover.start();
	}

	@Override
	public List<ThriftServerInfo> allServices() {
		Set<ThriftServerInfo> failedServers = failoverCheckingStrategy.getFailed();
		return discover.allServices().stream().filter(i -> !failedServers.contains(i)).collect(toList());
	}

	public static class Builder {
		private QServiceDiscover discover;
		private Function<ThriftServerInfo, TTransport> transportProvider = DEFAULT_TRANSPORT_PROVIDER;
		private FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy = new FailoverCheckingStrategy<>(10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1));
		private GenericKeyedObjectPoolConfig poolConfig = DEFAULT_CONFIG;
		
		public Builder discover(QServiceDiscover discover) {
			this.discover = discover;
			return this;
		}
		
		public Builder transportProvider(Function<ThriftServerInfo, TTransport> transportProvider) {
			this.transportProvider = transportProvider;
			return this;
		}

		public Builder failoverCheckingStrategy(FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy) {
			this.failoverCheckingStrategy = failoverCheckingStrategy;
			return this;
		}
		
		public Builder poolConfig(GenericKeyedObjectPoolConfig poolConfig) {
			this.poolConfig = poolConfig;
			return this;
		}

		public QFailoverPoolProvider build() {
			return new QFailoverPoolProvider(this);
		}
		
		// constants
		private static final int MIN_CONN = 1;
	    private static final int MAX_CONN = 1000;
	    private static final int TIMEOUT = (int) MINUTES.toMillis(5);
	    private static final GenericKeyedObjectPoolConfig DEFAULT_CONFIG;
	    public static final Function<ThriftServerInfo, TTransport> DEFAULT_TRANSPORT_PROVIDER = info -> {
            TSocket tsocket = new TSocket(info.getHost(), info.getPort());
            tsocket.setTimeout(TIMEOUT);
            return new TFramedTransport(tsocket);
        };
		static {
			DEFAULT_CONFIG = new GenericKeyedObjectPoolConfig();
			DEFAULT_CONFIG.setMaxTotal(MAX_CONN);
			DEFAULT_CONFIG.setMaxTotalPerKey(MAX_CONN);
			DEFAULT_CONFIG.setMaxIdlePerKey(MAX_CONN);
			DEFAULT_CONFIG.setMinIdlePerKey(MIN_CONN);
			DEFAULT_CONFIG.setTestOnBorrow(true);
			DEFAULT_CONFIG.setMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
			DEFAULT_CONFIG.setSoftMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
			DEFAULT_CONFIG.setJmxEnabled(false);
        }
	}

	private QFailoverPoolProvider(Builder builder) {
		this.discover = builder.discover;
		this.connectionPoolProvider = new DefaultThriftConnectionPoolImpl(builder.poolConfig, builder.transportProvider);
		this.failoverCheckingStrategy = builder.failoverCheckingStrategy;
	}
}
