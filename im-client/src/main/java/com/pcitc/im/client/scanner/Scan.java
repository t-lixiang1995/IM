package com.pcitc.im.client.scanner;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcitc.im.client.config.InitConfiguration;
import com.pcitc.im.client.config.SpringBeanFactory;
import com.pcitc.im.client.init.IMClientInit;

import com.pcitc.im.commons.constant.MessageConstant;
import com.pcitc.im.commons.protocol.ChatInfo;
import com.pcitc.im.commons.utils.StringUtil;

public class Scan implements Runnable {

	private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);
	
	private IMClientInit client;
	private InitConfiguration conf;
	
	public Scan(){
		this.client = SpringBeanFactory.getBean(IMClientInit.class);
		this.conf = SpringBeanFactory.getBean(InitConfiguration.class);
	}
	
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		try {
			while(true){
				String msg = scan.nextLine();
				if(StringUtil.isEmpty(msg)){
					LOGGER.info("---不允许发送空消息！");
					continue;
				}
				
				if(msg.equals(MessageConstant.LOGOUT)){
					//客户端主动下线
					client.clear();
					LOGGER.info("--客户端下线成功，如果需要加入聊天室，请重新登录");
					continue;
				}
				if(msg.equals(MessageConstant.LOGIN)){
					//客户端主动登录
					try {
						client.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					LOGGER.info("--客户端重新登录成功");
					continue;
				}
				
				//chat  api
				ChatInfo chat = new ChatInfo(MessageConstant.CHAT,System.currentTimeMillis(),conf.getUserId(),msg);
				client.sendMessage(chat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}

}
