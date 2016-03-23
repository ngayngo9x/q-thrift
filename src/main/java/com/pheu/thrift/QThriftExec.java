package com.pheu.thrift;


import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.exception.NoBackendException;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.google.common.base.Preconditions;

public abstract class QThriftExec<T, P> {
	
	private QThriftProvider<P> provider;

	public QThriftExec(QThriftProvider<P> provider) {
		this.provider = provider;
	}

	public T exec() throws TException {
		Preconditions.checkNotNull(provider);
		
		List<ThriftServerInfo> servers = provider.getServerInfoProvider().allServices();
		if (servers == null || servers.isEmpty()) {
			throw new NoBackendException();
		}
        ThriftServerInfo selected = provider.getSelectorStrategy().choose(servers);

        TTransport transport = provider.getPoolProvider().getConnection(selected);
        TProtocol protocol = provider.getProtocalProvider().getProtocol(transport);
		boolean success = true;
		TException ex = null;
		T e = null;
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

	protected abstract T call(TProtocol protocol) throws TException;
}
