package com.pcitc.im.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author pcitc
 * @createTime 2019年3月17日 下午9:47:14
 * 初始化一些参数
 */
@Component
public class InitConfiguration {

	private final static Logger LOGGER = LoggerFactory.getLogger(InitConfiguration.class);
	public InitConfiguration() {
		LOGGER.info("=========InitConfiguration init");
	}
	@Value("${im.user.userId}")
	private Integer userId;
	@Value("${im.user.userName}")
	private String userName;
	@Value("${im.route.login}")
	private String routeLoginUrl;
	@Value("${im.route.chat}")
	private String routeChatUrl;
	@Value("${im.route.logout}")
	private String routeLogoutUrl;
	
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
	public String getRouteLoginUrl() {
		return routeLoginUrl;
	}
	public void setRouteLoginUrl(String routeLoginUrl) {
		this.routeLoginUrl = routeLoginUrl;
	}
	public String getRouteChatUrl() {
		return routeChatUrl;
	}
	public void setRouteChatUrl(String routeChatUrl) {
		this.routeChatUrl = routeChatUrl;
	}
	public String getRouteLogoutUrl() {
		return routeLogoutUrl;
	}
	public void setRouteLogoutUrl(String routeLogoutUrl) {
		this.routeLogoutUrl = routeLogoutUrl;
	}
	
}
