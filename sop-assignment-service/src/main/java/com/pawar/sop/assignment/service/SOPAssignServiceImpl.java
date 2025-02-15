package com.pawar.sop.assignment.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.LogEntryDto;
import com.pawar.inventory.entity.Lpn;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.sop.assignment.config.AsnServiceConfiguration;
import com.pawar.sop.assignment.config.InventoryServiceConfiguration;
import com.pawar.sop.assignment.config.SopConfigServiceConfiguration;
import com.pawar.sop.assignment.constants.AsnStatusConstants;
import com.pawar.sop.assignment.constants.LpnFacilityStatusContants;
import com.pawar.sop.assignment.controller.SopAssignmentController;
import com.pawar.sop.assignment.log.wrapper.InventoryWrapper;
import com.pawar.sop.assignment.log.wrapper.SopConfigWrapper;
import com.pawar.sop.assignment.log.wrapper.SopLogWrapper;
import com.pawar.sop.assignment.model.SopEligibleItems;
import com.pawar.sop.assignment.repository.SopEligibleItemsRepository;

@Service
public class SOPAssignServiceImpl implements SOPAssignService {

	private final static Logger logger = LoggerFactory.getLogger(SOPAssignServiceImpl.class);

	@Value("${spring.application.name}")
	private String service_name;

