package com.pheu.service;

public interface QServiceRegister {
	public void start();
	public void register(String host, int port, boolean addShutdownHook, byte[] payload) throws ServiceDiscoveryException;
	public void close() throws ServiceDiscoveryException;
}
