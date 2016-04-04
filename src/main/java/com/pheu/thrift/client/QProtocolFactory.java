package com.pheu.thrift.client;

import org.apache.thrift.transport.TTransport;

public interface QProtocolFactory<T> {

	T getProtocol(TTransport transport);

}
