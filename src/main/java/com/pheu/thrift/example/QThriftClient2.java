package com.pheu.thrift.example;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.pheu.service.QServiceDiscover;
import com.pheu.service.QServiceDiscoverImpl;
import com.pheu.service.ServiceDiscoveryException;
import com.pheu.thrift.client.QFailoverPoolProvider;
import com.pheu.thrift.client.QProtocolFactory;
import com.pheu.thrift.client.QRoundRobinSelectorStrategy;
import com.pheu.thrift.client.QThriftClientFactory;
import com.pheu.thrift.client.QThriftConnectionProvider;
import com.pheu.thrift.client.QThriftProviderImpl;

public class QThriftClient2 {

	public static void main(String[] args) throws ServiceDiscoveryException {

		QServiceDiscover<Void> discover = new QServiceDiscoverImpl.Builder<Void>().withConnectionTimeout(1000)
				.withSessionTimeout(1000).withConnectString("localhost:2181").withServiceName("qthriftserverice").build();
		discover.start();

		String message = "Quy";

		QThriftConnectionProvider<Void> thriftProvider = new QThriftProviderImpl.Builder<Void>()
				.withPoolProvider(new QFailoverPoolProvider<Void>(discover)).serviceDiscoveryProvider(discover)
				.withSelectorStrategy(new QRoundRobinSelectorStrategy()).build();

		QThriftClientFactory<Void> thriftFactory = new QThriftClientFactory<>(thriftProvider);

		TestThriftService.Iface client = thriftFactory.create(TestThriftService.Iface.class, new QProtocolFactory<TestThriftService.Iface>() {
			@Override
			public TestThriftService.Iface getProtocol(TTransport transport) {
				TProtocol protocol = new TCompactProtocol(transport);
				return new TestThriftService.Client(protocol);
			}
		});
		
		callService(message, client);
		callService(message, client);
		callService(message, client);
		callService(message, client);
		callService(message, client);
		
		/*
Time: 6665
Time: 5810
Time: 6140
Time: 5893
Time: 5982 

Time: 6838
Time: 6181
Time: 6287
Time: 5905
Time: 6039
		 
		 * */

		discover.close();

	}

	private static void callService(String message, TestThriftService.Iface client) {
		long start = System.currentTimeMillis();
		try {
			for (int i = 1; i <= 100000; i++) {
				client.echo(message);
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Time: " + (System.currentTimeMillis() - start));
	}

}
