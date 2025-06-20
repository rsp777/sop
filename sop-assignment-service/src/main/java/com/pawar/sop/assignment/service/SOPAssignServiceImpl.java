package com.pawar.sop.assignment.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.sop.assignment.config.AsnServiceConfiguration;
import com.pawar.sop.assignment.config.InventoryServiceConfiguration;
import com.pawar.sop.assignment.config.SopConfigServiceConfiguration;
import com.pawar.sop.assignment.constants.AsnStatusConstants;
import com.pawar.sop.assignment.constants.BatchStatus;
import com.pawar.sop.assignment.constants.LpnFacilityStatusContants;
import com.pawar.sop.assignment.constants.SopConstants;
import com.pawar.sop.assignment.log.wrapper.InventoryWrapper;
import com.pawar.sop.assignment.log.wrapper.SopConfigWrapper;
import com.pawar.sop.assignment.log.wrapper.SopLogWrapper;
import com.pawar.sop.assignment.model.SopEligibleItems;
import com.pawar.sop.assignment.repository.SopEligibleItemsRepository;

import jakarta.transaction.Transactional;

@Service
public class SOPAssignServiceImpl implements SOPAssignService {

	private final static Logger logger = LoggerFactory.getLogger(SOPAssignServiceImpl.class);

	@Value("${spring.application.name}")
	private String service_name;

	private final static String FAILED_TO_CREATED_ASSIGNMENT = "Failed to Create Assignment";
	private final static String ASSIGNMENT_INTERRUPTED = "Assignment Interrupted";
	private final static String NO_ELIGIBLE_UPCS = "No Eligible UPCs";
	private final static String NO_ELIGIBLE_LOCATIONS = "No Eligible Locations";
	private final static String ASSIGNMENT_CREATED_SUCCESSFULLY = "Assignment Created Successfully";
	private final ObjectMapper objectMapper;
	@Autowired
	private SopLogWrapper sopLogWrapper;

	@Autowired
	private InventoryWrapper inventoryWrapper;

	@Autowired
	private SopConfigWrapper sopConfigWrapper;

	@Autowired
	private SopEligibleItemsRepository sopEligibleItemsRepository;

	public SOPAssignServiceImpl(SopConfigServiceConfiguration sopConfigServiceConfiguration,
			InventoryServiceConfiguration inventoryServiceConfiguration,
			AsnServiceConfiguration asnServiceConfiguration) {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

	}

	@Override
	public List<SopEligibleItemsDto> getEligibleUpcs(String category, LogEntryDto logEntryDto)
			throws ClientProtocolException, IOException {
		List<SopEligibleItemsDto> dbSopEligibleItemsDtos = fetchEligibleUpcsFromDb(category);
		logger.info("Eligible Items : {}", dbSopEligibleItemsDtos);
		return dbSopEligibleItemsDtos;
	}

	private List<SopEligibleItemsDto> fetchEligibleUpcsFromDb(String category) {

		List<SopEligibleItemsDto> sopEligibleItemsDtos = new ArrayList<>();
		SopEligibleItemsDto sopEligibleItemsDto = new SopEligibleItemsDto();
		List<SopEligibleItems> sopEligibleItems = sopEligibleItemsRepository.findByCategory(category);

		if (!sopEligibleItems.isEmpty()) {
			for (SopEligibleItems sopEligibleItem : sopEligibleItems) {
				sopEligibleItemsDto = sopEligibleItem.convertEntityToDto(sopEligibleItem);
				sopEligibleItemsDtos.add(sopEligibleItemsDto);

			}
		}
		return sopEligibleItemsDtos;
	}

	@Override
	@Transactional
	public String createAssignment(AssignmentModel assignmentModel) throws ClientProtocolException, IOException {

		if (assignmentModel.getSopActionType().equals("ASSIGN")
				&& assignmentModel.getBatchType().equals("BATCHTIMEASSIGN")) {
			String actionType = assignmentModel.getSopActionType();
			String batchType = assignmentModel.getBatchType();
			String category = assignmentModel.getCategory();
			logger.info("Action Type : {} ", actionType);
			String batchId = sopLogWrapper.createBatch(actionType, batchType);
			logger.info("RESPONSE : {}", batchId);
			String HttpStatus_SC_ACCEPTED = String.valueOf("<202 ACCEPTED Accepted");
			logger.info("HttpStatus_SC_ACCEPTED : {}", HttpStatus_SC_ACCEPTED);
			logger.info("batchId.contains(HttpStatus_SC_ACCEPTED) : {}", batchId.contains(HttpStatus_SC_ACCEPTED));
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
				logEntryDto.setBatchMode(actionType+" : "+batchType);
				logEntryDto.setMessage(
						SopConstants.NEW_BATCH + actionType + String.format(SopConstants.TRIGGERED, category));
				logEntryDto.setCreatedAt(LocalDateTime.now());
				logEntryDto.setServiceName(SopConstants.SERVICE_NAME);
				logEntryDto.setIpAddress(Inet4Address.getLocalHost().getHostAddress());
				sopLogWrapper.createLog(logEntryDto);

				logger.info("Starting to create assignment");
				List<SopEligibleItemsDto> sopEligibleItemsDtos = getEligibleUpcs(category, logEntryDto);
				int eligibleUpcsCount = getEligibleUpcsCount(sopEligibleItemsDtos);
				logEntryDto.setModuleName(SopConstants.ELIGIBLE_UPCS_MODULE);
				logEntryDto.setMessage("Total " + eligibleUpcsCount + " Eligible UPCs found ");
				logEntryDto.setCreatedAt(LocalDateTime.now());
				sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_UPCS_FOUND);
				sopLogWrapper.createLog(logEntryDto);

				if (sopEligibleItemsDtos.isEmpty()) {
					logEntryDto.setMessage(SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.FAILED);
					sopLogWrapper.createLog(logEntryDto);

					return SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS;
				}

				logger.info("Eligible UPCs : {}", sopEligibleItemsDtos);
				List<SopEligibleLocationsDto> sopEligibleLocationsDtos = sopConfigWrapper
						.getEligibleLocations(category);
				logEntryDto.setModuleName("Eligible Locations module");
				logEntryDto.setMessage("Total " + sopEligibleLocationsDtos.size() + " Eligible Locations found ");
				logEntryDto.setCreatedAt(LocalDateTime.now());
				sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ELIGIBLE_LOCATIONS_FOUND);
				sopLogWrapper.createLog(logEntryDto);

