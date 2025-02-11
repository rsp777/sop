package com.pawar.sop.log.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.sop.log.model.LogEntry;
import com.pawar.sop.log.service.LogEntryService;


@RestController
@RequestMapping("/sop-log-service")
public class LogEntryController {
	
	private final static Logger logger = LoggerFactory.getLogger(LogEntryController.class);

	
	@Autowired
	private LogEntryService logEntryService;

	@PostMapping("/create-batch")
	public ResponseEntity<String> createBatch(@RequestParam String actionType) {
		try {
			logger.info("Action Type : {}",actionType);
			String batchId = logEntryService.createNewBatch(actionType);
			logger.info("{} Batch created Successfully : {}",batchId,actionType);

			return new ResponseEntity<>(batchId, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Failed to Create Batch : {}",e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<String>("Failed to Create Batch: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping("/update-batch")
	public ResponseEntity<String> updateBatch(@RequestParam String batchId, @RequestParam int batchStatus) {
		logEntryService.updateBatchStatus(batchId, batchStatus);
		return new ResponseEntity<>("Batch status updated successfully", HttpStatus.OK);
	}

	@PostMapping("/logs")
	public ResponseEntity<String> createLog(@RequestBody LogEntry logEntry) {
		logEntryService.saveLog(logEntry);
		return new ResponseEntity<>("Log entry created successfully", HttpStatus.OK);
	}

}
