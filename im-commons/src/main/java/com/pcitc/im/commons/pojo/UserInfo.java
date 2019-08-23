package com.pcitc.im.commons.pojo;

import java.io.Serializable;

/**
 * @author pcitc
 * @createTime 2019年3月20日 下午8:27:52
 * 封装用户信息
 */
public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3675218087959272814L;
	private Integer userId;
	private String userName;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
