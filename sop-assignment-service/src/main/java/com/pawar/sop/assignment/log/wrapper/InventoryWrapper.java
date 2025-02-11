package com.pawar.sop.assignment.log.wrapper;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.pawar.sop.assignment.config.InventoryServiceConfiguration;
import com.pawar.sop.assignment.httputils.HttpUtils;

@Component
public class InventoryWrapper {

	private final static Logger logger = LoggerFactory.getLogger(SopLogWrapper.class);

	private final InventoryServiceConfiguration inventoryServiceConfiguration;
	private final HttpUtils httpUtils;
	
	public InventoryWrapper(InventoryServiceConfiguration inventoryServiceConfiguration, HttpUtils httpUtils) {
		this.inventoryServiceConfiguration = inventoryServiceConfiguration;
		this.httpUtils = httpUtils;
	}
	public boolean checkActiveInventory(String itemName) {
		boolean isExists = false;
		String json = createInventoryJson(itemName);
		logger.info("check Active Inventory Payload : " + json);
		String url = inventoryServiceConfiguration.getCheckActiveInventoryURL();
		logger.info("URL : {}", url);
		ResponseEntity<String> response = httpUtils.restCall(url, HttpMethod.POST, json, null);
		logger.info("Response : {}",response.getBody());
		if (!response.getBody().contains("404")) {
			isExists = true;
		}
		else {
			
		}
		logger.info("isExists : " + isExists);
		return isExists;
	}

	private String createInventoryJson(String itemName) {
		JSONObject inventory_json = new JSONObject();
		JSONObject inventory = new JSONObject();
		JSONObject item = new JSONObject();
		item.put("item_name", itemName);
		inventory.put("item", item);
		inventory_json.put("inventory", inventory);
		logger.info("{}", inventory_json);
		String json = inventory_json.toString();
		return json;
	}

}
