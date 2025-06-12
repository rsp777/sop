package com.pawar.sop.unassignment.wrapper;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.pawar.inventory.entity.BatchDto;
import com.pawar.inventory.entity.LogEntryDto;

import com.pawar.sop.http.service.HttpService;
import com.pawar.sop.unassignment.config.SopLogServiceConfiguration;

@Component
public class SopLogWrapper {

	private final static Logger logger = LoggerFactory.getLogger(SopLogWrapper.class);

	private final SopLogServiceConfiguration sopLogServiceConfiguration;
	
	@Autowired
	private final HttpService httpService;
	
//	public SopLogWrapper(SopLogServiceConfiguration sopLogServiceConfiguration, HttpUtils httpUtils) {
//		this.sopLogServiceConfiguration = sopLogServiceConfiguration;
//		this.httpUtils = httpUtils;
//	}
	
	public SopLogWrapper(SopLogServiceConfiguration sopLogServiceConfiguration, HttpService httpService) {
		this.sopLogServiceConfiguration = sopLogServiceConfiguration;
		this.httpService = httpService;
	}
	
	Map<String, Object> queryParams;
	@SuppressWarnings("unlikely-arg-type")
	public String createBatch(String actionType, String batchType) throws ClientProtocolException, IOException {
		String batchId = "";
		String url = sopLogServiceConfiguration.getCreateBatchURL();
		logger.info("Create Batch URL : {}", url);
		logger.info("Creating New Batch with actionType : {} and batchType : {}", actionType,batchType);
		queryParams = Map.of("actionType",actionType,"batchType",batchType);
		logger.info("queryParams : {}",queryParams);
		ResponseEntity<String> response = (ResponseEntity<String>) httpService.restCall(url, HttpMethod.POST, actionType,queryParams);
		logger.info("response : {}",response);

		batchId = response.getBody();
		logger.info("Created New Batch {} with status : {}", batchId, actionType);
		return batchId;
	}

	public void updateBatchStatus(String batchId, int batchStatus) throws ClientProtocolException, IOException {
		String url = sopLogServiceConfiguration.getUpdateBatchURL();
		logger.info("Updating Batch {} with status : {}", batchId, batchStatus);
		BatchDto batchDto = new BatchDto(batchId, batchStatus);
		queryParams = Map.of("batchId",batchId,"batchStatus",batchStatus);
		ResponseEntity<String> response =  (ResponseEntity<String>) httpService.restCall(url, HttpMethod.PUT, batchDto,queryParams);
		batchId = response.getBody();
		logger.info("Updated Batch {} with status : {}", batchId, batchStatus);
	}

	public void createLog(LogEntryDto logEntryDto) throws ClientProtocolException, IOException {
		String url = sopLogServiceConfiguration.getCreateLogURL();
		logger.info("Create Log URL : {}", url);
		logger.info("Writing Log for Batch : {}", logEntryDto.getBatchId());
		httpService.restCall(url, HttpMethod.POST, logEntryDto,null);
		logger.info("Written Log for Batch : {}", logEntryDto.getBatchId());
	}

}
