package com.pheu.thrift;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.pheu.service.QServiceDiscover;

public interface QThriftProvider<P> {
	public ThriftConnectionPoolProvider getPoolProvider();

	public QServiceDiscover<P> getServerInfoProvider();

	public QProtocolProvider getProtocalProvider();
	
	public QSelectorStrategy getSelectorStrategy();
}
