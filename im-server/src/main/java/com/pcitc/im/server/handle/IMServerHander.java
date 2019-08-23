package com.pcitc.im.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.pcitc.im.commons.constant.MessageConstant;
import com.pcitc.im.commons.protocol.MessageProto;
import com.pcitc.im.server.config.InitConfiguration;
import com.pcitc.im.server.config.SpringBeanFactory;
import com.pcitc.im.server.zk.ZKUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class IMServerHander extends ChannelInboundHandlerAdapter  {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerHander.class);
	
	private final AttributeKey<Integer> uid = AttributeKey.valueOf("userId");
	private ChannelMap map = ChannelMap.newInstance();
	
	private InitConfiguration conf;
	private OkHttpClient okHttpClient;
	private ZKUtil zk;

	public IMServerHander() {
		conf = SpringBeanFactory.getBean(InitConfiguration.class);
		okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
		this.zk = SpringBeanFactory.getBean(ZKUtil.class);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;
		String ip= InetAddress.getLocalHost().getHostAddress(); //得到IP地址
		String path = conf.getRoot() +"/"+ip+"-"+conf.getNettyPort()+"-"+conf.getHttpPort();

		if(message.getCommand().equals(MessageConstant.LOGIN)){
			//系统指令：登录
			//保存channel到Map中
			
			ctx.channel().attr(uid).set(message.getUserId()); //用户登录时，绑定一个userId属性在Channel
			map.putClient(message.getUserId(), ctx.channel());
			LOGGER.info("---客户端登录成功。userId:"+message.getUserId());
		}else{
			//聊天
			LOGGER.info("---服务端接收到数据："+message.getContent()+"，发送人："+message.getUserId());
		}
		zk.insertContent(path,message.getContent());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 客户端强制下线
		Integer userId = ctx.channel().attr(uid).get();
		map.getChannelMap().remove(userId); //从服务端删除
		
		JSONObject json = new JSONObject();
		json.put("userId", userId);
		
		MediaType mediaType = MediaType.parse("application/json");
		okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
		Request request = new Request.Builder()
				.url(conf.getRouteLogoutUrl())
				.post(requestBody)
				.build();
		Response response = okHttpClient.newCall(request).execute();
		if(!response.isSuccessful()){
			LOGGER.error("---服务端调用路由 logout失败");
			throw new IOException("---服务端调用路由 logout失败");
		}
		
		super.channelInactive(ctx);
	}
}
