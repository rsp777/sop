package com.pawar.sop.config.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.sop.config.controller.SopConfigController;
import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopEligibleLocations;
import com.pawar.sop.config.model.SopLocationRange;
import com.pawar.sop.config.repository.SopActionTypeRepository;
import com.pawar.sop.config.repository.SopEligibleLocationsRepository;
import com.pawar.sop.config.repository.SopLocationRangeRepository;

@Service
public class SOPConfigServiceImpl implements SOPConfigService {

	private final static Logger logger = LoggerFactory.getLogger(SOPConfigServiceImpl.class);

	private final static String LOCATION_RANGE_ADD_SUCCESS = "Location Range Added Successfully.";
	private final static String LOCATION_RANGE_ADD_FAILED = "Failed to add Location Range.";
	private final static String ACTION_TYPE_ADD_SUCCESS = "Action Type Added Successfully.";
	private final static String ACTION_TYPE_ADD_FAILED = "Failed to add Action Type";
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	@Autowired
	private SopLocationRangeRepository sopLocationRangeRepository;

	@Autowired
	private SopEligibleLocationsRepository sopEligibleLocationsRepository;

	@Autowired
	private SopActionTypeRepository sopActionTypeRepository;

	public SOPConfigServiceImpl() {
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public SopActionType getActionType(String actionType) {
		logger.info("getActionType : {}", actionType);
		SopActionType sopActionType = sopActionTypeRepository.findByActionType(actionType);
		return sopActionType;
	}
	
	@Override
	public String addActionType(SopActionTypeDto sopActionTypeDto) {

		logger.info("New Action Type : {}", sopActionTypeDto);
		try {
			SopActionType sopActionType = new SopActionType(sopActionTypeDto);
			logger.info("Dto convert to entity : {}", sopActionType);
			sopActionTypeRepository.save(sopActionType);
			logger.info(ACTION_TYPE_ADD_SUCCESS);
			return ACTION_TYPE_ADD_SUCCESS;
		} catch (Exception e) {
			logger.error("Error occurred while adding Action Type: {}", e.getMessage(), e);
			return ACTION_TYPE_ADD_FAILED;
		}
	}

	@Override
	public String addLocationRange(SopLocationRangeDto sopLocationRangeDto) {

		logger.info("New Location Range : {}", sopLocationRangeDto);
		try {
			int totalActLocations = 0;
			String fromLocation = sopLocationRangeDto.getFromLocation();
			String toLocation = sopLocationRangeDto.getToLocation();
			SopLocationRange sopLocationRange = new SopLocationRange(sopLocationRangeDto);
			List<Location> locations = (List<Location>) findLocationByLocationRange(fromLocation, toLocation);
			sopLocationRange.setAvlActiveLocations(locations.size());
			sopLocationRange.setTotalActiveLocations(locations.size());
			logger.info("Dto convert to entity : {}", sopLocationRange);
			sopLocationRangeRepository.save(sopLocationRange);
			logger.info(LOCATION_RANGE_ADD_SUCCESS);
			return LOCATION_RANGE_ADD_SUCCESS;
		} catch (Exception e) {
			logger.error("Error occurred while adding Location Range: {}", e.getMessage(), e);
			return LOCATION_RANGE_ADD_FAILED;
		}
	}

	public Iterable<Location> findLocationByLocationRange(String fromLocation, String toLocation)
			throws ClientProtocolException, IOException {

		String url = getUrl("locations", fromLocation, toLocation);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		org.apache.http.HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		List<Location> fetchedLocations = objectMapper.readValue(json, new TypeReference<List<Location>>() {
		});

		return fetchedLocations;

	}

	public String getUrl(String serviceName, String fromLocation, String toLocation) {
		String baseUrl = "http://localhost:8085/";
		String url = baseUrl + serviceName + "/list/by-range?fromLocation=" + fromLocation + "&toLocation="
				+ toLocation;
		return url;
	}

	@Override
	public List<SopLocationRangeDto> getAllLocationRanges() {
		List<SopLocationRange> sopLocationRanges = sopLocationRangeRepository.findAll();
		List<SopLocationRangeDto> sopLocationRangeDtos = new ArrayList<>();

		for (SopLocationRange sopLocationRange : sopLocationRanges) {
			SopLocationRangeDto sopLocationRangeDto = new SopLocationRangeDto(sopLocationRange.getSopLocationRangeId(),
					convertSopActionTypeEntityToDto(sopLocationRange.getSopActionType()),
					sopLocationRange.getCategory(), sopLocationRange.getFromLocation(),
					sopLocationRange.getToLocation(), sopLocationRange.getTotalActiveLocations(),
					sopLocationRange.getUsedActiveLocations(), sopLocationRange.getAvlActiveLocations(),
					sopLocationRange.getIsActive(), sopLocationRange.getCreatedDttm(),
					sopLocationRange.getLastUpdatedDttm(), sopLocationRange.getCreatedSource(),
					sopLocationRange.getLastUpdatedSource());

			sopLocationRangeDtos.add(sopLocationRangeDto);
		}
		return sopLocationRangeDtos;
	}

	public SopActionTypeDto convertSopActionTypeEntityToDto(SopActionType sopActionType) {

		SopActionTypeDto sopActionTypeDto = new SopActionTypeDto(sopActionType.getSopActionTypeId(),
				sopActionType.getActionDesc(), sopActionType.getActionType(), sopActionType.getCreatedDttm(),
				sopActionType.getLastUpdatedDttm(), sopActionType.getCreatedSource(),
				sopActionType.getLastUpdatedSource());
		return sopActionTypeDto;

	}

	@Override
	public SopLocationRangeDto getLocationRangeById(Integer id) {
		SopLocationRange sopLocationRange = sopLocationRangeRepository.findById(id).get();
		SopLocationRangeDto sopLocationRangeDto = new SopLocationRangeDto(sopLocationRange.getSopLocationRangeId(),
				convertSopActionTypeEntityToDto(sopLocationRange.getSopActionType()), sopLocationRange.getCategory(),
				sopLocationRange.getFromLocation(), sopLocationRange.getToLocation(),
				sopLocationRange.getTotalActiveLocations(), sopLocationRange.getUsedActiveLocations(),
				sopLocationRange.getAvlActiveLocations(), sopLocationRange.getIsActive(),
				sopLocationRange.getCreatedDttm(), sopLocationRange.getLastUpdatedDttm(),
				sopLocationRange.getCreatedSource(), sopLocationRange.getLastUpdatedSource());

		return sopLocationRangeDto;
	}

	@Override
	public String deleteLocationRange(Integer id) {
		sopLocationRangeRepository.deleteById(id);
		return "Location Range deleted successfully : " + id;

	}

	@Override
	public String updateLocationRange(Integer id, SopLocationRangeDto sopLocationRangeDto) {
		return null;
	}

	@Override
	public List<SopEligibleLocationsDto> getEligibleLocations(String sopActionType, String category) {
		
		SopActionType actionType = sopActionTypeRepository.findByActionType(sopActionType);
		int sopActionTypeId = actionType.getSopActionTypeId();
		List<SopEligibleLocations> sopEligibleLocations = sopEligibleLocationsRepository.findSopEligibleLocationsByCategoryAndSopActionType(sopActionTypeId,category);
		List<SopEligibleLocationsDto> sopEligibleLocationsDtos = new ArrayList<>();
		for (SopEligibleLocations sopEligibleLocation : sopEligibleLocations) {

			SopEligibleLocationsDto sopEligibleLocationsDto = new SopEligibleLocationsDto(
					sopEligibleLocation.getSopEligibleLocationsId(), sopEligibleLocation.getLocn_id(),
					sopEligibleLocation.getLocn_brcd(), sopEligibleLocation.getGrp(),
					sopEligibleLocation.getAssignedNbrOfUpc(), sopEligibleLocation.getMaxNbrOfSku(),
					convertSopLocationRangeEntitytoDto(sopEligibleLocation.getSopLocationRange()),
					sopEligibleLocation.getCategory(), sopEligibleLocation.getLength(), sopEligibleLocation.getWidth(),
					sopEligibleLocation.getHeight(), sopEligibleLocation.getCreatedDttm(),
					sopEligibleLocation.getLastUpdatedDttm(), sopEligibleLocation.getCreatedSource(),
					sopEligibleLocation.getLastUpdatedSource());

			sopEligibleLocationsDtos.add(sopEligibleLocationsDto);

		}

		return sopEligibleLocationsDtos;
	}

	public SopLocationRangeDto convertSopLocationRangeEntitytoDto(SopLocationRange sopLocationRange) {
		SopLocationRangeDto sopLocationRangeDto = new SopLocationRangeDto(sopLocationRange.getSopLocationRangeId(),
				sopLocationRange.convertSopActionTypeEntityToDto(sopLocationRange.getSopActionType()),
				sopLocationRange.getCategory(), sopLocationRange.getFromLocation(), sopLocationRange.getToLocation(),
				sopLocationRange.getTotalActiveLocations(), sopLocationRange.getUsedActiveLocations(),
				sopLocationRange.getAvlActiveLocations(), sopLocationRange.getIsActive(),
				sopLocationRange.getCreatedDttm(), sopLocationRange.getLastUpdatedDttm(),
				sopLocationRange.getCreatedSource(), sopLocationRange.getLastUpdatedSource());
		return sopLocationRangeDto;
	}

	@Override
	public List<SopEligibleLocationsDto> getEligibleLocationsByCategory(String category) {
		logger.info("category: {}",category);

		List<SopEligibleLocations> sopEligibleLocations = sopEligibleLocationsRepository.findByCategory(category);
		List<SopEligibleLocationsDto> sopEligibleLocationsDtos = new ArrayList<>();
		for (SopEligibleLocations sopEligibleLocation : sopEligibleLocations) {

			SopEligibleLocationsDto sopEligibleLocationsDto = new SopEligibleLocationsDto(
					sopEligibleLocation.getSopEligibleLocationsId(), sopEligibleLocation.getLocn_id(),
					sopEligibleLocation.getLocn_brcd(), sopEligibleLocation.getGrp(),
					sopEligibleLocation.getAssignedNbrOfUpc(), sopEligibleLocation.getMaxNbrOfSku(),
					convertSopLocationRangeEntitytoDto(sopEligibleLocation.getSopLocationRange()),
					sopEligibleLocation.getCategory(), sopEligibleLocation.getLength(), sopEligibleLocation.getWidth(),
					sopEligibleLocation.getHeight(), sopEligibleLocation.getCreatedDttm(),
					sopEligibleLocation.getLastUpdatedDttm(), sopEligibleLocation.getCreatedSource(),
					sopEligibleLocation.getLastUpdatedSource());

			sopEligibleLocationsDtos.add(sopEligibleLocationsDto);

		}

		return sopEligibleLocationsDtos;
	}

	@Override
	public String updateEligibleLocation(SopEligibleLocationsDto sopEligibleLocationsDto) {
		SopEligibleLocations sopEligibleLocations = new SopEligibleLocations(sopEligibleLocationsDto);
		sopEligibleLocationsRepository.save(sopEligibleLocations);
		
		return "Sop Eligible Locations updated";
	}

	@Override
	public List<SopActionType> getActionTypes() {
		List<SopActionType> sopActionType = sopActionTypeRepository.findAll();
		return sopActionType;
	}

	@Override
	public void deleteEligibleLocation(Integer sopEligibleLocationsId) {
		logger.info("Deleting the Eligible Location with Id: {}",sopEligibleLocationsId);
		sopEligibleLocationsRepository.deleteById(sopEligibleLocationsId);;
	}

	
}
