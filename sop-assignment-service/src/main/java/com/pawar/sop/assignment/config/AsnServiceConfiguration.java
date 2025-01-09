package com.pawar.sop.assignment.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "asn.service")
public class AsnServiceConfiguration {

	@Value("${asn.service}")
	private String asnService;

	@Value("${asn.service.asns.category}")
	private String asnsByCategory;

	public String getAsnService() {
		return asnService;
	}

	public void setAsnService(String asnService) {
		this.asnService = asnService;
	}

	public String getAsnsByCategory() {
		return asnsByCategory;
	}

	public void setAsnsByCategory(String asnsByCategory) {
		this.asnsByCategory = asnsByCategory;
	}

	public String getASNByCategoryURL() {
		String asnService = getAsnService();
		String asnsByCategory = getAsnsByCategory();
		return asnService + asnsByCategory;

	}
}
