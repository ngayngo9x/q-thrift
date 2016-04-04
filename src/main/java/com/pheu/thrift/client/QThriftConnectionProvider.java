package com.pheu.thrift.client;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.pheu.service.QServiceDiscover;

public interface QThriftConnectionProvider<P> {
	public ThriftConnectionPoolProvider getPoolProvider();

	public QServiceDiscover<P> getServiceDiscoverProvider();

	//public QProtocolFactory<T> getProtocalProvider();
	
	public QSelectorStrategy getSelectorStrategy();
}
