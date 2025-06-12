package com.pawar.sop.realtime.assign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RTConfig {

	@Value("${rtu.to.wms.realtime.assignment.topic}")
	private String rtuToWmsRealtimeUnassignmentTopic;
	
	public String getRtuToWmsRealtimeUnassignmentTopic() {
		return rtuToWmsRealtimeUnassignmentTopic;
	}

	public void setRtuToWmsRealtimeUnassignmentTopic(String rtuToWmsRealtimeUnassignmentTopic) {
		this.rtuToWmsRealtimeUnassignmentTopic = rtuToWmsRealtimeUnassignmentTopic;
	}
}
