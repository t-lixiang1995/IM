package com.pcitc.im.commons.protocol;

import java.io.Serializable;

/**
 * @author pcitc
 * @createTime 2019年3月22日 下午8:15:12
 * 
 */
public class ChatInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6583129438762622022L;
	private String command;
	private Long time;
	private Integer userId;
	private String content;
	
	/**
	 * @param command
	 * @param time
	 * @param userId
	 * @param content
	 */
	public ChatInfo(String command, Long time, Integer userId, String content) {
		super();
		this.command = command;
		this.time = time;
		this.userId = userId;
		this.content = content;
	}
	/**
	 * 
	 */
	public ChatInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
