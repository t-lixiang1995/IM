package com.pcitc.im.client.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcitc.im.client.config.SpringBeanFactory;
import com.pcitc.im.client.init.IMClientInit;

import com.pcitc.im.commons.protocol.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author pcitc
 * @createTime 2019年3月17日 下午8:27:13
 * 
 */
public class IMClientHandler extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMClientHandler.class);
	private IMClientInit client;
	
	/**
	 * IMClientInit start(@PostConstruct) 方法中，调用new IMClientHandler， 此时IMClientInit在Spring中还未完成实例化过程， 如果在此时从Spring容器中获取IMClientInit实例，会导致循环依赖的死锁情况。
	 **/
	public IMClientHandler() {
		LOGGER.info("--IMClientHandler init");
		//client = SpringBeanFactory.getBean(IMClientInit.class);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;
		LOGGER.info("---客户端接收到消息："+message.getContent());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		client = SpringBeanFactory.getBean(IMClientInit.class);
		//服务端强制下线事件
		client.restart();
		super.channelInactive(ctx);
	}
}
