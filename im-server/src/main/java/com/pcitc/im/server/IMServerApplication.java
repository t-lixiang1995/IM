package com.pcitc.im.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pcitc.im.server.zk.RegisterToZK;

@SpringBootApplication
public class IMServerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(IMServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Thread thread = new Thread(new RegisterToZK());
		thread.setName("im-server-regist2ZK-thread");
		thread.start();
	}
}
