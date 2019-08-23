package com.pcitc.im.server.zk;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcitc.im.server.config.InitConfiguration;
import com.pcitc.im.server.config.SpringBeanFactory;

/**
 * @author pcitc
 * @createTime 2019年3月17日 下午10:06:20
 * 
 */
public class RegisterToZK implements Runnable {

	private final static Logger LOGGER = LoggerFactory.getLogger(RegisterToZK.class);
	
	private InitConfiguration conf;
	private ZKUtil zk;
	
	public RegisterToZK() {
		this.conf = SpringBeanFactory.getBean(InitConfiguration.class);
		this.zk = SpringBeanFactory.getBean(ZKUtil.class);
	}
	
	@Override
	public void run() {
		try {
			//把这个三个参数， 注册到ZK上
			String ip=InetAddress.getLocalHost().getHostAddress(); //得到IP地址
			Integer httpPort = conf.getHttpPort();
			Integer nettyPort = conf.getNettyPort();
			LOGGER.info("--服务端准备注册到Zookeeper中， IP :"+ip+"; HttpPort:"+httpPort+"; nettyPort:"+nettyPort); 
			//尝试创建根节点
			zk.createRootNode();
			
			//判断是否需要注册到ZK上
			if(conf.isZkSwitch()){
				String path = conf.getRoot() +"/"+ip+"-"+nettyPort+"-"+httpPort;
				zk.createNode(path);
				LOGGER.info("--服务端注册成功");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
