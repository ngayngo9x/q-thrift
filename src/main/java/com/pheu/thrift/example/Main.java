package com.pheu.thrift.example;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.librato.disco.Decoder;
import com.librato.disco.DiscoClient;
import com.librato.disco.DiscoService;
import com.librato.disco.Node;
import com.librato.disco.RoundRobinSelectorStrategy;
import com.librato.disco.SelectorStrategy;

public class Main {

	public static void main(String[] args) {
		
		CuratorFramework framework = CuratorFrameworkFactory.builder()
				.connectionTimeoutMs(1000)
				.connectString("localhost:2181")
				.retryPolicy(new ExponentialBackoffRetry(1000, 5))
				.build();
		
		CuratorFramework framework2 = CuratorFrameworkFactory.builder()
				.connectionTimeoutMs(1000)
				.connectString("localhost:2181")
				.retryPolicy(new ExponentialBackoffRetry(1000, 5))
				.build();
		
		DiscoService service = new DiscoService(framework2, "serverTest");
		
		SelectorStrategy strategy = new RoundRobinSelectorStrategy();
		DiscoClient<String> client = new DiscoClient<>(framework, "serverTest", strategy, new Decoder<String>() {

			@Override
			public String decode(byte[] bytes) {
				return new String(bytes);
			}

			@Override
			public void handleException(Exception ex) {
				System.out.println(ex.getMessage());
			}
		});
		
		try {
			framework.start();
			framework2.start();
			service.start("quy", 1, true, "".getBytes());
			service.start("quy", 2, true, "".getBytes());
			client.start();
		
			//Thread.sleep(1000 * 10);
			
			List<Node<String>> allNodes = client.getAllNodes();
			allNodes.forEach(node -> System.out.println(node.host + ":" + node.port));
			
			client.stop();
			service.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		framework.close();
		framework2.close();
	}
	
}
