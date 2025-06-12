package com.pawar.sop.realtime.unassign.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.sop.realtime.unassign.config.RTUConfig;
import com.pawar.sop.realtime.unassign.constants.BatchStatus;
import com.pawar.sop.realtime.unassign.constants.SopConstants;
import com.pawar.sop.realtime.unassign.exceptions.MessageNotSentException;
import com.pawar.sop.realtime.unassign.messaging.AssignmentModelPublisher;
import com.pawar.sop.realtime.unassign.wrapper.InventoryWrapper;
import com.pawar.sop.realtime.unassign.wrapper.SopConfigWrapper;
import com.pawar.sop.realtime.unassign.wrapper.SopLogWrapper;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service("sopRealTimeUnassignServiceImpl")
public class SOPRealTimeUnassignServiceImpl implements SOPRealTimeUnassignService {

	private final static Logger logger = LoggerFactory.getLogger(SOPRealTimeUnassignServiceImpl.class);

	private final ObjectMapper objectMapper;

	@Autowired
	private SopLogWrapper sopLogWrapper;

	@Autowired
	private InventoryWrapper inventoryWrapper;

	@Autowired
	private RTUConfig rtuConfig;

	@Autowired
	private SopConfigWrapper sopConfigWrapper;

	@Autowired
	private AssignmentModelPublisher assignmentModelPublisher;

	public static String WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT;

