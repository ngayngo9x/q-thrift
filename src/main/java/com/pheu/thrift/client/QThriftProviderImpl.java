package com.pheu.thrift.client;

import com.github.phantomthief.thrift.client.pool.ThriftConnectionPoolProvider;
import com.google.common.base.Preconditions;
import com.pheu.service.QServiceDiscover;

public class QThriftProviderImpl<P> implements QThriftConnectionProvider<P> {

	private ThriftConnectionPoolProvider poolProvider;
	private QServiceDiscover<P> serverInfoProvider;
	//private QProtocolFactory<T> protocalProvider;
	private QSelectorStrategy selectorStrategy;

	public QThriftProviderImpl(ThriftConnectionPoolProvider poolProvider, QServiceDiscover<P> serverInfoProvider,
			QSelectorStrategy selectorStrategy) {
		this.poolProvider = poolProvider;
		this.serverInfoProvider = serverInfoProvider;
		//this.protocalProvider = protocalProvider;
		this.selectorStrategy = selectorStrategy;
	}

	public ThriftConnectionPoolProvider getPoolProvider() {
		return poolProvider;
	}

	public QServiceDiscover<P> getServiceDiscoverProvider() {
		return serverInfoProvider;
	}

//	public QProtocolFactory<T> getProtocalProvider() {
//		return protocalProvider;
//	}

	public QSelectorStrategy getSelectorStrategy() {
		return selectorStrategy;
	}

	public static class Builder<P> {
		private ThriftConnectionPoolProvider poolProvider;
		private QServiceDiscover<P> serverInfoProvider;
		//private QProtocolFactory<T> protocalProvider;
		private QSelectorStrategy selectorStrategy;

		public Builder<P> withPoolProvider(ThriftConnectionPoolProvider poolProvider) {
			this.poolProvider = poolProvider;
			return this;
		}

		public Builder<P> serviceDiscoveryProvider(QServiceDiscover<P> serverInfoProvider) {
			this.serverInfoProvider = serverInfoProvider;
			return this;
		}

//		public Builder<T, P> withProtocalProvider(QProtocolFactory<T> protocalProvider) {
//			this.protocalProvider = protocalProvider;
//			return this;
//		}

		public Builder<P> withSelectorStrategy(QSelectorStrategy selectorStrategy) {
			this.selectorStrategy = selectorStrategy;
			return this;
		}

		public QThriftProviderImpl<P> build() {
			Preconditions.checkNotNull(this.poolProvider);
			Preconditions.checkNotNull(this.serverInfoProvider);
			//Preconditions.checkNotNull(this.protocalProvider);
			Preconditions.checkNotNull(this.selectorStrategy);
			return new QThriftProviderImpl<P>(this.poolProvider, this.serverInfoProvider, 
					this.selectorStrategy);
		}
	}

	
}
