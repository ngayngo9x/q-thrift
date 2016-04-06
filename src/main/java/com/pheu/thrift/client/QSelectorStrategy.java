package com.pheu.thrift.client;

import java.util.List;

import com.pheu.common.ThriftServerInfo;

public interface QSelectorStrategy {
	ThriftServerInfo choose(List<ThriftServerInfo> servers);
}
