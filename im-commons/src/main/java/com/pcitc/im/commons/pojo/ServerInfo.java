package com.pcitc.im.commons.pojo;

import java.io.Serializable;

/**
 * @author pcitc
 * @createTime 2019年3月20日 下午8:28:00
 * 封装Server节点的信息
 */
public class ServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1610803139396676632L;
	private String ip;
	private Integer neetyPort;
	private Integer httpPort;
	
	/**
	 * @param ip
	 * @param neetyPort
	 * @param httpPort
	 */
	public ServerInfo(String ip, Integer neetyPort, Integer httpPort) {
		super();
		this.ip = ip;
		this.neetyPort = neetyPort;
		this.httpPort = httpPort;
	}
	/**
	 * 
	 */
	public ServerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getNeetyPort() {
		return neetyPort;
	}
	public void setNeetyPort(Integer neetyPort) {
		this.neetyPort = neetyPort;
	}
	public Integer getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(Integer httpPort) {
		this.httpPort = httpPort;
	}
	
}
