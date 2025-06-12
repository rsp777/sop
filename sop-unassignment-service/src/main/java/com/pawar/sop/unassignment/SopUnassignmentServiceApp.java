package com.pawar.sop.unassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "com.pawar.sop.unassignment", "com.pawar.sop.http" })
@EnableConfigurationProperties
public class SopUnassignmentServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(SopUnassignmentServiceApp.class, args);
	}
}
