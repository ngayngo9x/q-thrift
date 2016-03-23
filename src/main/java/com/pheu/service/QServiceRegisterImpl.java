package com.pheu.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.librato.disco.DiscoService;

public class QServiceRegisterImpl implements QServiceRegister {
	private CuratorFramework curatorFramework;
	private DiscoService discoService;

	public QServiceRegisterImpl(CuratorFramework curatorFramework, String serviceName) {
		this.curatorFramework = curatorFramework;
		this.discoService = new DiscoService(curatorFramework, serviceName);
	}
	
	public void start() {
		if (!curatorFramework.getState().equals(CuratorFrameworkState.STARTED)) {
			curatorFramework.start();
		}
	}
	
	public void register(String host, int port, boolean addShutdownHook, byte[] payload) throws ServiceDiscoveryException {
		try {
			this.discoService.start(host, port, addShutdownHook, payload);
		} catch (Exception e) {
			throw new ServiceDiscoveryException(e.getMessage());
		}
	}

	public void close() throws ServiceDiscoveryException {
		try {
			this.discoService.stop();
		} catch (Exception e) {
			throw new ServiceDiscoveryException(e.getMessage());
		}
		this.curatorFramework.close();
	}

	public static class Builder {
		private int connectionTimeout;
		private int sessionTimeout;
		private String connectString;
		private int sleepBackoff;
		private int maxRetries;
		private String serviceName;

		public Builder connectionTimeout(int connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}

		public Builder sessionTimeout(int sessionTimeout) {
			this.sessionTimeout = sessionTimeout;
			return this;
		}

		public Builder connectString(String connectString) {
			this.connectString = connectString;
			return this;
		}

		public Builder sleepBackoff(int sleepBackoff) {
			this.sleepBackoff = sleepBackoff;
			return this;
		}

		public Builder maxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
			return this;
		}
		
		public Builder serviceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public QServiceRegisterImpl build() {
			CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString)
					.connectionTimeoutMs(connectionTimeout).sessionTimeoutMs(sessionTimeout)
					.retryPolicy(new ExponentialBackoffRetry(sleepBackoff, maxRetries)).build();
			return new QServiceRegisterImpl(curatorFramework, serviceName);
		}
	}

}
