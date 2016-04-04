package com.pheu.thrift.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.nifty.core.NiftyDispatcher;
import com.facebook.nifty.core.ThriftFrameDecoder;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.nifty.core.ThriftServerDefBuilder;
import com.librato.disco.DiscoService;
import com.pheu.thrift.example.EchoHandler;
import com.pheu.thrift.example.TestThriftService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class QNettyThriftServer {
	private static final Logger log = LoggerFactory.getLogger(QNettyThriftServer.class);
	
	public void start(int port) throws InterruptedException {
		
		EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.DEBUG));
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {

//                    TMultiplexedProcessor multiprocessor = new TMultiplexedProcessor();
//
//                    multiprocessor.registerProcessor("Test", new TestThriftService.Processor<>(new EchoHandler(port)));

                    ThriftServerDef def = new ThriftServerDefBuilder()
                    		.withProcessor(new TestThriftService.Processor<>(new EchoHandler(port)))
                    		.speaks(new TCompactProtocol.Factory())
                    		.build();

                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast("frameDecoder", new ThriftFrameDecoder(def.getMaxFrameSize(), def.getInProtocolFactory()));

                    pipeline.addLast("dispatcher", new NiftyDispatcher(def));

                }
            });
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            log.debug("configuration serverBootstrap");

            if (log.isInfoEnabled()) {
                log.info("Start server with port: {} ", port);
            } else if (log.isWarnEnabled()) {
                log.warn("Start server with port: {} ", port);
            } else if (log.isErrorEnabled()) {
                log.error("Start server with port: {} ", port);
            }
            b.bind(port).sync().channel().closeFuture().sync().channel();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
		
	}
	
	public static void main(String[] args) throws Exception {
		
		CuratorFramework framework = CuratorFrameworkFactory.builder()
				.connectionTimeoutMs(1000)
				.connectString("localhost:2181")
				.retryPolicy(new ExponentialBackoffRetry(1000, 5))
				.build();
		DiscoService service = new DiscoService(framework, "qthriftserverice");
		framework.start();
		
		int port = 9092;
		
		QNettyThriftServer server = new QNettyThriftServer();
		service.start("localhost", port, true, "".getBytes());
		server.start(port);
		
	}
	
}
