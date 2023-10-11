package com.demo.metamusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestMetaMusicServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(MetaMusicServiceApplication::main).with(TestMetaMusicServiceApplication.class).run(args);
	}

}
