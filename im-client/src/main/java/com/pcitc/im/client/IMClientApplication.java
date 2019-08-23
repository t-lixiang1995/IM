package com.pcitc.im.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pcitc.im.client.scanner.Scan;

/**
 * @author pcitc
 * @createTime 2019年3月17日 下午8:10:30
 * 
 */
@SpringBootApplication
public class IMClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(IMClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Thread thread = new Thread(new Scan());
		thread.setName("im-client-thread"); //程序出错的时候，通过线程名称可以快速定位问题
		thread.start();
	}
}
