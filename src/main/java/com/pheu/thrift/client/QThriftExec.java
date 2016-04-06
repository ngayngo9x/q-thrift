package com.pheu.thrift.client;


import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.exception.NoBackendException;
import com.google.common.base.Preconditions;
import com.pheu.common.ThriftServerInfo;

public abstract class QThriftExec<T extends TServiceClient, R> {
	
	private QThriftConnectionProvider provider;
	private QClientFactory<T> protocolFactory;

	public QThriftExec(QThriftConnectionProvider provider, QClientFactory<T> protocolFactory) {
		this.provider = provider;
		this.protocolFactory = protocolFactory;
	}

	public R exec() throws TException {
		Preconditions.checkNotNull(provider);
		
		List<ThriftServerInfo> servers = provider.getServiceDiscoverProvider().allServices();
		if (servers == null || servers.isEmpty()) {
			throw new NoBackendException();
		}
        ThriftServerInfo selected = provider.getSelectorStrategy().choose(servers);

        TTransport transport = provider.getPoolProvider().getConnection(selected);
        T t = protocolFactory.getClient(transport);

		boolean success = true;
		TException ex = null;
		R e = null;
        try {
        	e = call(t);
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
