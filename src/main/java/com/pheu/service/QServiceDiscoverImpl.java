package com.pheu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.google.common.base.Optional;
import com.librato.disco.Decoder;
import com.librato.disco.DiscoClient;
import com.librato.disco.Node;
import com.librato.disco.RandomSelectorStrategy;

public class QServiceDiscoverImpl<P> implements QServiceDiscover<P> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QServiceDiscoverImpl.class);

	private CuratorFramework curatorFramework;
	private DiscoClient<P> clientDiscovery;

	public QServiceDiscoverImpl(CuratorFramework curatorFramework, DiscoClient<P> clientDiscovery) {
		this.curatorFramework = curatorFramework;
		this.clientDiscovery = clientDiscovery;
	}

	public QServiceDiscoverImpl(CuratorFramework curatorFramework, String serviceName, Decoder<P> decoder) {
		this(curatorFramework, new DiscoClient<>(curatorFramework, serviceName, new RandomSelectorStrategy(), decoder));
	}

	public void start() throws ServiceDiscoveryException {
		if (!curatorFramework.getState().equals(CuratorFrameworkState.STARTED)) {
			curatorFramework.start();
		}
		if (!clientDiscovery.isStarted()) {
			try {
				clientDiscovery.start();
			} catch (Exception e) {
				throw new ServiceDiscoveryException(e.getMessage());
			}
		}
	}

	public void close() throws ServiceDiscoveryException {
		try {
			clientDiscovery.stop();
		} catch (IOException e) {
			throw new ServiceDiscoveryException(e.getMessage());
		}
		curatorFramework.close();
	}

	public List<ThriftServerInfo> allServices() {
		List<ThriftServerInfo> result = new ArrayList<>();
		List<Node<P>> allNodes = clientDiscovery.getAllNodes();
		for (Node<P> node : allNodes) {
			result.add(ThriftServerInfo.of(node.host, node.port));
		}
		return result;
	}

	public Optional<Node<P>> nextService() {
		return clientDiscovery.getServiceNode();
	}

	public static class Builder<T> {
		private String serviceName;
		private Decoder<T> decoder = new Decoder<T>() {

			@Override
			public T decode(byte[] bytes) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void handleException(Exception ex) {
				LOGGER.error(ex.getMessage());
			}
		};
		private int connectionTimeout = 1000;
		private int sessionTimeout = 1000;
		private String connectString;
		private int sleepBackoff = 200;
		private int maxRetries = 5;

		public Builder<T> serviceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public Builder<T> decoder(Decoder<T> decoder) {
			this.decoder = decoder;
			return this;
		}

		public Builder<T> connectionTimeout(int connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}

		public Builder<T> sessionTimeout(int sessionTimeout) {
			this.sessionTimeout = sessionTimeout;
			return this;
		}

		public Builder<T> connectString(String connectString) {
			this.connectString = connectString;
			return this;
		}

		public Builder<T> sleepBackoff(int sleepBackoff) {
			this.sleepBackoff = sleepBackoff;
			return this;
		}

		public Builder<T> maxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
			return this;
		}

		public QServiceDiscoverImpl<T> build() {
			CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString)
					.connectionTimeoutMs(connectionTimeout).sessionTimeoutMs(sessionTimeout)
					.retryPolicy(new ExponentialBackoffRetry(sleepBackoff, maxRetries)).build();
			return new QServiceDiscoverImpl<>(curatorFramework, serviceName, decoder);
		}
	}

}