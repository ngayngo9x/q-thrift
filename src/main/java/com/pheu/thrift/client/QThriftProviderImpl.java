package com.pheu.thrift.client;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.github.phantomthief.thrift.client.pool.impl.DefaultThriftConnectionPoolImpl;
import com.google.common.base.Preconditions;
import com.pheu.service.QServiceDiscover;
import com.pheu.service.QServiceDiscoverImpl;

public class QThriftProviderImpl implements QThriftConnectionProvider {

	private ThriftConnectionPoolProvider poolProvider;
	private QServiceDiscover serverInfoProvider;
	//private QProtocolFactory<T> protocalProvider;
	private QSelectorStrategy selectorStrategy;

	public QThriftProviderImpl(ThriftConnectionPoolProvider poolProvider, QServiceDiscover serverInfoProvider,
			QSelectorStrategy selectorStrategy) {
		this.poolProvider = poolProvider;
		this.serverInfoProvider = serverInfoProvider;
		//this.protocalProvider = protocalProvider;
		this.selectorStrategy = selectorStrategy;
	}

	public ThriftConnectionPoolProvider getPoolProvider() {
		return poolProvider;
	}

	public QServiceDiscover getServiceDiscoverProvider() {
		return serverInfoProvider;
	}

//	public QProtocolFactory<T> getProtocalProvider() {
//		return protocalProvider;
//	}

	public QSelectorStrategy getSelectorStrategy() {
		return selectorStrategy;
	}

	public static class Builder {
		private ThriftConnectionPoolProvider poolProvider = DefaultThriftConnectionPoolImpl.getInstance();
		private QServiceDiscover serviceDiscoveryProvider = new QServiceDiscoverImpl.Builder().build();
		private QSelectorStrategy selectorStrategy = new QRoundRobinSelectorStrategy();

		public Builder withPoolProvider(ThriftConnectionPoolProvider poolProvider) {
			this.poolProvider = poolProvider;
			return this;
		}

		public Builder serviceDiscoveryProvider(QServiceDiscover serverInfoProvider) {
			this.serviceDiscoveryProvider = serverInfoProvider;
			return this;
		}

		public Builder withSelectorStrategy(QSelectorStrategy selectorStrategy) {
			this.selectorStrategy = selectorStrategy;
			return this;
		}

		public QThriftProviderImpl build() {
			Preconditions.checkNotNull(this.poolProvider);
			Preconditions.checkNotNull(this.serviceDiscoveryProvider);
			//Preconditions.checkNotNull(this.protocalProvider);
			Preconditions.checkNotNull(this.selectorStrategy);
			return new QThriftProviderImpl(this.poolProvider, this.serviceDiscoveryProvider, 
					this.selectorStrategy);
		}
	}

	
}