	public SOPRealTimeUnassignServiceImpl() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

	}

	@PostConstruct
	public void init() {
		SOPRealTimeUnassignServiceImpl.WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT = rtuConfig
				.getWmsToSopRealtimeUnassignmentTopic();
	}

	@Override
	@KafkaListener(topics = "#{@sopRealTimeUnassignServiceImpl.WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT}", groupId = "sop-unassign-group")
	@Transactional
	public void unassign(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
			throws ClientProtocolException, IOException {
		String value = consumerRecord.value();
		logger.info("Incoming payload : {}", value);
		AssignmentModel assignmentModel = objectMapper.readValue(value, AssignmentModel.class);
		logger.info("Incoming AssignmentModel values : {}", assignmentModel);

		String actionType = assignmentModel.getSopActionType();
		String batchType = assignmentModel.getBatchType();
		String category = assignmentModel.getCategory();
		String itemName = assignmentModel.getItemName();
		AssignmentModel newAssignmentModel = new AssignmentModel();
		// Kafka listener method returns void, exceptions can be handled or logged
		if (assignmentModel.getSopActionType().equals(SopConstants.UNASSIGN)
				&& assignmentModel.getBatchType().equals(SopConstants.REALTIMEUNASSIGN)) {

			logger.info("Received unassign request: actionType={}, item={}, category={}", actionType, itemName,
					category);
			String HttpStatus_SC_ACCEPTED = String.valueOf(HttpStatus.SC_ACCEPTED + " " + "ACCEPTED");
			logger.info("HttpStatus_SC_ACCEPTED : {}", HttpStatus_SC_ACCEPTED);
			try {
				String batchId = sopLogWrapper.createBatch(actionType, batchType);
				if(batchId.contains("Batch is in progress")) {
					return ;
				}
				if (batchId.contains(HttpStatus_SC_ACCEPTED)) {
					logger.info("RESPONSEE : {}", batchId.contains(HttpStatus_SC_ACCEPTED));
					return;
				} else {
					LogEntryDto logEntryDto = new LogEntryDto();

					logEntryDto.setBatchId(batchId);
					logEntryDto.setBatchMode(actionType + " : " + batchType);
					logEntryDto.setMessage(SopConstants.NEW_BATCH + actionType
							+ String.format(SopConstants.TRIGGERED, itemName, category));
					logEntryDto.setCreatedAt(LocalDateTime.now());
					logEntryDto.setServiceName(SopConstants.SERVICE_NAME);
					logEntryDto.setIpAddress(Inet4Address.getLocalHost().getHostAddress());
					sopLogWrapper.createLog(logEntryDto);

					// Fetch active inventories by itemName
					List<Inventory> activeInventories = inventoryWrapper.fetchActiveInventoryByItemName(itemName);

					if (activeInventories.isEmpty()) {
						logEntryDto.setMessage(
								SopConstants.ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND + " for itemName: " + itemName);
						logEntryDto.setCreatedAt(LocalDateTime.now());
						sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND);
						sopLogWrapper.createLog(logEntryDto);
						logger.info("No active inventory to unassign for itemName: {}", itemName);
						return;
					}

					for (Inventory activeInventory : activeInventories) {
							logger.info("Processing active inventory for item: {} at location: {}",
									activeInventory.getItem().getItemName(),
									activeInventory.getLocation().getLocn_brcd());

							logEntryDto.setMessage(String.format(SopConstants.CURRENT_ACTIVE_INVENTORY,
									activeInventory.getItem().getItemName(),
									activeInventory.getLocation().getLocn_brcd()));
							logEntryDto.setCreatedAt(LocalDateTime.now());
							logEntryDto.setItem(activeInventory.getItem().getItemName());
							logEntryDto.setLocation(activeInventory.getLocation().getLocn_brcd());
							sopLogWrapper.createLog(logEntryDto);

							// Preserve on_hand_qty before deletion
							int onHandQty = (int) activeInventory.getOn_hand_qty();
							logger.info("Preserved on_hand_qty: {}", onHandQty);

							// Delete the active inventory using item's unique identifier (can customize
							// accordingly)
							inventoryWrapper.deleteActiveInventory(activeInventory.getLocation().getLocn_brcd());

							logger.info("Deleted active inventory for item: {}",
									activeInventory.getItem().getItemName());

							// Log unassignment completed
							logEntryDto.setMessage(String.format(SopConstants.UNASSIGN_COMPLETED_FOR_ITEM_LOCATION,
									activeInventory.getItem().getItemName(),
									activeInventory.getLocation().getLocn_brcd()));
							logEntryDto.setCreatedAt(LocalDateTime.now());
							sopLogWrapper.createLog(logEntryDto);

							// Prepare assignment model to send message with preserved onHandQty
							newAssignmentModel.setSopActionType(SopConstants.ASSIGN);
							newAssignmentModel.setBatchType(SopConstants.REALTIMEASSIGN);
							newAssignmentModel.setItemName(activeInventory.getItem().getItemName());
							newAssignmentModel.setOnHandQty(onHandQty);

							
						
					}

					logEntryDto.setMessage(SopConstants.UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.UNASSIGN_BATCH_COMPLETED);
					sopLogWrapper.createLog(logEntryDto);
					
					Thread.sleep(1000);
					
					// Send the message onto output topic
					sendItemForRealTimeAssign(newAssignmentModel, logEntryDto, sopLogWrapper);

					logger.info("Sent message to topic {} for item {}",
							rtuConfig.getRtuToWmsRealtimeUnassignmentTopic(), newAssignmentModel.getItemName());
					logger.info(SopConstants.UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY);
				}

			} catch (Exception e) {
				logger.error("Exception occurred in unassign processing", e);
			}
		} else {
			LogEntryDto logEntryDto = new LogEntryDto();
			logEntryDto.setBatchMode(actionType + " : " + batchType);
			logEntryDto.setMessage(String.format(SopConstants.INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE,
					assignmentModel.getSopActionType()));
			logEntryDto.setCreatedAt(LocalDateTime.now());
			logEntryDto.setServiceName(SopConstants.SERVICE_NAME);
			logEntryDto.setIpAddress(Inet4Address.getLocalHost().getHostAddress());
			sopLogWrapper.createLog(logEntryDto);
			logger.warn("Invalid action type received: {}. Expected UNASSIGN.", actionType);
			return;

		}
	}

	public int getSopActionTypeId(String sopActionType) {

		if (sopActionType.equals("ASSIGN")) {
			return 1;
		} else {
			return 2;
		}

	}

	@Override
	public void sendItemForRealTimeAssign(AssignmentModel assignmentModel, LogEntryDto logEntryDto,
			SopLogWrapper sopLogWrapper) throws ClientProtocolException, MessageNotSentException, InterruptedException,
			ExecutionException, IOException {
		logger.info("inside sendItemForRealTimeAssign method : {}",assignmentModel);
		assignmentModelPublisher.publishAssignmentModeltoRealtimeAssignment(assignmentModel, logEntryDto,
				sopLogWrapper);

	}

}
