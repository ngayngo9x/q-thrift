package com.pheu.thrift.client;

import org.apache.thrift.transport.TTransport;

public interface QClientFactory<T> {
	T getClient(TTransport transport);

}
