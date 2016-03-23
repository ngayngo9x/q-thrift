package com.pheu.thrift;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public class QRoundRobinSelectorStrategy implements QSelectorStrategy {
	private final AtomicLong idx = new AtomicLong(0);
	
	@Override
	public ThriftServerInfo choose(List<ThriftServerInfo> servers) {
		return servers.get((int) (idx.getAndIncrement() % servers.size()));
	}

}
