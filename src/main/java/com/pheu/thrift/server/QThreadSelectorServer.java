package com.pheu.thrift.server;

import java.util.concurrent.ExecutorService;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args;
import org.apache.thrift.server.TThreadedSelectorServer.Args.AcceptPolicy;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

public class QThreadSelectorServer implements QThriftServer {

	private int port;
	private int selectorSize;
	private int acceptQueueSizePerThread;
	private AcceptPolicy acceptPolicy;
	private ExecutorService executorService;
	private TProcessorFactory processorFactory;
	private TProtocolFactory protocolFactory;
	private int workerThreadSize;

	private TServer server;

	@Override
	public void close() throws Exception {
		server.stop();
	}

	@Override
	public void start() throws TTransportException {
		if (server == null) {
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
			server = new TThreadedSelectorServer(new Args(serverTransport).selectorThreads(selectorSize)
					.acceptQueueSizePerThread(acceptQueueSizePerThread).acceptPolicy(acceptPolicy)
					.executorService(executorService).processorFactory(processorFactory)
					.protocolFactory(protocolFactory).workerThreads(workerThreadSize));
		}
		if (!server.isServing()) {
			server.serve();
		}
	}

	public static class Builder {
		private int port;
		private int selectorSize = 1;
		private int acceptQueueSizePerThread = 1000;
		private AcceptPolicy acceptPolicy = AcceptPolicy.FAIR_ACCEPT;
		private ExecutorService executorService = null;
		private TProcessorFactory processorFactory;
		private TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
		private int workerThreadSize = 10;
		
		private TServer server;

		public Builder(TProcessorFactory processorFactory, int port) {
			this.processorFactory = processorFactory;
			this.port = port;
		}

		public Builder selectorSize(int selectorSize) {
			this.selectorSize = selectorSize;
			return this;
		}

		public Builder acceptQueueSizePerThread(int acceptQueueSizePerThread) {
			this.acceptQueueSizePerThread = acceptQueueSizePerThread;
			return this;
		}

		public Builder acceptPolicy(AcceptPolicy acceptPolicy) {
			this.acceptPolicy = acceptPolicy;
			return this;
		}

		public Builder executorService(ExecutorService executorService) {
			this.executorService = executorService;
			return this;
		}

		public Builder processorFactory(TProcessorFactory processorFactory) {
			this.processorFactory = processorFactory;
			return this;
		}

		public Builder protocolFactory(TProtocolFactory protocolFactory) {
			this.protocolFactory = protocolFactory;
			return this;
		}

		public Builder workerThreadSize(int workerThreadSize) {
			this.workerThreadSize = workerThreadSize;
			return this;
		}

		public Builder server(TServer server) {
			this.server = server;
			return this;
		}

		public QThreadSelectorServer build() {
			return new QThreadSelectorServer(this);
		}
	}

	private QThreadSelectorServer(Builder builder) {
		this.port = builder.port;
		this.selectorSize = builder.selectorSize;
		this.acceptQueueSizePerThread = builder.acceptQueueSizePerThread;
		this.acceptPolicy = builder.acceptPolicy;
		this.executorService = builder.executorService;
		this.processorFactory = builder.processorFactory;
		this.protocolFactory = builder.protocolFactory;
		this.workerThreadSize = builder.workerThreadSize;
		this.server = builder.server;
	}
}
