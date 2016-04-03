package com.pheu.service.client;

import java.util.List;

import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public interface QServiceDiscover<P> {
	public void start() throws ServiceDiscoveryException;
	public void close() throws ServiceDiscoveryException;
	public List<ThriftServerInfo> allServices();
	//public Optional<Node<P>> nextService();
}
