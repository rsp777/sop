package com.pawar.sop.assignment.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
import com.pawar.sop.assignment.model.SopEligibleItems;
import com.pawar.sop.assignment.repository.SopEligibleItemsRepository;

@Service
public class SOPAssignServiceImpl implements SOPAssignService {

	private final static Logger logger = LoggerFactory.getLogger(SOPAssignServiceImpl.class);

	private final static String LOCATION_RANGE_ADD_SUCCESS = "Location Range Added Successfully.";
	private final static String LOCATION_RANGE_ADD_FAILED = "Failed to add Location Range.";
	private final static String ACTION_TYPE_ADD_SUCCESS = "Action Type Added Successfully.";
	private final static String ACTION_TYPE_ADD_FAILED = "Failed to add Action Type";
	private final static String FAILED_TO_CREATED_ASSIGNMENT = "Failed to Create Assignment";
	private final static String ASSIGNMENT_INTERRUPTED = "Assignment Interrupted";
	private final static String NO_ELIGIBLE_UPCS = "No Eligible UPCs";
	private final static String NO_ELIGIBLE_LOCATIONS = "No Eligible Locations";

//	@Value("${asn.service.get}")
//	private static String ASN_GET;
//
//	@Value("${invn.create.inventory}")
//	private static String CREATE_INVENTORY;
//
//	@Value("${sop.config.service.location.range}")
//	private static String LOCATION_RANGE_GET;
//
//	@Value("${sop.config.service.eligible.locations.get}")
//	private static String ELIGIBILE_LOCATIONS_GET;
//
//	@Value("${sop.config.service.eligible.locations.update}")
//	private static String ELIGIBILE_LOCATIONS_UPDATE;

	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private final SopConfigServiceConfiguration sopConfigServiceConfiguration;
	private final InventoryServiceConfiguration inventoryServiceConfiguration;
	private final AsnServiceConfiguration asnServiceConfiguration;

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

