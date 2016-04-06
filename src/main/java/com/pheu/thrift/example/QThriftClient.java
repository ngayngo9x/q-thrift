package com.pheu.thrift.example;

import java.net.InetSocketAddress;
import java.util.function.Function;

import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import com.pheu.common.ThriftServerInfo;
import com.pheu.service.QServiceDiscover;
import com.pheu.service.QServiceDiscoverImpl;
import com.pheu.service.ServiceDiscoveryException;
import com.pheu.thrift.client.QFailoverPoolProvider;
import com.pheu.thrift.client.QClientFactory;
import com.pheu.thrift.client.QRoundRobinSelectorStrategy;
import com.pheu.thrift.client.QThriftExec;
import com.pheu.thrift.client.QThriftConnectionProvider;
import com.pheu.thrift.client.QThriftProviderImpl;

public class QThriftClient {

	public static void main(String[] args) throws ServiceDiscoveryException {

		QServiceDiscover discover = new QServiceDiscoverImpl.Builder<Void>().withConnectionTimeout(1000)
				.withSessionTimeout(1000).withConnectString("localhost:2181").withServiceName("qthriftserverice").build();
		discover.start();

		String message = "Quy";

		NiftyClient client = new NiftyClient(NettyClientConfig.newBuilder().setBossThreadCount(1).setWorkerThreadCount(8).build());
		
		QThriftConnectionProvider thriftProvider = new QThriftProviderImpl.Builder()
				.withPoolProvider(new QFailoverPoolProvider.Builder().discover(discover).transportProvider(new Function<ThriftServerInfo, TTransport>() {
					@Override
					public TTransport apply(ThriftServerInfo t) {
						InetSocketAddress address = new InetSocketAddress(t.getHost(), t.getPort());
						FramedClientConnector framedClientConnector = new FramedClientConnector(address, TDuplexProtocolFactory.fromSingleFactory(new TCompactProtocol.Factory()));
							try {
								return client.connectSync(TestThriftService.Client.class, framedClientConnector);
							} catch (TTransportException | InterruptedException e) {
								return null;
							}
					}
				}).build()).serviceDiscoveryProvider(discover)
				.withSelectorStrategy(new QRoundRobinSelectorStrategy()).build();

		QThriftExec<TestThriftService.Client, String> qThriftExec = new QThriftExec<TestThriftService.Client, String>(
				thriftProvider,
				new QClientFactory<TestThriftService.Client>() {
					@Override
					public TestThriftService.Client getClient(TTransport transport) {
						TProtocol p = new TCompactProtocol(transport);
						//TMultiplexedProtocol mul = new TMultiplexedProtocol(p, "b");
						return new TestThriftService.Client(p);
					}
				}) {

			@Override
			protected String call(TestThriftService.Client protocol) throws TException {
				return protocol.echo(message);
			}
		};

		callService(qThriftExec);
		callService(qThriftExec);
		callService(qThriftExec);
		callService(qThriftExec);
		callService(qThriftExec);

		discover.close();
		client.close();

	}

	private static void callService(QThriftExec<TestThriftService.Client, String> qThriftExec) {
		long start = System.currentTimeMillis();
		try {
			for (int i = 1; i <= 100000; i++) {
				qThriftExec.exec();
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Time: " + (System.currentTimeMillis() - start));
	}

}