	private final static String LOCATION_RANGE_ADD_SUCCESS = "Location Range Added Successfully.";
	private final static String LOCATION_RANGE_ADD_FAILED = "Failed to add Location Range.";
	private final static String ACTION_TYPE_ADD_SUCCESS = "Action Type Added Successfully.";
	private final static String ACTION_TYPE_ADD_FAILED = "Failed to add Action Type";
	private final static String FAILED_TO_CREATED_ASSIGNMENT = "Failed to Create Assignment";
	private final static String ASSIGNMENT_INTERRUPTED = "Assignment Interrupted";
	private final static String NO_ELIGIBLE_UPCS = "No Eligible UPCs";
	private final static String NO_ELIGIBLE_LOCATIONS = "No Eligible Locations";
	private final static String NEW_BATCH = "New Batch ";
	private final static String TRIGGERD = " Trigger for Category : %s";
	private final static String ASSIGNMENT_CREATED_SUCCESSFULLY = "Assignment Created Successfully";
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private final SopConfigServiceConfiguration sopConfigServiceConfiguration;
	private final InventoryServiceConfiguration inventoryServiceConfiguration;
	private final AsnServiceConfiguration asnServiceConfiguration;

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
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		this.sopConfigServiceConfiguration = sopConfigServiceConfiguration;
		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
		this.asnServiceConfiguration = asnServiceConfiguration;

	}

	@Override
	public List<SopEligibleItemsDto> getEligibleUpcs(String category) throws ClientProtocolException, IOException {
		List<SopEligibleItemsDto> sopEligibleItemsDtos = fetchEligibleUpcs(category);
		logger.info("Eligible Items : {}", sopEligibleItemsDtos);
		saveEligibleUpcs(sopEligibleItemsDtos);
		return sopEligibleItemsDtos;
	}

	public void saveEligibleUpcs(List<SopEligibleItemsDto> sopEligibleItemsDtos) {
		logger.info("Eligible Upcs {}", sopEligibleItemsDtos);

		for (SopEligibleItemsDto sopEligibleItemsDto : sopEligibleItemsDtos) {

			SopEligibleItems eligibleItems = new SopEligibleItems(sopEligibleItemsDto);
			List<SopEligibleItems> eligibleItems2 = sopEligibleItemsRepository
					.findByAsnBrcdAndItemBrcd(eligibleItems.getAsnBrcd(), eligibleItems.getItemBrcd());
			logger.info("{}", eligibleItems.getAsnLpnInfo());
			logger.info("eligibleItems2.isEmpty() : {}", eligibleItems2.isEmpty());
			if (eligibleItems2.isEmpty()) {

				sopEligibleItemsRepository.save(eligibleItems);

			}
		}
	}

	public List<SopEligibleItemsDto> fetchEligibleUpcs(String category) throws ClientProtocolException, IOException {
		List<SopEligibleItemsDto> sopEligibleItemsDtos = new ArrayList<>();
		List<ASNDto> fetchedASNDtos = inventoryWrapper.fetchASNs(category);
		List<LpnDto> fetchedLpnDtos = inventoryWrapper.fetchLpns(category);
//		logger.info("fetchedASNDtos : {}", fetchedASNDtos);
//		logger.info("fetchedLpnDtos : {}", fetchedLpnDtos);

		for (ASNDto asnDto : fetchedASNDtos) {
			List<LpnDto> lpnDtos = asnDto.getLpns();
			for (LpnDto lpnDto : lpnDtos) {

				logger.info("condition : {} , ASN : {}, Lpn : {}",
						((asnDto.getAsnStatus() == AsnStatusConstants.IN_TRANSIT)
								&& (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.IN_TRANSIT)),
						asnDto.getAsnBrcd() + " " + asnDto.getAsnStatus(),
						lpnDto.getLpn_name() + " " + lpnDto.getLpn_facility_status());

				logger.info("condition : {} , ASN : {}, Lpn : {}",
						((asnDto.getAsnStatus() == AsnStatusConstants.RECEIVED)
								&& (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY)),
						asnDto.getAsnBrcd() + " " + asnDto.getAsnStatus(),
						lpnDto.getLpn_name() + " " + lpnDto.getLpn_facility_status());

				if ((asnDto.getAsnStatus() == AsnStatusConstants.IN_TRANSIT)
						&& (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.IN_TRANSIT)) {

					sopEligibleItemsDtos.add(createSopEligibleItemsDto(lpnDto, asnDto));

				} else if ((asnDto.getAsnStatus() == AsnStatusConstants.RECEIVED)
						&& (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY)) {
					sopEligibleItemsDtos.add(createSopEligibleItemsDto(lpnDto, asnDto));

				} else {
					logger.info("No Eligible UPCs");
					return sopEligibleItemsDtos;
				}
			}
		}
		if (fetchedASNDtos.isEmpty()) {
			for (LpnDto lpnDto : fetchedLpnDtos) {

				if (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY) {
					sopEligibleItemsDtos.add(createSopEligibleItemsDto(lpnDto));

				} else {
					logger.info("No Eligible UPCs");
					return sopEligibleItemsDtos;
				}

			}
		}
		return sopEligibleItemsDtos;
	}

	public String getAsnLpnInfo(ASNDto asnDto, LpnDto lpnDto) {

		String asnLpnInfo = "";
		if (asnDto != null) {
			String asnBrcd = lpnDto.getAsn_brcd();
			String lpnBrcd = lpnDto.getLpn_name();
			String itemBrcd = getItemBrcdByLpn(lpnDto);
			asnLpnInfo = asnLpnInfo + "ASN : " + asnBrcd + " ,Lpn : " + lpnBrcd + " , Item : " + itemBrcd;

		} else {
			String lpnBrcd = lpnDto.getLpn_name();
			String itemBrcd = getItemBrcdByLpn(lpnDto);
			asnLpnInfo = asnLpnInfo + "Lpn : " + lpnBrcd + ", Item : " + itemBrcd;

		}
		return asnLpnInfo;
	}

	public String getItemBrcdByLpn(LpnDto lpnDto) {
		String itemBrcd = "";
		itemBrcd = lpnDto.getItem().getItem_name();
		return itemBrcd;
	}

	public int getInTransitQty(ASNDto asnDto) {
		int inTransitQty = 0;

		if (asnDto != null) {
			if (asnDto.getAsnStatus() == AsnStatusConstants.IN_TRANSIT) {
				inTransitQty = asnDto.getTotalQuantity();
				return inTransitQty;
			}
		}
		return inTransitQty;

	}

	public int getRcvQty(ASNDto asnDto) {
		int rcvQty = 0;
		if (asnDto != null) {
			if (asnDto.getAsnStatus() == AsnStatusConstants.RECEIVED) {
				List<LpnDto> lpnDtos = asnDto.getLpns();
				for (LpnDto lpnDto : lpnDtos) {

					if (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.CREATED) {
						int lpnQty = lpnDto.getQuantity();
						rcvQty = rcvQty + lpnQty;
					}

				}
				return rcvQty;
			}
		}
		return rcvQty;

	}

	public int getResvQty(LpnDto lpnDto) {
		int resvQty = 0;
		if (lpnDto != null) {
			if (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY) {

				int lpnQty = lpnDto.getQuantity();
				resvQty = lpnQty;
				return resvQty;
			}
		}
		return resvQty;
	}

	public int getResvQty(ASNDto asnDto) {
		int resvQty = 0;
		if (asnDto != null) {
			if (asnDto.getAsnStatus() == AsnStatusConstants.RECEIVED) {
				List<LpnDto> lpnDtos = asnDto.getLpns();
				for (LpnDto lpnDto : lpnDtos) {

					if (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY) {
						int lpnQty = lpnDto.getQuantity();
						resvQty = resvQty + lpnQty;
					}

				}
				return resvQty;
			}
		}
		return resvQty;
	}

	public String getCategory(ASNDto asnDto) {
		String category = "";
		if (asnDto != null) {

			List<LpnDto> lpnDtos = asnDto.getLpns();
			for (LpnDto lpnDto : lpnDtos) {
				if (category.equals("")) {
					category = lpnDto.getItem().getCategory().getCategory_name();
				} else {
					category = category + " , " + lpnDto.getItem().getCategory().getCategory_name();
				}
				return category;
			}
		}
		return category;
	}

	public String getCategory(LpnDto lpnDto) {
		String category = "";
		if (lpnDto != null) {

			if (category.equals("")) {
				category = lpnDto.getItem().getCategory().getCategory_name();
			} else {
				category = category + " , " + lpnDto.getItem().getCategory().getCategory_name();
			}
			return category;

		}
		return category;
	}

	private SopEligibleItemsDto createSopEligibleItemsDto(LpnDto lpnDto, ASNDto asnDto) {
		SopEligibleItemsDto sopEligibleItemsDto = new SopEligibleItemsDto();
		populateSopEligibleItemsDto(sopEligibleItemsDto, lpnDto, asnDto);
		return sopEligibleItemsDto;
	}

	private SopEligibleItemsDto createSopEligibleItemsDto(LpnDto lpnDto) {
		SopEligibleItemsDto sopEligibleItemsDto = new SopEligibleItemsDto();
		populateSopEligibleItemsDto(sopEligibleItemsDto, lpnDto, null);
		return sopEligibleItemsDto;
	}

	private void populateSopEligibleItemsDto(SopEligibleItemsDto sopEligibleItemsDto, LpnDto lpnDto, ASNDto asnDto) {
//		logger.info("ASN : {}, Lpn : {}, Item : {}", lpnDto.getAsn_brcd(), lpnDto.getLpn_name(),
//				lpnDto.getItem().getItem_name());
		sopEligibleItemsDto.setItem_id(lpnDto.getItem().getItem_id());
		sopEligibleItemsDto.setItem_brcd(lpnDto.getItem().getItem_name());
		sopEligibleItemsDto.setLength(lpnDto.getItem().getUnit_length());
		sopEligibleItemsDto.setWidth(lpnDto.getItem().getUnit_width());
		sopEligibleItemsDto.setHeight(lpnDto.getItem().getUnit_height());

		if (asnDto != null) {
//			logger.info("ASN : {}, Lpn : {}, Item : {}", asnDto.getAsnBrcd(), lpnDto.getLpn_name(),
//					lpnDto.getItem().getItem_name());
			sopEligibleItemsDto.setAsnBrcd(asnDto.getAsnBrcd());
			sopEligibleItemsDto.setResvQty(getResvQty(asnDto));
			sopEligibleItemsDto.setAsnInTranQty(getInTransitQty(asnDto));
			sopEligibleItemsDto.setAsnRcvQty(getRcvQty(asnDto));
			sopEligibleItemsDto.setAsnLpnInfo(getAsnLpnInfo(asnDto, lpnDto));
			sopEligibleItemsDto.setCategory(getCategory(asnDto));
		} else {
//			logger.info("ASN : {}, Lpn : {}, Item : {}", lpnDto.getAsn_brcd(), lpnDto.getLpn_name(),
//					lpnDto.getItem().getItem_name());
//			logger.info("getResvQty(lpnDto) {}, item : {}", getResvQty(lpnDto), lpnDto.getItem().getItem_name());
			sopEligibleItemsDto.setAsnLpnInfo(getAsnLpnInfo(null, lpnDto));
			sopEligibleItemsDto.setResvQty(getResvQty(lpnDto));
			sopEligibleItemsDto.setCategory(getCategory(lpnDto));
		}

		sopEligibleItemsDto.setCreatedDttm(LocalDateTime.now());
		sopEligibleItemsDto.setLastUpdatedDttm(LocalDateTime.now());
		sopEligibleItemsDto.setCreatedSource("sop-assignment-service");
		sopEligibleItemsDto.setLastUpdatedSource("sop-assignment-service");
	}

	@Override
	public String createAssignment(AssignmentModel assignmentModel) throws ClientProtocolException, IOException {
		String actionType = assignmentModel.getSopActionType();
		String category = assignmentModel.getCategory();
		logger.info("Action Type : {} ", actionType);
		String batchId = sopLogWrapper.createBatch(actionType);
		LogEntryDto logEntryDto = new LogEntryDto();

		logEntryDto.setBatchId(batchId);
		logEntryDto.setBatchMode(actionType);
		logEntryDto.setMessage(NEW_BATCH + actionType + String.format(TRIGGERD, category));
		logEntryDto.setCreatedAt(LocalDateTime.now());
		logEntryDto.setServiceName(service_name);
		logEntryDto.setIpAddress(Inet4Address.getLocalHost().getHostAddress());
		sopLogWrapper.createLog(logEntryDto);

		logger.info("Starting to create assignment");
		List<SopEligibleItemsDto> sopEligibleItemsDtos = getEligibleUpcs(category);
		int eligibleUpcsCount = getEligibleUpcsCount(sopEligibleItemsDtos);
		logEntryDto.setModuleName("Eligible UPCs module");
		logEntryDto.setMessage("Total " + eligibleUpcsCount + " Eligible UPCs found ");
		logEntryDto.setCreatedAt(LocalDateTime.now());
		sopLogWrapper.updateBatchStatus(batchId, 20);
		sopLogWrapper.createLog(logEntryDto);
		int count = 0;
		if (sopEligibleItemsDtos.isEmpty()) {
			logEntryDto.setMessage(ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_UPCS);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.updateBatchStatus(batchId, 96);
			sopLogWrapper.createLog(logEntryDto);

			return ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_UPCS;
		}

		logger.info("Eligible UPCs : {}", sopEligibleItemsDtos);
		List<SopEligibleLocationsDto> sopEligibleLocationsDtos = sopConfigWrapper.getEligibleLocations(category);
		logEntryDto.setModuleName("Eligible Locations module");
		logEntryDto.setMessage("Total " + sopEligibleLocationsDtos.size() + " Eligible Locations found ");
		logEntryDto.setCreatedAt(LocalDateTime.now());
		sopLogWrapper.updateBatchStatus(batchId, 30);
		sopLogWrapper.createLog(logEntryDto);

		if (sopEligibleLocationsDtos.isEmpty()) {
			logEntryDto.setMessage(ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_LOCATIONS);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.updateBatchStatus(batchId, 96);
			sopLogWrapper.createLog(logEntryDto);
			return ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_LOCATIONS;
		}

		logger.info("Eligible Locations : {}", sopEligibleLocationsDtos);

		for (SopEligibleLocationsDto sopEligibleLocationsDto : sopEligibleLocationsDtos) {
			for (SopEligibleItemsDto sopEligibleItemsDto : sopEligibleItemsDtos) {
				String itemBrcd = sopEligibleItemsDto.getItem_brcd();
				String locnBrcd = sopEligibleLocationsDto.getLocn_brcd();
				logEntryDto.setItem(sopEligibleItemsDto.getItem_brcd());
				logEntryDto.setLocation(sopEligibleLocationsDto.getLocn_brcd());
				logEntryDto.setModuleName("Assignment Module");
				logEntryDto.setAsn(sopEligibleItemsDto.getAsnBrcd());
				if (sopEligibleLocationsDto.getAssignedNbrOfUpc() >= 0 && sopEligibleLocationsDto.getMaxNbrOfSku() > 0
						&& (sopEligibleLocationsDto.getAssignedNbrOfUpc() < sopEligibleLocationsDto.getMaxNbrOfSku())) {
					if (canAssign(sopEligibleLocationsDto, sopEligibleItemsDto, logEntryDto, sopLogWrapper)) {
						sopLogWrapper.updateBatchStatus(batchId, 45);

						logger.info("Creating Assignment for UPC: {} and Location : {}", itemBrcd, locnBrcd);
						String message = String.format("Creating Assignment for UPC: %s and Location : %s", itemBrcd,
								locnBrcd);
						logEntryDto.setMessage(message);
						sopLogWrapper.createLog(logEntryDto);
						inventoryWrapper.createInventory(itemBrcd, locnBrcd);
						message = String.format("Created Assignment for UPC: %s and Location : %s", itemBrcd, locnBrcd);
						logEntryDto.setCreatedAt(LocalDateTime.now());
						logEntryDto.setMessage(message);
						sopLogWrapper.createLog(logEntryDto);
						count++;
						sopEligibleLocationsDto.setAssignedNbrOfUpc(count);
						sopEligibleLocationsDto.setLastUpdatedDttm(LocalDateTime.now());
						sopEligibleLocationsDto.setLastUpdatedSource("assignment-service");
						sopConfigWrapper.updateSopEligibleLocations(sopEligibleLocationsDto);
						logger.info("Location updated with ");
					} else {
						String message = String.format(FAILED_TO_CREATED_ASSIGNMENT + " : Check Item Dimensions: %s",
								itemBrcd);
						logEntryDto.setMessage(message);
						logEntryDto.setCreatedAt(LocalDateTime.now());
						sopLogWrapper.createLog(logEntryDto);
						logger.info("Check Item Dimensions : {}", itemBrcd);
					}
				} else {
					String message = String.format("Location : %s already assigned with Item : %s", locnBrcd, itemBrcd);
					logEntryDto.setMessage(message);
					logEntryDto.setCreatedAt(LocalDateTime.now());
					sopLogWrapper.createLog(logEntryDto);
					logger.info("Location : {} already assigned with Item : {}", locnBrcd, itemBrcd);
				}
			}
		}
		logger.info("Compare eligibleUpcs : {} and count : {}", eligibleUpcsCount, count);
		if (eligibleUpcsCount == count) {
			logEntryDto.setMessage(ASSIGNMENT_CREATED_SUCCESSFULLY);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.updateBatchStatus(batchId, 90);
			sopLogWrapper.createLog(logEntryDto);
			return ASSIGNMENT_CREATED_SUCCESSFULLY;
		} else {
			logEntryDto.setMessage(FAILED_TO_CREATED_ASSIGNMENT);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.updateBatchStatus(batchId, 96);
			sopLogWrapper.createLog(logEntryDto);
			return FAILED_TO_CREATED_ASSIGNMENT;
		}
	}

	private boolean canAssign(SopEligibleLocationsDto location, SopEligibleItemsDto item, LogEntryDto logEntryDto,
			SopLogWrapper sopLogWrapper) throws ClientProtocolException, IOException {
		// Parameter validation
		if (location == null || item == null) {
			logger.error("Invalid parameters: location={}, item={}", location, item);
			return false;
		}

		// Validate numerical constraints
		if (location.getLength() <= 0 || location.getWidth() <= 0 || item.getLength() <= 0 || item.getWidth() <= 0) {
			logger.warn("Invalid dimensions - Location: {}{}, Item: {}{}", location.getLength(), location.getWidth(),
					item.getLength(), item.getWidth());
			String message = String.format("Invalid dimensions - Location: {}{}, Item: {}{}", location.getLength(),
					location.getWidth(), item.getLength(), item.getWidth());
			logEntryDto.setMessage(message);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
			return false;
		}

		final String itemBrcd = item.getItem_brcd();
		boolean inventoryExists = inventoryWrapper.checkActiveInventory(itemBrcd);
		logger.info("Inventory check for item {}: {}", itemBrcd, inventoryExists);

		if (inventoryExists) {
			logger.info("Assignment blocked - Active inventory exists for item {}", itemBrcd);
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
			String message = String.format("Location : %s already assigned", location.getLocn_brcd());
			logEntryDto.setMessage(message);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
			logger.info("Location : {} already assigned", location.getLocn_brcd());

		}

		// Occupied location checks
		boolean hasCapacity = assignedUpc < maxSkuCapacity;
		boolean fitsDimensions = fitsInLocation(location, item, logEntryDto, sopLogWrapper);
		boolean meetsAssignmentCriteria = hasCapacity && fitsDimensions;

		logger.info("Occupied location check - Capacity: {}/{} ({}), Fits: {}", assignedUpc, maxSkuCapacity,
				hasCapacity, fitsDimensions);

		return meetsAssignmentCriteria;
	}

	private boolean fitsInLocation(SopEligibleLocationsDto location, SopEligibleItemsDto item, LogEntryDto logEntryDto,
			SopLogWrapper sopLogWrapper) throws ClientProtocolException, IOException {

		boolean isfits = false;
		if (location.getLength() >= item.getLength() && location.getWidth() >= item.getWidth()
				&& location.getHeight() >= item.getHeight()) {
			isfits = true;
		} else {
			String message = String.format("Invalid dimensions - Location: {}{}, Item: {}{}", location.getLength(),
					location.getWidth(), item.getLength(), item.getWidth());
			logEntryDto.setMessage(message);
			logEntryDto.setCreatedAt(LocalDateTime.now());
			sopLogWrapper.createLog(logEntryDto);
		}
		return isfits; // Added height check if available
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