	public Iterable<Location> findLocationByLocationRange(String fromLocation, String toLocation)
			throws ClientProtocolException, IOException {

		String url = sopConfigServiceConfiguration.getLocationRangeURL().replace("{fromLocation}", fromLocation)
				.replace("{toLocation}", toLocation);
		// LOCATION_RANGE_GET.replace("{fromLocation}",
		// fromLocation).replace("{toLocation}", toLocation);
		logger.info("Get Location Range URL : {}", url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		List<Location> fetchedLocations = objectMapper.readValue(json, new TypeReference<List<Location>>() {
		});

		return fetchedLocations;

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
		List<ASNDto> fetchedASNDtos = fetchASNs(category);
		List<LpnDto> fetchedLpnDtos = fetchLpns(category);
//		logger.info("fetchedASNDtos : {}", fetchedASNDtos);
//		logger.info("fetchedLpnDtos : {}", fetchedLpnDtos);

		for (ASNDto asnDto : fetchedASNDtos) {
			List<LpnDto> lpnDtos = asnDto.getLpns();
			for (LpnDto lpnDto : lpnDtos) {
//				logger.info("ASN : {}, Lpn : {}, Item : {}", asnDto.getAsnBrcd(), lpnDto.getLpn_name(),
//						lpnDto.getItem().getItem_name());
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
				}
			}
		}
		if (fetchedASNDtos.isEmpty()) {
			for (LpnDto lpnDto : fetchedLpnDtos) {

				if (lpnDto.getLpn_facility_status() == LpnFacilityStatusContants.PUTAWAY) {
					sopEligibleItemsDtos.add(createSopEligibleItemsDto(lpnDto));

				} else {
					logger.info("No Eligible UPCs");
				}

			}
		}
		return sopEligibleItemsDtos;
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

	public List<ASNDto> fetchASNs(String category) throws ClientProtocolException, IOException {
		String url = asnServiceConfiguration.getASNByCategoryURL().replace("{category}", category);
		logger.info("Get ASN URL : {}", url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		List<ASNDto> fetchedASNDtos = objectMapper.readValue(json, new TypeReference<List<ASNDto>>() {
		});
		return fetchedASNDtos;
	}

	public List<LpnDto> fetchLpns(String category) throws ClientProtocolException, IOException {
		String url = inventoryServiceConfiguration.getGetLpnByCategoryURL().replace("{category}", category);
		logger.info("Get LPN URL : {}", url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		List<LpnDto> fetchedLpnDtos = objectMapper.readValue(json, new TypeReference<List<LpnDto>>() {
		});
		return fetchedLpnDtos;
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

//	public String getASNUrl(String serviceName, String category) {
//		String url = SOP_ASN_BASE_URL + serviceName + "/asns?category=" + category;
//		return url;
//	}

//	public String getLocationRangeURL(String serviceName, String fromLocation, String toLocation) {
//		String url = INVMS_BASE_URL + serviceName + "/list/by-range?fromLocation=" + fromLocation + "&toLocation="
//				+ toLocation;
//		return url;
//	}

	@Override
	public String createAssignment(AssignmentModel assignmentModel) throws ClientProtocolException, IOException {
		logger.info("Starting to create assignment");
		String category = assignmentModel.getCategory();
		List<SopEligibleItemsDto> sopEligibleItemsDtos = getEligibleUpcs(category);
		int count = 0;
		if (sopEligibleItemsDtos.isEmpty()) {
			return ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_UPCS;
		}

		logger.info("Eligible UPCs : {}", sopEligibleItemsDtos);
		List<SopEligibleLocationsDto> sopEligibleLocationsDtos = getEligibleLocations(category);

		if (sopEligibleLocationsDtos.isEmpty()) {
			return ASSIGNMENT_INTERRUPTED + " : " + NO_ELIGIBLE_LOCATIONS;
		}

		logger.info("Eligible Locations : {}", sopEligibleLocationsDtos);
		for (SopEligibleLocationsDto sopEligibleLocationsDto : sopEligibleLocationsDtos) {
			for (SopEligibleItemsDto sopEligibleItemsDto : sopEligibleItemsDtos) {
				String itemBrcd = sopEligibleItemsDto.getItem_brcd();
				String locnBrcd = sopEligibleLocationsDto.getLocn_brcd();

				if (sopEligibleLocationsDto.getAssignedNbrOfUpc() == 0
						&& sopEligibleLocationsDto.getMaxNbrOfSku() > 0) {
					if (canAssign(sopEligibleLocationsDto, sopEligibleItemsDto)) {
						logger.info("Creating Assignment for UPC: {} and Location : {}", itemBrcd, locnBrcd);
						createInventory(itemBrcd, locnBrcd);
						count++;
						sopEligibleLocationsDto.setAssignedNbrOfUpc(count);
						sopEligibleLocationsDto.setLastUpdatedDttm(LocalDateTime.now());
						sopEligibleLocationsDto.setLastUpdatedSource("assignment-service");
						updateSopEligibleLocations(sopEligibleLocationsDto);
						logger.info("Assignment Created Successfully");
						return "Assignment Created Successfully";
					} else {
						logger.info("Check Item Dimensions : {} : {}", itemBrcd, sopEligibleItemsDto.getItem_id());
					}
				} else {
					logger.info("Location : {} already assigned with Item : {}", locnBrcd, itemBrcd);
//	                return String.format("Location: %s already assigned with Item: %s", locnBrcd, itemBrcd);
				}
			}
		}
		return FAILED_TO_CREATED_ASSIGNMENT;
	}

	private boolean canAssign(SopEligibleLocationsDto location, SopEligibleItemsDto item) {
		return location.getLength() >= item.getLength() && location.getWidth() >= item.getLength();
	}

	private void updateSopEligibleLocations(SopEligibleLocationsDto sopEligibleLocationsDto)
			throws ClientProtocolException, IOException {
		String url = sopConfigServiceConfiguration.getEligibleLocationsUpdateURL();
		logger.info("ELIGIBILE_LOCATIONS_UPDATE : {}", url);
		String sopEligibleLocationsJson = objectMapper.writeValueAsString(sopEligibleLocationsDto);
		logger.info("sopEligibleLocationsJson : " + sopEligibleLocationsJson);
		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpPut httpPut = new HttpPut(url);
		StringEntity entity = new StringEntity(sopEligibleLocationsJson, ContentType.APPLICATION_JSON);
		httpPut.setEntity(entity);
		// HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);

		httpClient.execute(httpPut);
		// logger.info("entity : "+entity);
//		String json2 = EntityUtils.toString(entity);
		// RestTemplate restTemplate = new RestTemplate();
		// String response2 = restTemplate.postForObject(url, httpEntity, String.class);
		// String response = restTemplate.exchange(url, null, httpEntity, String.class);

		logger.info("Response SopEligibleLocations updated");

	}

	public void createInventory(String itemName, String locnBrcd) throws ClientProtocolException, IOException {
		JSONObject inventory_json = new JSONObject();
		JSONObject item = new JSONObject();
		JSONObject location = new JSONObject();
		inventory_json.put("item", itemName);
		inventory_json.put("location", locnBrcd);
		String json = inventory_json.toString();

		logger.info("json ; " + json);

		String url = inventoryServiceConfiguration.getCreateInventoryURL();
		logger.info("CREATE_INVENTORY : {}", url);

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.postForObject(url, httpEntity, String.class);
		logger.info(response);
	}

	public List<SopEligibleLocationsDto> getEligibleLocations(String category)
			throws ClientProtocolException, IOException {

		String url = sopConfigServiceConfiguration.getEligibleLocationsURL().replace("{category}", category);
		logger.info("ELIGIBILE_LOCATIONS_GET : {}", url);
		// "http://localhost:8089/sop-config-service/eligible-locations/category/" +
		// category;
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		List<SopEligibleLocationsDto> fetchedSopEligibleLocationsDtos = objectMapper.readValue(json,
				new TypeReference<List<SopEligibleLocationsDto>>() {
				});

		logger.info("fetchedSopEligibleLocationsDtos : {}", fetchedSopEligibleLocationsDtos);
		return fetchedSopEligibleLocationsDtos;

	}

}
