package com.pcitc.im.route.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author pcitc
 * @createTime 2019年3月17日 下午10:00:33
 * 
 */
@Component
public class InitConfiguration {

	@Value("${im.zk.switch}")
	private boolean zkSwitch;
	@Value("${im.zk.addr}")
	private String addr;
	@Value("${im.zk.root}")
	private String root;
	
	public boolean isZkSwitch() {
		return zkSwitch;
	}
	public void setZkSwitch(boolean zkSwitch) {
		this.zkSwitch = zkSwitch;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	
}
