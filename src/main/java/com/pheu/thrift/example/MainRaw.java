package com.pheu.thrift.example;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

public class MainRaw {
	public static void main(String[] args) throws TException {
		
		TSocket socket = new TSocket("127.0.0.1", 9090);
		socket.open();
		TProtocol proto = new TBinaryProtocol(socket);
		TestThriftService.Client client = new TestThriftService.Client(proto);
		System.out.println(client.echo("quy"));
		socket.close();
		
	}
}
