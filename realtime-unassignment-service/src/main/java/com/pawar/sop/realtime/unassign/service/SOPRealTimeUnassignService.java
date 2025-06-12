package com.pawar.sop.realtime.unassign.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.sop.realtime.unassign.exceptions.MessageNotSentException;
import com.pawar.sop.realtime.unassign.wrapper.SopLogWrapper;

public interface SOPRealTimeUnassignService {

	void unassign(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)  throws ClientProtocolException, IOException ;
	void sendItemForRealTimeAssign(AssignmentModel assignmentModel,LogEntryDto logEntryDto,SopLogWrapper sopLogWrapper)throws ClientProtocolException, MessageNotSentException, InterruptedException, ExecutionException, IOException ;
	
}
