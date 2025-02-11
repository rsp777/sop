package com.pawar.sop.log.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pawar.sop.log.model.Batch;
import com.pawar.sop.log.model.LogEntry;
import com.pawar.sop.log.repository.BatchRepository;
import com.pawar.sop.log.repository.LogRepository;

import jakarta.transaction.Transactional;

@Service
public class LogEntryService {

	private final static Logger logger = LoggerFactory.getLogger(LogEntryService.class);

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private BatchRepository batchRepository;

	@Async
	public void saveLog(LogEntry logEntry) {

		logRepository.save(logEntry);
		logger.info("Saved Log : {}", logEntry);
	}

	@Transactional
	public String createNewBatch(String actionType) throws ParseException {
		 String prefix = actionType.equalsIgnoreCase("ASSIGN") ? "BTA-" : "BTU-";

		    // Get the current date in yyyyMMdd format
		    String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		    logger.info("datePart : {}", datePart);

		    // Fetch the last batch ID for today
		    Batch lastBatch = batchRepository.fetchLatestBatch(); // Assuming this method fetches the latest batch
		    int nextNumber = 1; // Default to 1 if no last batch is found

		    if (lastBatch != null) {
		        String lastBatchId = lastBatch.getBatchId();
		        
		        // Check if the last batch ID matches today's date
		        if (lastBatchId.startsWith(prefix + datePart)) {
		            // Extract the numeric part from the last batch ID
		            String numericPart = lastBatchId.substring(lastBatchId.length() - 2); // Get the last two digits
		            nextNumber = Integer.parseInt(numericPart) + 1; // Increment the number
		        }
		    }

		    // Create the new batch ID
		    String newBatchId = prefix + datePart + String.format("%02d", nextNumber); // Format to ensure two digits
		    logger.info("New Batch ID: {}", newBatchId);

		    // Save the new batch ID
		    Batch batchId = new Batch();
		    batchId.setBatchId(newBatchId);
		    batchId.setCreatedAt(LocalDateTime.now());
		    batchRepository.save(batchId);

		    return newBatchId;
	}

	public void updateBatchStatus(String batchId, int status) {
		logger.info("Updating batch status {}for batchId: {}",status, batchId);

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
		Batch batch = batchRepository.findByBatchId(batchId);

		if (batch != null) {
			// Update the batch status
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
