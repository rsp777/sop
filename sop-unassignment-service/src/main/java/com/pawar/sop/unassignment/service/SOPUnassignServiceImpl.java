package com.pawar.sop.unassignment.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.sop.unassignment.constants.BatchStatus;
import com.pawar.sop.unassignment.constants.SopConstants;
import com.pawar.sop.unassignment.repository.SopUnassignDataRepository;
import com.pawar.sop.unassignment.wrapper.InventoryWrapper;
import com.pawar.sop.unassignment.wrapper.SopConfigWrapper;
import com.pawar.sop.unassignment.wrapper.SopLogWrapper;

import jakarta.transaction.Transactional;

@Service
public class SOPUnassignServiceImpl implements SOPUnassignService {

	private final static Logger logger = LoggerFactory.getLogger(SOPUnassignServiceImpl.class);

	private final ObjectMapper objectMapper;
	@Autowired
	private SopLogWrapper sopLogWrapper;

	@Autowired
	private InventoryWrapper inventoryWrapper;

	@Autowired
	private SopConfigWrapper sopConfigWrapper;

	@Autowired
	private SopUnassignDataRepository unassignDataRepository;

	public SOPUnassignServiceImpl() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

	}

	@Override
	@Transactional
	public String unassign(AssignmentModel assignmentModel) throws ClientProtocolException, IOException {

		if (assignmentModel.getSopActionType().equals("UNASSIGN")
				&& assignmentModel.getBatchType().equals("BATCHTIMEUNASSIGN")) {
			String actionType = assignmentModel.getSopActionType();
			String batchType = assignmentModel.getBatchType();
			String category = assignmentModel.getCategory();
			logger.info("Action Type : {} ", actionType);
			String batchId = sopLogWrapper.createBatch(actionType,batchType);

			logger.info("RESPONSE : {}", batchId);
			String HttpStatus_SC_ACCEPTED = String.valueOf(HttpStatus.SC_ACCEPTED + " " + "ACCEPTED");
			logger.info("HttpStatus_SC_ACCEPTED : {}", HttpStatus_SC_ACCEPTED);
			if(batchId.contains("Batch is in progress")) {
				return batchId;
			}
			else if (batchId.contains(HttpStatus_SC_ACCEPTED)) {
				logger.info("RESPONSEE : {}", batchId.contains(HttpStatus_SC_ACCEPTED));
				return batchId;

			} else {
				LogEntryDto logEntryDto = new LogEntryDto();
				Set<String> assignedItems = new HashSet<>();

				logEntryDto.setBatchId(batchId);
				logEntryDto.setBatchMode(actionType + " : " + batchType);
				logEntryDto.setMessage(
						SopConstants.NEW_BATCH + actionType + String.format(SopConstants.TRIGGERED, category));
				logEntryDto.setCreatedAt(LocalDateTime.now());
				logEntryDto.setServiceName(SopConstants.SERVICE_NAME);
				logEntryDto.setIpAddress(Inet4Address.getLocalHost().getHostAddress());
				sopLogWrapper.createLog(logEntryDto);

				logger.info("Starting to unassignment");
//				List<SopEligibleItemsDto> sopEligibleItemsDtos = getEligibleUpcs(category, logEntryDto);
//				int eligibleUpcsCount = getEligibleUpcsCount(sopEligibleItemsDtos);
//				logEntryDto.setModuleName(SopConstants.ELIGIBLE_UPCS_MODULE);
//				logEntryDto.setMessage("Total " + eligibleUpcsCount + " Eligible UPCs found ");
//				logEntryDto.setCreatedAt(LocalDateTime.now());
//				sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_UPCS_FOUND);
//				sopLogWrapper.createLog(logEntryDto);

//				if (sopEligibleItemsDtos.isEmpty()) {
//					logEntryDto.setMessage(SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS);
//					logEntryDto.setCreatedAt(LocalDateTime.now());
//					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.FAILED);
//					sopLogWrapper.createLog(logEntryDto);
				//
//					return SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS;
//				}
				//
//				logger.info("Eligible UPCs : {}", sopEligibleItemsDtos);
				int sopActionTypeId = getSopActionTypeId(assignmentModel.getSopActionType());
				List<SopEligibleLocationsDto> sopEligibleLocationsDtos = sopConfigWrapper
						.getEligibleLocations(sopActionTypeId, category);
				logEntryDto.setModuleName("Eligible Locations module");
				logEntryDto.setMessage("Total " + sopEligibleLocationsDtos.size() + " Eligible Locations found ");
				logEntryDto.setCreatedAt(LocalDateTime.now());
				sopLogWrapper.updateBatchStatus(batchId, BatchStatus.UNASSIGN_ELIGIBLE_LOCATIONS_FOUND);
				sopLogWrapper.createLog(logEntryDto);

				if (sopEligibleLocationsDtos.isEmpty()) {
					logEntryDto.setMessage(SopConstants.UNASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.FAILED);
					sopLogWrapper.createLog(logEntryDto);
					return SopConstants.UNASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS;
				}

				logger.info("Eligible Locations : {}", sopEligibleLocationsDtos);

				for (SopEligibleLocationsDto sopEligibleLocationsDto : sopEligibleLocationsDtos) {
					synchronized (this) {
						String locnBrcd = sopEligibleLocationsDto.getLocn_brcd();
						List<Inventory> activeInventories = inventoryWrapper.fetchActiveInventory(locnBrcd);
						if (!activeInventories.isEmpty()) {
							logEntryDto.setMessage(
									SopConstants.ELIGIBLE_ACTIVE_INVENTORY_FETCHED + " " + activeInventories);
							logEntryDto.setCreatedAt(LocalDateTime.now());
							sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_ACTIVE_INVENTORY_FETCHED);
							sopLogWrapper.createLog(logEntryDto);
							logger.info("Unassignment Eligible Active Inventories fetched : {}", activeInventories);

							for (Inventory activeInventory : activeInventories) {
								logger.info(String.format(SopConstants.CURRENT_ACTIVE_INVENTORY,
										activeInventory.getItem().getItemName(),
										activeInventory.getLocation().getLocn_brcd()));
								logEntryDto.setMessage(String.format(SopConstants.CURRENT_ACTIVE_INVENTORY,
										activeInventory.getItem().getItemName(),
										activeInventory.getLocation().getLocn_brcd()));
								logEntryDto.setCreatedAt(LocalDateTime.now());
								logEntryDto.setItem(activeInventory.getItem().getItemName());
								logEntryDto.setLocation(activeInventory.getLocation().getLocn_brcd());
								sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_ACTIVE_INVENTORY_FETCHED);
								sopLogWrapper.createLog(logEntryDto);

								logger.info("Unassignment starting..");
								inventoryWrapper.deleteActiveInventory(locnBrcd);
								logger.info("Active Inventory Unassigned for location: {}", locnBrcd);
								if (sopEligibleLocationsDto.getSopLocationRange().getSopActionType().getActionType()
										.equals("ASSIGN")) {
									sopEligibleLocationsDto.setAssignedNbrOfUpc(0);
									sopEligibleLocationsDto.setLastUpdatedDttm(LocalDateTime.now());
									sopEligibleLocationsDto.setLastUpdatedSource(SopConstants.SERVICE_NAME);
									sopConfigWrapper.updateSopEligibleLocations(sopEligibleLocationsDto);
								} else {
									logger.info("Unassign Location : {}", sopEligibleLocationsDto.getLocn_brcd());
									sopConfigWrapper.deleteSopEligibleLocation(
											sopEligibleLocationsDto.getSopEligibleLocationsId());
								}

								logEntryDto.setMessage(String.format(SopConstants.UNASSIGN_COMPLETED_FOR_ITEM_LOCATION,
										activeInventory.getItem().getItemName(),
										activeInventory.getLocation().getLocn_brcd()));
								logEntryDto.setCreatedAt(LocalDateTime.now());
								logEntryDto.setItem(activeInventory.getItem().getItemName());
								logEntryDto.setLocation(activeInventory.getLocation().getLocn_brcd());
								sopLogWrapper.createLog(logEntryDto);
							}
						} else {
							logEntryDto.setMessage(
									SopConstants.ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND + " " + activeInventories);
							logEntryDto.setCreatedAt(LocalDateTime.now());
							sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND);
							sopLogWrapper.createLog(logEntryDto);
						}
					}
					logEntryDto.setMessage(SopConstants.UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.UNASSIGN_BATCH_COMPLETED);
					sopLogWrapper.createLog(logEntryDto);
					logger.info(SopConstants.UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY);

				}

			}
		} else {
			return String.format(SopConstants.INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE,
					assignmentModel.getSopActionType());
		}
		return SopConstants.UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY;
	}

	public int getSopActionTypeId(String sopActionType) {

		if (sopActionType.equals("ASSIGN")) {
			return 1;
		} else {
			return 2;
		}

	}

}
