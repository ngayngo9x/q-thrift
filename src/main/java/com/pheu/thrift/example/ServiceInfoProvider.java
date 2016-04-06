package com.pheu.thrift.example;

import java.util.List;

import com.pheu.common.ThriftServerInfo;

public interface ServiceInfoProvider {

	public List<ThriftServerInfo> list();
	public ThriftServerInfo nextService();
	
}
