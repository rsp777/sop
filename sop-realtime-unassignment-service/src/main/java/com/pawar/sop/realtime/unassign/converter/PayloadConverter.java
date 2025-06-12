package com.pawar.sop.realtime.unassign.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Item;

@Component
public class PayloadConverter {

	private static final Logger logger = LoggerFactory.getLogger(PayloadConverter.class);

	private final ObjectMapper objectMapper;

	public PayloadConverter() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	public String convertAssignmentModelPayloadtoJson(AssignmentModel assignmentModel) throws JsonProcessingException {
		String assignmentModelJsonPayload = objectMapper.writeValueAsString(assignmentModel);
		logger.info("AssignmentModel Json Payload : {}", assignmentModelJsonPayload);
		return assignmentModelJsonPayload;
	}
}
