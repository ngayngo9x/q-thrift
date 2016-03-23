package com.pheu.thrift.example;

import java.util.List;

import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public interface ServiceInfoProvider {

	public List<ThriftServerInfo> list();
	public ThriftServerInfo nextService();
	
}
