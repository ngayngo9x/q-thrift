package com.pheu.thrift.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.github.phantomthief.thrift.client.impl.ThriftClientImpl;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.librato.disco.Decoder;
import com.librato.disco.DiscoClient;
import com.librato.disco.Node;
import com.librato.disco.RoundRobinSelectorStrategy;
import com.librato.disco.SelectorStrategy;
import com.pheu.thrift.QThriftExec;

public class Fuck implements TestThriftService.Iface {

	public Fuck(ThriftClientImpl thriftClient) {
		this.thriftClient = thriftClient;
	}

	private ThriftClientImpl thriftClient;

	public static void main(String[] args) throws Exception {

		CuratorFramework framework = CuratorFrameworkFactory.builder().connectionTimeoutMs(1000)
				.connectString("localhost:2181").retryPolicy(new ExponentialBackoffRetry(1000, 5)).build();
		framework.start();
		SelectorStrategy strategy = new RoundRobinSelectorStrategy();
		DiscoClient<String> client = new DiscoClient<>(framework, "serverTest", strategy, new Decoder<String>() {

			@Override
			public String decode(byte[] bytes) {
				return null;
			}

			@Override
			public void handleException(Exception ex) {
				System.out.println(ex.getMessage());
			}
		});

		client.start();
		ThriftClientImpl thriftClient = new ThriftClientImpl(new Supplier<List<ThriftServerInfo>>() {

			@Override
			public List<ThriftServerInfo> get() {
				List<ThriftServerInfo> result = new ArrayList<>();
				List<Node<String>> allNodes = client.getAllNodes();
				for (Node<String> node : allNodes) {
					result.add(ThriftServerInfo.of(node.host, node.port));
				}
				return result;
			}
		});

		Fuck fuck = new Fuck(thriftClient);

		try {
			for (int i = 1; i <= 10; i++) {
				fuck.echo1("Void");
				System.out.println(fuck.echo("Quynt"));
			}
		} catch (TException e) {
		}

	}

	@Override
	public String echo(String message) throws TException {
		QThriftExec<String> ex = new QThriftExec<String>(thriftClient) {
			@Override
			public String call(TProtocol protocol) throws TException {
				TestThriftService.Client client = new TestThriftService.Client(protocol);
				return client.echo(message);
			}
		};
		return ex.exec();
	}

	@Override
	public void echo1(String message) throws TException {
		QThriftExec<Void> ex = new QThriftExec<Void>(thriftClient) {
			@Override
			public Void call(TProtocol protocol) throws TException {
				TestThriftService.Client client = new TestThriftService.Client(protocol);
				client.echo1(message);
				return null;
			}
		};
		ex.exec();
	}
}
