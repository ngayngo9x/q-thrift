package com.pheu.thrift.server;

import java.util.concurrent.ExecutorService;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

public class QThreadPoolServer implements QThriftServer {

	private TProcessorFactory processorFactory;
	private TProtocolFactory protocolFactory;
	private int requestTimeout;
	private ExecutorService executorService;
	private int maxWorkerSize;
	private int minWorkerSize;
	private int port;
	private TTransportFactory transportFactory;

	private TServer server;

	public QThreadPoolServer(TProcessorFactory processorFactory, TProtocolFactory protocolFactory, int requestTimeout,
			ExecutorService executorService, int maxWorkerSize, int minWorkerSize, int port) {
		this.processorFactory = processorFactory;
		this.protocolFactory = protocolFactory;
		this.requestTimeout = requestTimeout;
		this.executorService = executorService;
		this.maxWorkerSize = maxWorkerSize;
		this.minWorkerSize = minWorkerSize;
		this.port = port;
	}

	public static class Builder {
		private TProcessorFactory processorFactory;
		private TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
		private int requestTimeout = 5; // TimeUnit seconds
		private ExecutorService executorService = null;
		private int maxWorkerSize = 10;
		private int minWorkerSize = 1;
		private int port;
		private TTransportFactory transportFactory = new TFramedTransport.Factory();

		public Builder(TProcessorFactory processorFactory, int port) {
			this.processorFactory = processorFactory;
			this.port = port;
		}

		public Builder protocolFactory(TProtocolFactory protocolFactory) {
			this.protocolFactory = protocolFactory;
			return this;
		}

		public Builder requestTimeout(int requestTimeout) {
			this.requestTimeout = requestTimeout;
			return this;
		}

		public Builder executorService(ExecutorService executorService) {
			this.executorService = executorService;
			return this;
		}

		public Builder maxWorkerSize(int maxWorkerSize) {
			this.maxWorkerSize = maxWorkerSize;
			return this;
		}

		public Builder minWorkerSize(int minWorkerSize) {
			this.minWorkerSize = minWorkerSize;
			return this;
		}
		
		public Builder transportFactory(TTransportFactory transportFactory) {
			this.transportFactory = transportFactory;
			return this;
		}

		public QThreadPoolServer build() {
			return new QThreadPoolServer(this);
		}
	}

	private QThreadPoolServer(Builder builder) {
		this.processorFactory = builder.processorFactory;
		this.protocolFactory = builder.protocolFactory;
		this.requestTimeout = builder.requestTimeout;
		this.executorService = builder.executorService;
		this.maxWorkerSize = builder.maxWorkerSize;
		this.minWorkerSize = builder.minWorkerSize;
		this.port = builder.port;
		this.transportFactory = builder.transportFactory;
	}

	@Override
	public void close() throws Exception {
		server.stop();
	}

	@Override
	public void start() throws TTransportException {
		if (server == null) {
			TServerTransport serverTransport = new TServerSocket(port);
			server = new TThreadPoolServer(new Args(serverTransport).processorFactory(processorFactory)
					.transportFactory(transportFactory).protocolFactory(protocolFactory).requestTimeout(requestTimeout).executorService(executorService)
					.maxWorkerThreads(maxWorkerSize).minWorkerThreads(minWorkerSize));
			
		}
		if (!server.isServing()) {
			server.serve();
		}
	}
}
