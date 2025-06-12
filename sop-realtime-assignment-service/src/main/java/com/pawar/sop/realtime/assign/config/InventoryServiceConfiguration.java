package com.pawar.sop.realtime.assign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "invn")
public class InventoryServiceConfiguration {

	@Value("${invn.inventory.service.active.sop}")
	private String createInventoryURL;

	@Value("${invn.lpns.service.get.category}")
	private String getLpnByCategoryURL;
	
	@Value("${invn.inventory.service.checkActiveInventory}")
	private String checkActiveInventoryURL;
	
	@Value("${invn.inventory.service.get.by.item.name}")
	private String getInventoryByItemName;
	
	@Value("${invn.inventory.service.get.by.locn.brcd}")
	private String getInventoryByLocnBrcd;
	
	@Value("${invn.inventory.service.getExistingInventories}")
	private String getExistingInventories;
	
	@Value("${invn.inventory.service.delete.active.inventory}")
	private String deleteInventoryURL;
	
	public String getCreateInventoryURL() {
		return createInventoryURL;
	}

	public void setCreateInventoryURL(String createInventoryURL) {
		this.createInventoryURL = createInventoryURL;
	}

	public String getGetLpnByCategoryURL() {
		return getLpnByCategoryURL;
	}

	public void setGetLpnByCategoryURL(String getLpnByCategoryURL) {
		this.getLpnByCategoryURL = getLpnByCategoryURL;
	}

	public String getCheckActiveInventoryURL() {
		return checkActiveInventoryURL;
	}

	public void setCheckActiveInventoryURL(String checkActiveInventoryURL) {
		this.checkActiveInventoryURL = checkActiveInventoryURL;
	}

	public String getGetInventoryByItemName() {
		return getInventoryByItemName;
	}

	public void setGetInventoryByItemName(String getInventoryByItemName) {
		this.getInventoryByItemName = getInventoryByItemName;
	}

	public String getGetInventoryByLocnBrcd() {
		return getInventoryByLocnBrcd;
	}

	public void setGetInventoryByLocnBrcd(String getInventoryByLocnBrcd) {
		this.getInventoryByLocnBrcd = getInventoryByLocnBrcd;
	}

	public String getGetExistingInventories() {
		return getExistingInventories;
	}

	public void setGetExistingInventories(String getExistingInventories) {
		this.getExistingInventories = getExistingInventories;
	}

	public String getDeleteInventoryURL() {
		return deleteInventoryURL;
	}

	public void setDeleteInventoryURL(String deleteInventoryURL) {
		this.deleteInventoryURL = deleteInventoryURL;
	}
}
