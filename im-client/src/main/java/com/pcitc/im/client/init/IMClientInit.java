package com.pcitc.im.client.init;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pcitc.im.client.config.InitConfiguration;
import com.pcitc.im.client.handle.IMClientHandler;

import com.pcitc.im.commons.constant.MessageConstant;
import com.pcitc.im.commons.pojo.ServerInfo;
import com.pcitc.im.commons.protocol.ChatInfo;
import com.pcitc.im.commons.protocol.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
public class IMClientInit {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IMClientInit.class);
	public IMClientInit() {
		LOGGER.info("=========IMClientInit init");
	}

	public Channel channel;
	private ServerInfo server; 
	@Autowired
	private OkHttpClient okHttpClient;
	@Autowired
	private InitConfiguration conf;
	
	@PostConstruct
	public void start() throws Exception{
		if(server != null){
			LOGGER.info("--客户端当前是登录状态！");
			return;
		}
		//1.从Route得到服务端的IP+port  
		getServerInfo();
		//2.启动服务
		startClient();
		//3.登录到服务端（服务端保存UserId与Channel的映射关系）
		registerToServer();
	}

	private void registerToServer() {
		MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
				.setCommand(MessageConstant.LOGIN)
				.setTime(System.currentTimeMillis())
				.setUserId(conf.getUserId())
				.setContent("login").build();
		channel.writeAndFlush(message);
	}

	/**
	 * 从Route得到服务端的IP+port  
	 **/
	private void getServerInfo() {
		try {
			JSONObject json = new JSONObject();
			json.put("userId", conf.getUserId());
			json.put("userName", conf.getUserName());
			
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody requestBody = RequestBody.create(mediaType, json.toString());
			Request request = new Request.Builder()
					.url(conf.getRouteLoginUrl())
					.post(requestBody)
					.build();
			Response response = okHttpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("---客户端获取server节点失败！");
				throw new IOException("---客户端获取server节点失败！");
			}
			ResponseBody body = response.body();
			try {
				String responseJson = body.string();
				this.server = JSON.parseObject(responseJson, ServerInfo.class);
				LOGGER.info("--得到服务端Server节点   port:"+server.getNeetyPort());
			} finally {
				body.close();
				response.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void startClient() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						ChannelPipeline pipeline = arg0.pipeline();
						//google protobuf 编解码
						pipeline.addLast(new ProtobufVarint32FrameDecoder());
				        pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
				        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
				        pipeline.addLast(new ProtobufEncoder());
				        
						pipeline.addLast(new IMClientHandler());
					}
				});
			ChannelFuture cf = bs.connect(server.getIp(), server.getNeetyPort()).sync();
			if(cf.isSuccess()){
				LOGGER.info("---客户端启动成功，连接的是===port："+server.getNeetyPort());
				channel = cf.channel();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//客户端通过调用路由端的API，进行消息的发送
	public void sendMessage(ChatInfo chat){
		try {
			JSONObject json = new JSONObject();
			json.put("command", chat.getCommand());
			json.put("time", chat.getTime());
			json.put("userId", chat.getUserId());
			json.put("content", chat.getContent());
			
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody requestBody = RequestBody.create(mediaType, json.toString());
			Request request = new Request.Builder()
					.url(conf.getRouteChatUrl())
					.post(requestBody)
					.build();
			Response response = okHttpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("---客户端调用Route chat api失败！");
				throw new IOException("---客户端调用Route chat api失败！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clear() {
		//调用路由端 清除缓存数据
		logoutRoute();
		//调用服务端清除channel数据
		logoutServer();
		
		server = null; 
	}
	
	/**
	 * 调用服务端清理数据
	 **/
	private void logoutServer() {
		try {
			JSONObject json = new JSONObject();
			json.put("userId", conf.getUserId());
			
			MediaType mediaType = MediaType.parse("application/json");
			okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
			Request request = new Request.Builder()
					.url("http://"+server.getIp()+":"+server.getHttpPort()+"/clientLogout") 
					.post(requestBody)
					.build();
			Response response = okHttpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("---客户端调用服务端 clientLogout失败");
				throw new IOException("---客户端调用服务端 clientLogout失败");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 调用路由端清理数据
	 **/
	private void logoutRoute() {
		try {
			JSONObject json = new JSONObject();
			json.put("userId", conf.getUserId());
			
			MediaType mediaType = MediaType.parse("application/json");
			okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
			Request request = new Request.Builder()
					.url(conf.getRouteLogoutUrl())
					.post(requestBody)
					.build();
			Response response = okHttpClient.newCall(request).execute();
			if(!response.isSuccessful()){
				LOGGER.error("---客户端调用路由 logout失败");
				throw new IOException("---客户端调用路由 logout失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void restart() throws Exception {
		//清理客户端信息（路由）
		logoutRoute();
		server = null;
		start();
	}
}
