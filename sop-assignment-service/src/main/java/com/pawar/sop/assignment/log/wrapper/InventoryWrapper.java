package com.pawar.sop.assignment.log.wrapper;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
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

import com.pawar.sop.assignment.config.InventoryServiceConfiguration;
import com.pawar.sop.assignment.httputils.HttpUtils;
import com.pawar.sop.http.exception.RestClientException;
import com.pawar.sop.http.service.HttpService;

@Component
public class InventoryWrapper {

	private final static Logger logger = LoggerFactory.getLogger(SopLogWrapper.class);

	private final InventoryServiceConfiguration inventoryServiceConfiguration;
	private final HttpUtils httpUtils;
	private final HttpService httpService;
	ResponseEntity<String> response;
//	public InventoryWrapper(InventoryServiceConfiguration inventoryServiceConfiguration, HttpUtils httpUtils) {
//		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
//		this.httpUtils = httpUtils;
//	}

	public InventoryWrapper(InventoryServiceConfiguration inventoryServiceConfiguration, HttpUtils httpUtils,
			HttpService httpService) {
		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
		this.httpUtils = httpUtils;
		this.httpService = httpService;
	}

	public boolean checkActiveInventory(String itemName) {
		boolean isExists = false;
		String json = createInventoryJson(itemName,null);
		logger.info("check Active Inventory Payload : " + json);
		String url = inventoryServiceConfiguration.getCheckActiveInventoryURL();
		logger.info("URL : {}", url);
		try {
			response = httpService.restCall(url, HttpMethod.POST, json, null);
			logger.info("Response : {}", response.getBody());
			if (!response.getBody().contains("404")) {
				isExists = true;
			}

			logger.info("isExists : " + isExists);
			return isExists;
		} catch (RestClientException e) {

			return isExists;

		}
	}

	public void createInventory(String itemName, String locnBrcd) throws ClientProtocolException, IOException {
		String json = createInventoryJson(itemName, locnBrcd);
		logger.info("json ; " + json);
		String url = inventoryServiceConfiguration.getCreateInventoryURL();
		logger.info("CREATE_INVENTORY : {}", url);	
		logger.info("Json : {}",json);
		ResponseEntity<Object> responseEntity = httpService.restCall(url, HttpMethod.POST, json,null);
		String response = responseEntity.getBody().toString();
		logger.info(response);
	}

	private String createInventoryJson(String itemName, String locnBrcd) {
		String json = "";
		JSONObject inventory_json = new JSONObject();
		JSONObject inventory = new JSONObject();
		JSONObject item = new JSONObject();

		item.put("item_name", itemName);
		
		if (itemName != null && locnBrcd != null) {
			inventory.put("item", item);
			inventory_json.put("location", locnBrcd);
		} else if (itemName != null && locnBrcd == null) {
			inventory.put("item", item);
		}

		inventory_json.put("inventory", inventory);
		logger.info("{}", inventory_json);
		json = inventory_json.toString();
		return json;
	}

}
