package com.pawar.sop.assignment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sop.config.service")
public class SopConfigServiceConfiguration {

	@Value("${invn.locations.service.get.range}")
	private String locationRangeURL;

	@Value("${sop.config.service.eligible.locations.category}")
	private String eligibleLocationsURL;

	@Value("${sop.config.service.eligible.locations.update}")
	private String eligibleLocationsUpdateURL;

	public String getLocationRangeURL() {
		return locationRangeURL;
	}

	public void setLocationRange(String locationRangeURL) {
		this.locationRangeURL = locationRangeURL;
	}

	public void setLocationRangeURL(String locationRangeURL) {
		this.locationRangeURL = locationRangeURL;
	}

	public String getEligibleLocationsURL() {
		return eligibleLocationsURL;
	}

	public void setEligibleLocationsURL(String eligibleLocationsURL) {
		this.eligibleLocationsURL = eligibleLocationsURL;
	}

	public String getEligibleLocationsUpdateURL() {
		return eligibleLocationsUpdateURL;
	}

	public void setEligibleLocationsUpdateURL(String eligibleLocationsUpdateURL) {
		this.eligibleLocationsUpdateURL = eligibleLocationsUpdateURL;
	}

}