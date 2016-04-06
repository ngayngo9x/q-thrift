package com.pheu.thrift.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.util.HashedWheelTimer;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.NettyServerConfigBuilder;
import com.facebook.nifty.core.NettyServerTransport;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.nifty.core.ThriftServerDefBuilder;
import com.pheu.thrift.example.EchoHandler;
import com.pheu.thrift.example.TestThriftService;

public class QNetty3ThriftServer {

	public static void start(int port) {
		
		// Create the handler
	    TestThriftService.Iface serviceInterface = new EchoHandler(port);

	    // Create the processor
	    TProcessor processor = new TestThriftService.Processor<>(serviceInterface);

	    // Build the server definition
	    ThriftServerDef serverDef = new ThriftServerDefBuilder().listen(port).protocol(new TCompactProtocol.Factory()).withProcessor(processor)
	                                                            .build();

	    // Create the server transport
	    final NettyServerTransport server = new NettyServerTransport(
	    		serverDef, 
	    		NettyServerConfig.newBuilder().build(), 
	    		new DefaultChannelGroup());

	    // Start the server
	    server.start();
		
	}
	
}
