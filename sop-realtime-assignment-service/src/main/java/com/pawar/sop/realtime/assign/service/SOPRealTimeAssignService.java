package com.pawar.sop.realtime.assign.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

public interface SOPRealTimeAssignService {


	String createAssignment(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) throws ClientProtocolException, IOException;

	List<SopEligibleItemsDto> getEligibleUpc(String itemBrcd, LogEntryDto logEntryDto)
			throws ClientProtocolException, IOException;	

}
