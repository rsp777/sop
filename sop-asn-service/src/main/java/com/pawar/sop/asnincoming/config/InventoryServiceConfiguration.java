package com.pawar.sop.asnincoming.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "invn")
public class InventoryServiceConfiguration {

	@Value("${invn.asn}")
	private String invnAsnURL;

	@Value("${invn.asn.list}")
	private String invnAsnListURL;
	
	@Value("${invn.asn.list.category}")
	private String invnAsnListCategoryURL;

	public String getInvnAsnURL() {
		return invnAsnURL;
	}

	public void setInvnAsnURL(String invnAsnURL) {
		this.invnAsnURL = invnAsnURL;
	}

	public String getInvnAsnListURL() {
		return invnAsnListURL;
	}

	public void setInvnAsnListURL(String invnAsnListURL) {
		this.invnAsnListURL = invnAsnListURL;
	}

	public String getInvnAsnListCategoryURL() {
		return invnAsnListCategoryURL;
	}

	public void setInvnAsnListCategoryURL(String invnAsnListCategoryURL) {
		this.invnAsnListCategoryURL = invnAsnListCategoryURL;
	}
	
}
