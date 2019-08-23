package com.pcitc.im.server.handle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * @author pcitc
 * @createTime 2019年3月20日 下午9:59:52
 * 保存所有与本节点 连接的 client Channel
 */
public class ChannelMap {

	//多线程保障可见性  禁止指令重排 
	private static volatile ChannelMap instance;
	private final Map<Integer, Channel> CHANNEL_MAP = new ConcurrentHashMap<Integer, Channel>();
	
	private ChannelMap(){
	}
	
	public static ChannelMap newInstance(){
		if(instance==null){
			synchronized(ChannelMap.class){
				if(instance==null){
					instance = new ChannelMap();
				}
			}
		}
		return instance;
	}
	
	public Map<Integer, Channel> getChannelMap(){
		return CHANNEL_MAP;
	}
	public void putClient(Integer userId, Channel channel){
		CHANNEL_MAP.put(userId, channel);
	}
	public Channel getClient(Integer userId){
		return CHANNEL_MAP.get(userId);
	}
}
