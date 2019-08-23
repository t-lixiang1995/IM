package com.pcitc.im.server.init;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pcitc.im.commons.protocol.MessageProto;
import com.pcitc.im.server.config.InitConfiguration;
import com.pcitc.im.server.handle.IMServerHander;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

@Component
public class IMServerInit {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerInit.class);
	
	@Autowired
	private InitConfiguration conf;
	
	@PostConstruct
	public void start() throws Exception{
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						ChannelPipeline pipeline = arg0.pipeline();
						//google protobuf 编解码
						pipeline.addLast(new ProtobufVarint32FrameDecoder());
				        pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
				        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
				        pipeline.addLast(new ProtobufEncoder());
				        
						pipeline.addLast(new IMServerHander());  
					}
				});
			ChannelFuture cf = sb.bind(conf.getNettyPort()).sync();
			if(cf.isSuccess()){
				LOGGER.info("---服务端启动Netty成功 ["+conf.getNettyPort()+"]");
			}
		} catch (Exception e) {
			
		}
	}
}
