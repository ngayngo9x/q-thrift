package com.pheu.thrift.client;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.pheu.service.QServiceDiscover;

public interface QThriftConnectionProvider {
	public ThriftConnectionPoolProvider getPoolProvider();

	public QServiceDiscover getServiceDiscoverProvider();
	
	public QSelectorStrategy getSelectorStrategy();
}
