package com.pawar.sop.assignment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sop.log.service")
public class SopLogServiceConfiguration {

	@Value("${sop.log.service.create.batch}")
	private String createBatchURL;

	@Value("${sop.log.service.update.batch}")
	private String updateBatchURL;

	@Value("${sop.log.service.create.log}")
	private String createLogURL;

	public String getCreateBatchURL() {
		return createBatchURL;
	}

	public void setCreateBatchURL(String createBatchURL) {
		this.createBatchURL = createBatchURL;
	}

	public String getUpdateBatchURL() {
		return updateBatchURL;
	}

	public void setUpdateBatchURL(String updateBatchURL) {
		this.updateBatchURL = updateBatchURL;
	}

	public String getCreateLogURL() {
		return createLogURL;
	}

	public void setCreateLogURL(String createLogURL) {
		this.createLogURL = createLogURL;
	}

}
