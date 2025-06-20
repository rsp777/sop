package com.pawar.sop.realtime.assign.wrapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.sop.http.service.HttpService;
import com.pawar.sop.realtime.assign.config.SopConfigServiceConfiguration;

@Component
public class SopConfigWrapper {

	private final static Logger logger = LoggerFactory.getLogger(SopLogWrapper.class);

	private final SopConfigServiceConfiguration sopConfigServiceConfiguration;
	private final HttpService httpService;
	private final ObjectMapper objectMapper;

	public SopConfigWrapper(SopConfigServiceConfiguration sopConfigServiceConfiguration, HttpService httpService) {
		this.sopConfigServiceConfiguration = sopConfigServiceConfiguration;
		this.httpService = httpService;
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	public void updateSopEligibleLocations(SopEligibleLocationsDto sopEligibleLocationsDto)
			throws JsonProcessingException {
		String url = sopConfigServiceConfiguration.getEligibleLocationsUpdateURL();
		logger.info("ELIGIBILE_LOCATIONS_UPDATE : {}", url);
		String sopEligibleLocationsJson = objectMapper.writeValueAsString(sopEligibleLocationsDto);
		logger.info("sopEligibleLocationsJson : " + sopEligibleLocationsJson);
		httpService.restCall(url, HttpMethod.PUT, sopEligibleLocationsJson, null);
		logger.info("Response SopEligibleLocations updated");
	}

	public List<SopEligibleLocationsDto> getEligibleLocations(String actionType,String category)
			throws JsonMappingException, JsonProcessingException {
		String url = sopConfigServiceConfiguration.getEligibleLocationsURL().replace("{category}", category).replace("{sopActionType}",actionType);
		logger.info("ELIGIBILE_LOCATIONS_GET : {}", url);
		String json = httpService.restCall(url, HttpMethod.GET, null, null).getBody().toString();
		List<SopEligibleLocationsDto> fetchedSopEligibleLocationsDtos = objectMapper.readValue(json,
				new TypeReference<List<SopEligibleLocationsDto>>() {
				});
		logger.info("fetchedSopEligibleLocationsDtos : {}", fetchedSopEligibleLocationsDtos);
		return fetchedSopEligibleLocationsDtos;
	}

	public Iterable<Location> findLocationByLocationRange(String fromLocation, String toLocation)
			throws JsonMappingException, JsonProcessingException {
		String url = sopConfigServiceConfiguration.getLocationRangeURL().replace("{fromLocation}", fromLocation)
				.replace("{toLocation}", toLocation);
		logger.info("LOCATION_RANGE_GET URL : {}", url);
		String json = httpService.restCall(url, HttpMethod.GET, null, null).getBody().toString();
		List<Location> fetchedLocations = objectMapper.readValue(json, new TypeReference<List<Location>>() {
		});
		return fetchedLocations;
	}
}
