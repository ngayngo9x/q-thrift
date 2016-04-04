package com.pheu.thrift.client;


import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.github.phantomthief.thrift.client.pool.impl.DefaultThriftConnectionPoolImpl;
import com.github.phantomthief.thrift.client.utils.FailoverCheckingStrategy;
import com.pheu.service.QServiceDiscover;
import com.pheu.service.ServiceDiscoveryException;

public class QFailoverPoolProvider<P> implements ThriftConnectionPoolProvider, QServiceDiscover<P> {
	private final QServiceDiscover<P> discover;
	
	private final ThriftConnectionPoolProvider connectionPoolProvider;

    private final FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy;

    private QFailoverPoolProvider(QServiceDiscover<P> discover, ThriftConnectionPoolProvider connectionPoolProvider,
            FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy) {
        this.discover = discover;
    	this.connectionPoolProvider = connectionPoolProvider;
        this.failoverCheckingStrategy = failoverCheckingStrategy;
    }
    
    public QFailoverPoolProvider(QServiceDiscover<P> discover, DefaultThriftConnectionPoolImpl instance) {
		this(discover, instance, new FailoverCheckingStrategy<>(
	            10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1)));
	}
    
    public QFailoverPoolProvider(QServiceDiscover<P> discover) {
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
		return discover.allServices().stream()
                .filter(i -> !failedServers.contains(i)).collect(toList());
	}

}
