package com.pawar.sop.assignment.log.wrapper;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.sop.assignment.config.AsnServiceConfiguration;
import com.pawar.sop.assignment.config.InventoryServiceConfiguration;
import com.pawar.sop.assignment.httputils.HttpUtils;
import com.pawar.sop.http.exception.RestClientException;
import com.pawar.sop.http.service.HttpService;

@Component
public class InventoryWrapper {

	private final static Logger logger = LoggerFactory.getLogger(InventoryWrapper.class);

	private final AsnServiceConfiguration asnServiceConfiguration;
	private final InventoryServiceConfiguration inventoryServiceConfiguration;
	private final HttpUtils httpUtils;
	private final HttpService httpService;
	private final ObjectMapper objectMapper;

//	public InventoryWrapper(InventoryServiceConfiguration inventoryServiceConfiguration, HttpUtils httpUtils) {
//		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
//		this.httpUtils = httpUtils;
//	}

	public InventoryWrapper(AsnServiceConfiguration asnServiceConfiguration,
			InventoryServiceConfiguration inventoryServiceConfiguration, HttpUtils httpUtils, HttpService httpService) {
		this.asnServiceConfiguration = asnServiceConfiguration;
		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
		this.httpUtils = httpUtils;
		this.httpService = httpService;
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	public boolean checkActiveInventory(String itemName) {
		boolean isExists = false;
		String json = createInventoryJson(itemName, null);
		logger.info("check Active Inventory Payload : " + json);
		String url = inventoryServiceConfiguration.getCheckActiveInventoryURL();
		logger.info("URL : {}", url);
		try {
			ResponseEntity<String> response = httpService.restCall(url, HttpMethod.POST, json, null);
			logger.info("Response : {}", response);

//			logger.info("Response Body : {}", response.getBody());
			if (!response.toString().contains("404")) {
				isExists = true;
			}
			logger.info("isExists : " + isExists);
			return isExists;
		} catch (RestClientException e) {
			return isExists;
		}
	}

	private String createInventoryJson(String itemName, String locnBrcd) {
		JSONObject inventory_json = new JSONObject();
		JSONObject inventory = new JSONObject();
		JSONObject item = new JSONObject();
		item.put("itemName", itemName);
		inventory.put("item", item);
		if (locnBrcd != null) {
			inventory.put("location", locnBrcd);

		}

		inventory_json.put("inventory", inventory);
		logger.info("{}", inventory_json);
		String json = inventory_json.toString();
		return json;
	}

	public void createInventory(String itemBrcd, String locnBrcd) {
		String json = createInventoryJson(itemBrcd, locnBrcd);
		logger.info("json ; " + json);
		String url = inventoryServiceConfiguration.getCreateInventoryURL();
		logger.info("CREATE_INVENTORY : {}", url);
		String response = httpService.restCall(url, HttpMethod.POST, json, null).getBody().toString();
		logger.info(response);
	}

	public List<ASNDto> fetchASNs(String category) throws JsonMappingException, JsonProcessingException {
		String url = asnServiceConfiguration.getASNByCategoryURL().replace("{category}", category);
		logger.info("Get ASN URL : {}", url);
		String json = httpService.restCall(url, HttpMethod.GET, null, null).getBody().toString();
		List<ASNDto> fetchedASNDtos = objectMapper.readValue(json, new TypeReference<List<ASNDto>>() {
		});
		return fetchedASNDtos;
	}

	public List<LpnDto> fetchLpns(String category) throws JsonMappingException, JsonProcessingException {
		String url = inventoryServiceConfiguration.getGetLpnByCategoryURL().replace("{category}", category);
		logger.info("Get LPN URL : {}", url);
		String json = httpService.restCall(url, HttpMethod.GET, null, null).getBody().toString();
		logger.info(json);
		List<LpnDto> fetchedLpnDtos = objectMapper.readValue(json, new TypeReference<List<LpnDto>>() {
		});
		return fetchedLpnDtos;
	}
}