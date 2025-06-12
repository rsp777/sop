package com.pawar.sop.realtime.unassign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RTUConfig {

	
	@Value("${wms.to.sop.realtime.unassignment.topic}")
	private String wmsToSopRealtimeUnassignmentTopic;
	
	@Value("${rtu.to.wms.realtime.assignment.topic}")
	private String rtuToWmsRealtimeUnassignmentTopic;


	public String getWmsToSopRealtimeUnassignmentTopic() {
		return wmsToSopRealtimeUnassignmentTopic;
	}

	public void setWmsToSopRealtimeUnassignmentTopic(String wmsToSopRealtimeUnassignmentTopic) {
		this.wmsToSopRealtimeUnassignmentTopic = wmsToSopRealtimeUnassignmentTopic;
	}

	public String getRtuToWmsRealtimeUnassignmentTopic() {
		return rtuToWmsRealtimeUnassignmentTopic;
	}

	public void setRtuToWmsRealtimeUnassignmentTopic(String rtuToWmsRealtimeUnassignmentTopic) {
		this.rtuToWmsRealtimeUnassignmentTopic = rtuToWmsRealtimeUnassignmentTopic;
	}
}