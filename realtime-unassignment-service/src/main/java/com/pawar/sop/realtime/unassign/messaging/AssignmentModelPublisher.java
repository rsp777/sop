package com.pawar.sop.realtime.unassign.messaging;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.sop.realtime.unassign.config.RTUConfig;
import com.pawar.sop.realtime.unassign.exceptions.MessageNotSentException;
import com.pawar.sop.realtime.unassign.producer.AssignmentModelProducer;
import com.pawar.sop.realtime.unassign.wrapper.SopLogWrapper;

@Component
public class AssignmentModelPublisher {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentModelPublisher.class);

	@Autowired
	private RTUConfig rtuConfig;

	@Autowired
	private AssignmentModelProducer assignmentModelProducer ;

//	public ItemPublisher(CubiscanToWmsConfig cubiscanToWmsConfig) {
//		this.cubiscanToWmsConfig = cubiscanToWmsConfig;
//	}

	public void publishAssignmentModeltoRealtimeAssignment(AssignmentModel assignmentModel,LogEntryDto logEntryDto, SopLogWrapper sopLogWrapper) throws MessageNotSentException, InterruptedException, ExecutionException, ClientProtocolException, IOException {
		String topic = rtuConfig.getRtuToWmsRealtimeUnassignmentTopic();
		logger.info("Publishing AssignmentModel {} to WMS Topic {}", assignmentModel, topic);
		assignmentModelProducer.sendMessage(topic, assignmentModel, logEntryDto, sopLogWrapper);
		logger.info("Published AssignmentModel {} to WMS Topic {}", assignmentModel, topic);
	}
}
