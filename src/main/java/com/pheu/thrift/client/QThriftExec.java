package com.pheu.thrift.client;


import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.exception.NoBackendException;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.google.common.base.Preconditions;

public abstract class QThriftExec<T, P, R> {
	
	private QThriftConnectionProvider<P> provider;
	private QProtocolFactory<T> clientFactory;

	public QThriftExec(QThriftConnectionProvider<P> provider, QProtocolFactory<T> clientFactory) {
		this.provider = provider;
		this.clientFactory = clientFactory;
	}

	public R exec() throws TException {
		Preconditions.checkNotNull(provider);
		
		List<ThriftServerInfo> servers = provider.getServiceDiscoverProvider().allServices();
		if (servers == null || servers.isEmpty()) {
			throw new NoBackendException();
		}
        ThriftServerInfo selected = provider.getSelectorStrategy().choose(servers);

        TTransport transport = provider.getPoolProvider().getConnection(selected);
		T protocol = clientFactory.getProtocol(transport);
		boolean success = true;
		TException ex = null;
		R e = null;
        try {
        	e = call(protocol);
		} catch (TException ex1) {
			ex = ex1;
			success = false;
		} finally {
			if (success) {
				provider.getPoolProvider().returnConnection(selected, transport);
            } else {
            	provider.getPoolProvider().returnBrokenConnection(selected, transport);
            	throw new TException(ex);
            }
		}
        return e;
	}

	protected abstract R call(T protocol) throws TException;
}
