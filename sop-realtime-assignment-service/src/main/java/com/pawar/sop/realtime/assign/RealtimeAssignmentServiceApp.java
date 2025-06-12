package com.pawar.sop.realtime.assign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "com.pawar.sop.realtime.assign", "com.pawar.sop.http" })
@EnableConfigurationProperties
public class RealtimeAssignmentServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(RealtimeAssignmentServiceApp.class, args);
	}
}