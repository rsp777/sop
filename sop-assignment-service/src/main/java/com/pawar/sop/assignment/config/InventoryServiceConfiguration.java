package com.pawar.sop.assignment.config;

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

}
