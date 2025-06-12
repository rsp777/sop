package com.pawar.sop.log.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pawar.sop.log.model.Batch;
import com.pawar.sop.log.model.BatchType;
import com.pawar.sop.log.model.LogEntry;
import com.pawar.sop.log.model.SopActionType;
import com.pawar.sop.log.repository.BatchRepository;
import com.pawar.sop.log.repository.BatchTypePrefixMappingsRepository;
import com.pawar.sop.log.repository.BatchTypeRepository;
import com.pawar.sop.log.repository.LogRepository;
import com.pawar.sop.log.repository.SopActionTypeRepository;

import jakarta.transaction.Transactional;

@Service
public class LogEntryService {

	private final static Logger logger = LoggerFactory.getLogger(LogEntryService.class);

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private BatchRepository batchRepository;

	@Autowired
	private BatchTypeRepository batchTypeRepository;

	@Autowired
	private BatchTypePrefixMappingsRepository batchTypePrefixMappingsRepository;

	@Autowired
	private SopActionTypeRepository sopActionTypeRepository;

	@Async
	public void saveLog(LogEntry logEntry) {

		logRepository.save(logEntry);
		logger.info("Saved Log : {}", logEntry);
	}

	public String createBatch(String actionType, String batchType) throws ParseException {
		logger.info("actionType : {} | batchType ; {}", actionType,batchType);

		Batch lastBatch = batchRepository.fetchLatestBatch();
		if (lastBatch == null) {
			return generateBatch(actionType, batchType, null);
		} else {
			logger.info("Batch : {}", lastBatch);
			logger.info("Batch Type : {} and Status ; {}", lastBatch.getBatchType(), lastBatch.getStatus());
			if (lastBatch.getStatus() == 0) {
				logger.info(String.format("Batch is in progress : %s, Please wait till it is completed.",
						lastBatch.getBatchId()));
				return String.format("Batch is in progress : %s, Please wait till it is completed.",
						lastBatch.getBatchId());
			} else {
				return generateBatch(actionType, batchType, lastBatch);
			}
		}

	}

	private String generateBatch(String actionType, String batchType, Batch lastBatch) throws ParseException {
		int count = 0;
		
		
		SopActionType fetchedSopActionType = sopActionTypeRepository.findByActionType(actionType);
		logger.info("fetchedSopActionType : {}",fetchedSopActionType);
		if (fetchedSopActionType == null) {
			logger.error("Invalid action type: {}", actionType);
			return "Invalid action type: " + actionType;
		}
		BatchType fetchedBatchType = batchTypeRepository.findByBatchType(batchType)
				.orElseThrow(() -> new ParseException("Batch type not found: " + batchType, 0));
		count = validBatchTypeMappings(fetchedBatchType.getId(), fetchedSopActionType.getSopActionTypeId());
		logger.info("count : {}", count);
		if (count == 0) {
			logger.error("Mappings don't exist for BatchType ID: {} and SopActionType ID: {}", fetchedBatchType.getId(),
					fetchedSopActionType.getSopActionTypeId());
			return "Mappings don't exist";
		}
		return createNewBatch(batchType, lastBatch);
	}

	private int validBatchTypeMappings(Long batchTypeId, Integer sopActionTypeId) {
		return batchTypeRepository.checkMappingsExists(batchTypeId, sopActionTypeId);
	}

	@Transactional
	public String createNewBatch(String batchType, Batch lastBatch) throws ParseException {

	    logger.info("Creating new batch for batchType: {}, Last batch: {}", batchType, lastBatch);

	    // Fetch the batch_type_id based on the batch type
	    Long batchTypeId = batchTypeRepository.findIdByBatchType(batchType)
	            .orElseThrow(() -> new ParseException("Batch type not found: " + batchType, 0));

	    
	    // Fetch the prefix from the mapping using the batch_type_id
	    String prefix = batchTypePrefixMappingsRepository.findPrefixByBatchTypeId(batchTypeId)
	            .orElseThrow(() -> new ParseException("Prefix not found for batchType: " + batchType, 0));

	    // Get current date in yyyyMMdd format
	    String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	    int nextNumber = 1;

	    if (lastBatch != null) {
	        String lastBatchId = lastBatch.getBatchId();
//	        if (lastBatchId.startsWith(prefix + datePart)) {
	            String numericPart = lastBatchId.substring(prefix.length() + datePart.length());
	            try {
	                nextNumber = Integer.parseInt(numericPart) + 1;
	            } catch (NumberFormatException e) {
	                logger.warn("Invalid numeric part in lastBatchId: {}. Resetting to 1.", lastBatchId);
	                nextNumber = 1; // fallback
	            }
//	        } 
//	    else {
//	            logger.info("Last batch ID does not match expected prefix and date. Resetting to 1.");
//	        }
	    }

	    String newBatchId = prefix + datePart + String.format("%02d", nextNumber);
	    logger.info("New Batch Number : {}", newBatchId);

	    // Save the new batch
	    BatchType fetchedBatchType = batchTypeRepository.findByBatchType(batchType)
	            .orElseThrow(() -> new ParseException("Batch type not found: " + batchType, 0));

	    Batch newBatch = new Batch();
	    newBatch.setBatchId(newBatchId);
	    newBatch.setCreatedAt(LocalDateTime.now());
	    newBatch.setBatchType(fetchedBatchType);
	    batchRepository.save(newBatch);

	    return newBatchId;
	}

	

	public void updateBatchStatus(String batchId, int status) {
		logger.info("Updating batch status {} for batchId: {}", status, batchId);

		// Create a new thread to update the batch status
		Thread thread = new Thread(() -> {
			try {
				// Update the batch status
				updateBatchStatusInDatabase(batchId, status);

				// Log the update
			} catch (Exception e) {
				logger.error("Error updating batch status for batchId: {}", batchId, e);
			}
		});

		// Start the thread
		thread.start();
	}

	private void updateBatchStatusInDatabase(String batchId, int status) {
		// Find the batch status by batchId
	    Optional<Batch> optionalBatch = batchRepository.findByBatchId(batchId);

		if (optionalBatch.isPresent()) {
			// Update the batch status
			Batch batch = optionalBatch.get();
			batch.setStatus(status);
			batch.setUpdatedAt(LocalDateTime.now());

			// Save the updated batch status
			batchRepository.save(batch);
			
		} else {
			// Create a new batch status if it doesn't exist
			Batch newBatch = new Batch();
			newBatch.setBatchId(batchId);
			newBatch.setStatus(status);
			newBatch.setCreatedAt(LocalDateTime.now());
			newBatch.setUpdatedAt(LocalDateTime.now());

			// Save the new batch status
			batchRepository.save(newBatch);
		}
	}

}
