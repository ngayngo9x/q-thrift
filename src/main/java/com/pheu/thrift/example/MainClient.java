package com.pheu.thrift.example;

import java.util.Arrays;

import org.apache.thrift.TException;

import com.github.phantomthief.thrift.client.ThriftClient;
import com.github.phantomthief.thrift.client.impl.ThriftClientImpl;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;

public class MainClient {
	public static void main(String[] args) {
		
		ThriftClient thriftClient = new ThriftClientImpl(() -> Arrays.asList(//
		        ThriftServerInfo.of("localhost", 9090), //
		        ThriftServerInfo.of("localhost", 9091) //
		        // or you can return a dynamic result.
		        ));
		try {
			System.out.println(thriftClient.iface(TestThriftService.Client.class).echo("quy"));
		} catch (TException e) {
			System.out.println(e.getMessage());
		}
	}
}
