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
import com.pheu.thrift.client.QThriftExec;
import com.pheu.thrift.client.QThriftConnectionProvider;
import com.pheu.thrift.client.QThriftProviderImpl;

public class QThriftClient3 {

	public static void main(String[] args) throws ServiceDiscoveryException {

		QServiceDiscover<Void> discover = new QServiceDiscoverImpl.Builder<Void>().withConnectionTimeout(1000)
				.withSessionTimeout(1000).withConnectString("localhost:2181").withServiceName("qthriftserverice").build();
		discover.start();

		String message = "Quy";

		QThriftConnectionProvider<Void> thriftProvider = new QThriftProviderImpl.Builder<Void>()
				.withPoolProvider(new QFailoverPoolProvider<Void>(discover)).serviceDiscoveryProvider(discover)
				.withSelectorStrategy(new QRoundRobinSelectorStrategy()).build();

		QThriftExec<TestThriftService.Iface, Void, String> qThriftExec = new QThriftExec<TestThriftService.Iface, Void, String>(
				thriftProvider,
				new QProtocolFactory<TestThriftService.Iface>() {
					@Override
					public TestThriftService.Iface getProtocol(TTransport transport) {
						TProtocol protocol = new TCompactProtocol(transport);
						return new TestThriftService.Client(protocol);
					}
				}) {

			@Override
			protected String call(TestThriftService.Iface protocol) throws TException {
				return protocol.echo(message);
			}
		};

		try {
			for (int i = 1; i <= 10; i++) {
				System.out.println(qThriftExec.exec());
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		discover.close();

	}

}
