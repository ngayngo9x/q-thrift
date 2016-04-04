package com.pheu.thrift.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.facebook.nifty4.core.NiftyDispatcher;
//import com.facebook.nifty4.core.ThriftFrameDecoder;
//import com.facebook.nifty4.core.ThriftServerDef;

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

public class QNetty4ThriftServer {
//	private static final Logger log = LoggerFactory.getLogger(QNetty4ThriftServer.class);
//
//	private int workerSize = 10;
//	private int bossSize = 5;
//	private LogLevel logLevel = LogLevel.INFO;
//	private ThriftServerDef def;
//	private int backLog = 128;
//	private boolean keepAlive = true;
//
//	public void start() throws InterruptedException {
//
//		EventLoopGroup workerGroup = new NioEventLoopGroup(workerSize);
//		EventLoopGroup bossGroup = new NioEventLoopGroup(bossSize);
//
//		try {
//			ServerBootstrap b = new ServerBootstrap();
//			b.group(bossGroup, workerGroup);
//			b.channel(NioServerSocketChannel.class);
//			b.handler(new LoggingHandler(logLevel));
//			b.childHandler(new ChannelInitializer<SocketChannel>() {
//
//				@Override
//				public void initChannel(SocketChannel ch) throws Exception {
//
//					//                    TMultiplexedProcessor multiprocessor = new TMultiplexedProcessor();
//					//
//					//                    multiprocessor.registerProcessor("Test", new TestThriftService.Processor<>(new EchoHandler(port)));
//
//					ChannelPipeline pipeline = ch.pipeline();
//
//					pipeline.addLast("frameDecoder",
//							new ThriftFrameDecoder(def.getMaxFrameSize(), def.getInProtocolFactory()));
//
//					pipeline.addLast("dispatcher", new NiftyDispatcher(def));
//
//				}
//			});
//			b.option(ChannelOption.SO_BACKLOG, backLog);
//			b.childOption(ChannelOption.SO_KEEPALIVE, keepAlive);
//			log.debug("configuration serverBootstrap");
//
//			if (log.isInfoEnabled()) {
//				log.info("Start server with port: {} ", def.getServerPort());
//			} else if (log.isWarnEnabled()) {
//				log.warn("Start server with port: {} ", def.getServerPort());
//			} else if (log.isErrorEnabled()) {
//				log.error("Start server with port: {} ", def.getServerPort());
//			}
//			b.bind(def.getServerPort()).sync().channel().closeFuture().sync().channel();
//
//		} finally {
//			workerGroup.shutdownGracefully();
//			bossGroup.shutdownGracefully();
//		}
//
//	}
//
//	public static class Builder {
//		private int workerSize;
//		private int bossSize;
//		private LogLevel logLevel;
//		private ThriftServerDef def;
//		private int backLog;
//		private boolean keepAlive;
//
//		public Builder(ThriftServerDef def) {
//			this.def = def;
//		}
//		
//		public Builder workerSize(int workerSize) {
//			this.workerSize = workerSize;
//			return this;
//		}
//
//		public Builder bossSize(int bossSize) {
//			this.bossSize = bossSize;
//			return this;
//		}
//
//		public Builder logLevel(LogLevel logLevel) {
//			this.logLevel = logLevel;
//			return this;
//		}
//
//		public Builder withThriftServerDef(ThriftServerDef def) {
//			this.def = def;
//			return this;
//		}
//
//		public Builder backLog(int backLog) {
//			this.backLog = backLog;
//			return this;
//		}
//
//		public Builder keepAlive(boolean keepAlive) {
//			this.keepAlive = keepAlive;
//			return this;
//		}
//
//		public QNetty4ThriftServer build() {
//			return new QNetty4ThriftServer(this);
//		}
//	}
//
//	private QNetty4ThriftServer(Builder builder) {
//		this.workerSize = builder.workerSize;
//		this.bossSize = builder.bossSize;
//		this.logLevel = builder.logLevel;
//		this.def = builder.def;
//		this.backLog = builder.backLog;
//		this.keepAlive = builder.keepAlive;
//	}
}
