package com.pcitc.im.server.controller;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pcitc.im.commons.constant.MessageConstant;
import com.pcitc.im.commons.pojo.UserInfo;
import com.pcitc.im.commons.protocol.ChatInfo;
import com.pcitc.im.commons.protocol.MessageProto;
import com.pcitc.im.server.handle.ChannelMap;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author pcitc
 * @createTime 2019年3月22日 下午8:32:57
 * 
 */
@RestController
@RequestMapping("/")
public class IMServerController {
	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerController.class);
	private ChannelMap CHANNEL_MAP = ChannelMap.newInstance();
	private final AttributeKey<Integer> uid = AttributeKey.valueOf("userId");
	
	/**
	 * 服务端接收消息， 推送到指定的客户端
	 **/
	@RequestMapping(value="/pushMessage", method=RequestMethod.POST)
	public void pushMessage(@RequestBody ChatInfo chat){
		//1.消息封装为protobuf对象
		MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
				.setCommand(chat.getCommand())
				.setTime(chat.getTime())
				.setUserId(chat.getUserId())
				.setContent(chat.getContent()).build();
		
		//2.ChannelMap得到所有的与该服务端连接的客户端Channel
		if(MessageConstant.CHAT.equals(chat.getCommand())){
			for (Entry<Integer, Channel> entry : CHANNEL_MAP.getChannelMap().entrySet()) {
				//过滤自己
				if(!chat.getUserId().equals(entry.getKey())){
					
					entry.getValue().writeAndFlush(message);//向客户端发送消息
					LOGGER.info("---服务端向客户端["+entry.getValue().attr(uid).get()+"]发送了消息，消息来自userId:"+chat.getUserId());
				}
			}
		}
	}
	
	/**
	 * 服务端处理客户端下线事件
	 **/
	@RequestMapping(value="/clientLogout", method=RequestMethod.POST)
	public void clientLogout(@RequestBody UserInfo user){
		CHANNEL_MAP.getChannelMap().remove(user.getUserId());
		LOGGER.info("---服务端处理客户端下线【"+user.getUserId()+"】");
	}
}
