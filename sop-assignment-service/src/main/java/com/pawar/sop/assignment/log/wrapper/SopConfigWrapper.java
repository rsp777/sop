package com.pawar.sop.assignment.log.wrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.sop.assignment.config.SopConfigServiceConfiguration;
import com.pawar.sop.http.service.HttpService;

@Component
public class SopConfigWrapper {

	private final static Logger logger = LoggerFactory.getLogger(SopConfigWrapper.class);

	private final SopConfigServiceConfiguration sopConfigServiceConfiguration;

	@Autowired
	private final HttpService httpService;

	private final ObjectMapper objectMapper;

	public SopConfigWrapper(HttpService httpService, SopConfigServiceConfiguration sopConfigServiceConfiguration) {
		this.httpService = httpService;
		this.sopConfigServiceConfiguration = sopConfigServiceConfiguration;
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	Map<String, Object> queryParams;

	public void updateSopEligibleLocations(SopEligibleLocationsDto sopEligibleLocationsDto)
			throws ClientProtocolException, IOException {
		String url = sopConfigServiceConfiguration.getEligibleLocationsUpdateURL();
		logger.info("ELIGIBILE_LOCATIONS_UPDATE : {}", url);

		httpService.restCall(url, HttpMethod.PUT, sopEligibleLocationsDto, null);

		logger.info("Response SopEligibleLocations updated");

	}

	public List<SopEligibleLocationsDto> getEligibleLocations(String category)
			throws ClientProtocolException, IOException {

		String url = sopConfigServiceConfiguration.getEligibleLocationsURL().replace("{category}", category);
		logger.info("ELIGIBILE_LOCATIONS_GET : {}", url);

		ResponseEntity<Object> response = httpService.restCall(url, HttpMethod.GET, null, null);
//		response.getBody().toString().replace("[", "").replace("]", "");
		String json = response.getBody().toString();
		logger.info("Json : {}", json);
		
		List<SopEligibleLocationsDto> fetchedSopEligibleLocationsDtos =
				objectMapper.readValue(json, new TypeReference<List<SopEligibleLocationsDto>>() {
				});

		logger.info("fetchedSopEligibleLocationsDtos : {}", fetchedSopEligibleLocationsDtos);
		return fetchedSopEligibleLocationsDtos;

	}

}
