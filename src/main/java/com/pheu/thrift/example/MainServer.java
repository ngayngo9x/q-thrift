package com.pheu.thrift.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import com.librato.disco.DiscoService;

public class MainServer {
	
	public static void main(String[] args) {
	
		TestThriftService.Iface handler = new EchoHandler(9090);
		TestThriftService.Iface handler2 = new EchoHandler(9091);
		
		TProcessor processor = new TestThriftService.Processor<>(handler);
		TProcessor processor2 = new TestThriftService.Processor<>(handler2);
		
		start(processor, 9090);
		start(processor2, 9091);
		
				
	}

	private static void start(TProcessor processor, int port) {
		try {
			CuratorFramework framework = CuratorFrameworkFactory.builder()
					.connectionTimeoutMs(1000)
					.connectString("localhost:2181")
					.retryPolicy(new ExponentialBackoffRetry(1000, 5))
					.build();
			DiscoService service = new DiscoService(framework, "qthriftserverice");
			framework.start();
			
			int maxWorkerThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
		    TServerTransport transport = new TServerSocket(port, 1000);
		    TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
		    TTransportFactory transportFactory = new TFramedTransport.Factory(10000);
		    TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
		            .processorFactory(new TProcessorFactory(processor)).protocolFactory(protocolFactory)
		            .transportFactory(transportFactory).minWorkerThreads(1)
		            .maxWorkerThreads(maxWorkerThreads);
		    TThreadPoolServer server = new TThreadPoolServer(args);
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					try {
						service.start("localhost", port, true, "".getBytes());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					server.serve();
				}
			};
			new Thread(r).start();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