				if (sopEligibleLocationsDtos.isEmpty()) {
					logEntryDto.setMessage(SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.FAILED);
					sopLogWrapper.createLog(logEntryDto);
					return SopConstants.ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS;
				}

				logger.info("Eligible Locations : {}", sopEligibleLocationsDtos);
				for (SopEligibleItemsDto sopEligibleItemsDto : sopEligibleItemsDtos) {
					boolean itemAssigned = false;
					for (SopEligibleLocationsDto sopEligibleLocationsDto : sopEligibleLocationsDtos) {
						synchronized (this) {
							String itemBrcd = sopEligibleItemsDto.getItem_brcd();
							String locnBrcd = sopEligibleLocationsDto.getLocn_brcd();
							logEntryDto.setItem(sopEligibleItemsDto.getItem_brcd());
							logEntryDto.setLocation(sopEligibleLocationsDto.getLocn_brcd());
							logEntryDto.setModuleName("Assignment Module");
							logEntryDto.setAsn(sopEligibleItemsDto.getAsnBrcd());
							int assignedUpc = sopEligibleLocationsDto.getAssignedNbrOfUpc();

							// Skip locations with existing UPC assignments
							if (assignedUpc > 0) {
								String skipMessage = String.format(
										"Skipping location %s - already has %d UPC(s) assigned", locnBrcd, assignedUpc);
								logger.info(skipMessage);
								logEntryDto.setMessage(skipMessage);
								logEntryDto.setCreatedAt(LocalDateTime.now());
								sopLogWrapper.createLog(logEntryDto);
								continue;
							}

							if (canAssign(sopEligibleLocationsDto, sopEligibleItemsDto, logEntryDto, sopLogWrapper)) {
								sopLogWrapper.updateBatchStatus(batchId, BatchStatus.ASSIGNMENT_IN_PROGRESS);

								logger.info("Creating Assignment for UPC: {} and Location : {}", itemBrcd, locnBrcd);
								String message = String.format("Creating Assignment for UPC: %s and Location : %s",
										itemBrcd, locnBrcd);
								logEntryDto.setMessage(message);
								sopLogWrapper.createLog(logEntryDto);
								inventoryWrapper.createInventory(itemBrcd, locnBrcd);
								assignedItems.add(itemBrcd);
								itemAssigned = true;
								message = String.format("Created Assignment for UPC: %s and Location : %s", itemBrcd,
										locnBrcd);
								logEntryDto.setCreatedAt(LocalDateTime.now());
								logEntryDto.setMessage(message);
								sopLogWrapper.createLog(logEntryDto);
								sopConfigWrapper.updateSopEligibleLocations(sopEligibleLocationsDto);
								logger.info("Location updated with ");
							} else {
								String message = String.format(
										FAILED_TO_CREATED_ASSIGNMENT + " : Check Item Dimensions: %s", itemBrcd);
								logEntryDto.setMessage(message);
								logEntryDto.setCreatedAt(LocalDateTime.now());
								sopLogWrapper.createLog(logEntryDto);
								logger.info("Check Item Dimensions : {}", itemBrcd);
							}
						}
						if (itemAssigned) {
							logger.info("Assigned item: {}", sopEligibleItemsDto.getItem_brcd());
						}
					}
				}
				logger.info("Compare eligibleUpcs : {} and Assigned Items count : {}", eligibleUpcsCount,
						assignedItems.size());
				if (assignedItems.size() != 0 && eligibleUpcsCount >= assignedItems.size()) {
					logEntryDto.setMessage(ASSIGNMENT_CREATED_SUCCESSFULLY);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.COMPLETED);
					sopLogWrapper.createLog(logEntryDto);
					return SopConstants.ASSIGNMENT_CREATED_SUCCESSFULLY;
				} else {
					logEntryDto.setMessage(FAILED_TO_CREATED_ASSIGNMENT);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.updateBatchStatus(batchId, BatchStatus.FAILED);
					sopLogWrapper.createLog(logEntryDto);
					return SopConstants.FAILED_TO_CREATE_ASSIGNMENT;
				}
			}

		} else {
			return String.format(SopConstants.INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE,
					assignmentModel.getSopActionType());
		}

	}

	private boolean canAssign(SopEligibleLocationsDto location, SopEligibleItemsDto item, LogEntryDto logEntryDto,
			SopLogWrapper sopLogWrapper) throws ClientProtocolException, IOException {
		logger.info("{}", item);
		// Parameter validation
		if (location == null || item == null) {
			logger.error("Invalid parameters: location={}, item={}", location, item);
			return false;
		}

		// Validate numerical constraints
		if (location.getLength() <= 0 || location.getWidth() <= 0 || item.getLength() <= 0 || item.getWidth() <= 0) {
			logger.error("Invalid dimensions - Location: {} {}, Item: {} {}", location.getLength(), location.getWidth(),
					item.getLength(), item.getWidth());
			String message = String.format("Invalid dimensions - Location: %.2f ,%.2f | Item: %.2f ,%.2f", location.getLength(),
					location.getWidth(), item.getLength(), item.getWidth());
			logEntryDto.setMessage(message);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
			return false;
		}

		final String itemBrcd = item.getItem_brcd();

		boolean inventoryExists = checkActiveInventory(itemBrcd);
		if (inventoryExists) {
			String inventoryMessage = "Assignment blocked - Active inventory exists for item " + itemBrcd;
			logger.info(inventoryMessage);
			logEntryDto.setMessage(inventoryMessage);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
			return false;
		}

		final int assignedUpc = location.getAssignedNbrOfUpc();
		final int maxSkuCapacity = location.getMaxNbrOfSku();

		// Check for empty location condition
		if (assignedUpc == 0) {
			boolean validEmptyLocation = maxSkuCapacity > 0; // Ensure location can accept items
			boolean fitsDimensions = fitsInLocation(location, item, logEntryDto, sopLogWrapper);

			logger.info("Empty location check - Valid: {}, Fits: {}", validEmptyLocation, fitsDimensions);
			return validEmptyLocation && fitsDimensions;

		} else {
			// Occupied location case
			// 5. Removed misleading "already assigned" message
			boolean hasCapacity = assignedUpc < maxSkuCapacity;
			boolean fitsDimensions = fitsInLocation(location, item, logEntryDto, sopLogWrapper);
			boolean meetsAssignmentCriteria = hasCapacity && fitsDimensions;

			logger.info("Occupied location check - Capacity: {}/{} ({}), Fits: {}", assignedUpc, maxSkuCapacity,
					hasCapacity, fitsDimensions);

			if (!meetsAssignmentCriteria) {
				String failureMessage = String.format("Assignment failed - Capacity: %d/%d, Fits: %b", assignedUpc,
						maxSkuCapacity, fitsDimensions);
				logEntryDto.setMessage(failureMessage);
				logEntryDto.setCreatedAt(LocalDateTime.now());
				sopLogWrapper.createLog(logEntryDto);
			}

			return meetsAssignmentCriteria;
		}

	}

	private boolean fitsInLocation(SopEligibleLocationsDto location, SopEligibleItemsDto item, LogEntryDto logEntryDto,
			SopLogWrapper sopLogWrapper) throws ClientProtocolException, IOException {

		boolean isfits = false;
		if (location.getLength() >= item.getLength() && location.getWidth() >= item.getWidth()
				&& location.getHeight() >= item.getHeight()) {
			isfits = true;
		} else {
			String message = String.format("Invalid dimensions - Location: %.2f, %.2f , %.2f |  Item: %.2f, %.2f , %.2f", location.getLength(),
					location.getWidth(),location.getHeight(), item.getLength(), item.getWidth(),item.getHeight());
			logEntryDto.setMessage(message);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
		}
		return isfits; // Added height check if available
	}

	private boolean checkActiveInventory(String itemBrcd) throws ClientProtocolException, IOException {
		boolean inventoryExists = inventoryWrapper.checkActiveInventory(itemBrcd);
		logger.info("Inventory check for item {}: {}", itemBrcd, inventoryExists);
		return inventoryExists;
	}

	private int getEligibleUpcsCount(List<SopEligibleItemsDto> sopEligibleItemsDtos) {
		Set<String> uniqueUpcs = new HashSet<>(); // Use a Set to store unique UPCs

		for (SopEligibleItemsDto sopEligibleItemsDto : sopEligibleItemsDtos) {
			String itemBrcd = sopEligibleItemsDto.getItem_brcd();
			uniqueUpcs.add(itemBrcd); // Add UPC to the Set
		}

		return uniqueUpcs.size(); // Return the count of unique UPCs
	}
}