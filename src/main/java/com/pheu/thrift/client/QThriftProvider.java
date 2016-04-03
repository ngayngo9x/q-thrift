package com.pheu.thrift.client;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.pheu.service.client.QServiceDiscover;

public interface QThriftProvider<P> {
	public ThriftConnectionPoolProvider getPoolProvider();

	public QServiceDiscover<P> getServerInfoProvider();

	public QProtocolProvider getProtocalProvider();
	
	public QSelectorStrategy getSelectorStrategy();
}
