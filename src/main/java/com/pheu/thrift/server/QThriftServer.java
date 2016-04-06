package com.pheu.thrift.server;

import org.apache.thrift.transport.TTransportException;

public interface QThriftServer extends AutoCloseable {

	public void start() throws TTransportException;
	
}
