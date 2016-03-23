package com.pheu.thrift;

import java.util.List;

import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public interface QSelectorStrategy {
	ThriftServerInfo choose(List<ThriftServerInfo> servers);
}
