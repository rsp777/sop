package com.pawar.sop.realtime.unassign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "com.pawar.sop.realtime.unassign", "com.pawar.sop.http" })
@EnableConfigurationProperties
public class RealtimeUnassignmentServiceApp
{
	public static void main(String[] args) {
		SpringApplication.run(RealtimeUnassignmentServiceApp.class, args);
	}
}
