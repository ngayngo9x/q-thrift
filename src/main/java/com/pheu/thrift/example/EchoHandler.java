package com.pheu.thrift.example;

import org.apache.thrift.TException;

public class EchoHandler implements TestThriftService.Iface {

	private int port;

	public EchoHandler(int port) {
		this.port = port;
	}
	
	@Override
	public String echo(String message) throws TException {
		return "Hello " + message + ", port=" + port;
	}

	@Override
	public void echo1(String message) throws TException {
		System.out.println(echo(message));
	}

}
