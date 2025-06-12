package com.pawar.sop.realtime.unassign.producer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.sop.realtime.unassign.constants.SopConstants;
import com.pawar.sop.realtime.unassign.converter.PayloadConverter;
import com.pawar.sop.realtime.unassign.exceptions.MessageNotSentException;
import com.pawar.sop.realtime.unassign.wrapper.SopLogWrapper;

@Component
public class AssignmentModelProducer {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentModelProducer.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;
	
	@Autowired
	private PayloadConverter payloadConverter;
	
	public AssignmentModelProducer() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}
	
	public void sendMessage(String topic, AssignmentModel assignmentModel,LogEntryDto logEntryDto, SopLogWrapper sopLogWrapper) throws MessageNotSentException, InterruptedException, ExecutionException, ClientProtocolException, IOException {
		String assignmentModelJsonPayload = payloadConverter.convertAssignmentModelPayloadtoJson(assignmentModel);

		try {
			kafkaTemplate.send(topic, assignmentModelJsonPayload).get();
			createSopLog(assignmentModelJsonPayload, topic,String.format(SopConstants.MESSAGE_PUBLISHED_TO_TOPIC,topic),logEntryDto, sopLogWrapper);
			logger.info("Message : {} sent to Topic : {}", assignmentModelJsonPayload, topic);
		} 
		catch (TimeoutException e) {
			e.printStackTrace();
			createSopLog(assignmentModelJsonPayload, topic,String.format(SopConstants.MESSAGE_FAILED_TO_PUBLISHED_TO_TOPIC,topic),logEntryDto, sopLogWrapper);
			logger.error("Error sending message : {}", e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			createSopLog(assignmentModelJsonPayload, topic,String.format(SopConstants.MESSAGE_FAILED_TO_PUBLISHED_TO_TOPIC,topic),logEntryDto, sopLogWrapper);
			logger.error("Error sending message : {}", e.getMessage());
		}
	}
	
	private void createSopLog(String jsonData,String topic,String message,LogEntryDto logEntryDto,SopLogWrapper sopLogWrapper) throws ClientProtocolException, IOException {
		logEntryDto.setMessage(message);
		logEntryDto.setMetadata(jsonData);
		logEntryDto.setCreatedAt(LocalDateTime.now());
		sopLogWrapper.createLog(logEntryDto);
	}
}
