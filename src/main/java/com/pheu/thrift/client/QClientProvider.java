package com.pheu.thrift.client;

public interface QClientProvider {

	<T> T createClient();
	
}
