package com.pheu.thrift.example;

import java.util.function.Function;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.github.phantomthief.thrift.client.ThriftClient;

public class DynThriftClientImpl implements ThriftClient {

	public DynThriftClientImpl() {
		
	}
	
	@Override
	public <X extends TServiceClient> X iface(Class<X> ifaceClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X extends TServiceClient> X iface(Class<X> ifaceClass, int hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X extends TServiceClient> X iface(Class<X> ifaceClass, Function<TTransport, TProtocol> protocolProvider,
			int hash) {
		// TODO Auto-generated method stub
		return null;
	}

}
