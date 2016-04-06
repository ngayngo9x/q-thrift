package com.pheu.thrift.client;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.pheu.common.ThriftServerInfo;

public class QRoundRobinSelectorStrategy implements QSelectorStrategy {
	private final AtomicLong idx = new AtomicLong(0);
	
	@Override
	public ThriftServerInfo choose(List<ThriftServerInfo> servers) {
		return servers.get((int) (idx.getAndIncrement() % servers.size()));
	}

}
