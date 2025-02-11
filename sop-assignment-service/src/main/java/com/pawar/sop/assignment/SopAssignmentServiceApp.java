package com.pawar.sop.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(scanBasePackages = { "com.pawar.sop.assignment", "com.pawar.sop.http" })
@EnableConfigurationProperties
public class SopAssignmentServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(SopAssignmentServiceApp.class, args);
	}

}