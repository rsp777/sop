package com.pawar.sop.log.controller;

import java.util.List;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.sop.log.model.BatchType;
import com.pawar.sop.log.model.BatchTypePrefixMappings;
import com.pawar.sop.log.model.LogEntry;
import com.pawar.sop.log.service.BatchTypePrefixMappingsService;
import com.pawar.sop.log.service.BatchTypeService;
import com.pawar.sop.log.service.LogEntryService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/sop-log-service")
public class LogEntryController {

	private final static Logger logger = LoggerFactory.getLogger(LogEntryController.class);

	private final BatchTypeService batchTypeService;

	private final BatchTypePrefixMappingsService batchTypePrefixMappingsService;

	public LogEntryController(BatchTypeService batchTypeService,
			BatchTypePrefixMappingsService batchTypePrefixMappingsService) {
		this.batchTypeService = batchTypeService;
		this.batchTypePrefixMappingsService = batchTypePrefixMappingsService;
	}

	@Autowired
	private LogEntryService logEntryService;

	@PostMapping("/create-batch")
	public ResponseEntity<String> createBatch(@RequestParam String actionType, @RequestParam String batchType) {
		try {
			logger.info("Action Type : {} | Batch Type : {}", actionType, batchType);
			String batchId = logEntryService.createBatch(actionType, batchType);

			// If createBatch returns the error message for missing mappings, detect and
			// respond with 400 Bad Request
			if (batchId.equals("Mappings don't exist")) {
				logger.warn("Mappings don't exist for BatchType: {} and ActionType: {}", batchType, actionType);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Mappings don't exist for the given BatchType and ActionType.");
			}
			else if (batchId.contains("Batch is in progress")) {
				logger.warn(batchId);
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body(batchId);
			}
			logger.info("{} Batch created Successfully : {}", batchId, batchType);
			return new ResponseEntity<>(batchId, HttpStatus.CREATED);
		} catch (ParseException e) {
			logger.error("Failed to Create Batch due to invalid input: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Failed to Create Batch : {}", e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("Failed to Create Batch: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

	@PostMapping("/create-batch-type")
	public ResponseEntity<BatchType> createBatchType(@RequestBody BatchType batchType) {
		try {
			batchTypeService.addBatchType(batchType);
			logger.info("Created BatchType: {}", batchType);
			return new ResponseEntity<>(batchType, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Failed to create BatchType: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/batch-type/{id}")
	public ResponseEntity<BatchType> getBatchTypeById(@PathVariable Long id) {
		try {
			BatchType batchType = batchTypeService.getBatchType(id);
			return new ResponseEntity<>(batchType, HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			logger.warn("BatchType not found with id: {}", id);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Error retrieving BatchType by id: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/batch-type/by-type/{batchType}")
	public ResponseEntity<BatchType> getBatchTypeByType(@PathVariable String batchType) {
		try {
			BatchType batchTypeObj = batchTypeService.getBatchType(batchType);
			return new ResponseEntity<>(batchTypeObj, HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			logger.warn("BatchType not found with type: {}", batchType);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Error retrieving BatchType by type: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/batch-types")
	public ResponseEntity<List<BatchType>> getAllBatchTypes() {
		try {
			List<BatchType> batchTypes = batchTypeService.getAllBatchTypes();
			return new ResponseEntity<>(batchTypes, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error retrieving all BatchTypes: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/batch-type/{id}")
	public ResponseEntity<BatchType> updateBatchType(@PathVariable Long id, @RequestBody BatchType batchType) {
		try {
			batchTypeService.updateBatchType(batchType, id);
			BatchType updatedBatchType = batchTypeService.getBatchType(id);
			logger.info("Updated BatchType with id {}: {}", id, updatedBatchType);
			return new ResponseEntity<>(updatedBatchType, HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			logger.warn("BatchType not found for update with id: {}", id);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Failed to update BatchType with id {}: {}", id, e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/batch-type/{id}")
	public ResponseEntity<Void> deleteBatchType(@PathVariable Long id) {
		try {
			batchTypeService.deleteBatchType(id);
			logger.info("Deleted BatchType with id: {}", id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (EntityNotFoundException e) {
			logger.warn("BatchType not found for deletion with id: {}", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Failed to delete BatchType with id {}: {}", id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/batch-type-prefix-mappings")
	public ResponseEntity<?> addPrefixMapping(@RequestParam Long batchTypeId, @RequestParam String prefix) {
		try {
			BatchTypePrefixMappings saved = batchTypePrefixMappingsService.addPrefixMapping(batchTypeId, prefix);
			return ResponseEntity.ok(saved);
		} catch (EntityNotFoundException enfe) {
			logger.warn("BatchType not found for id: {}", batchTypeId);
			return ResponseEntity.badRequest().body(enfe.getMessage());
		} catch (Exception e) {
			logger.error("Error adding prefix mapping: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error adding prefix mapping");
		}
	}

	@GetMapping("/batch-type-prefix-mappings/{id}")
	public ResponseEntity<?> getPrefixMapping(@PathVariable Long id) {
		try {
			BatchTypePrefixMappings mapping = batchTypePrefixMappingsService.getPrefixMapping(id);
			return ResponseEntity.ok(mapping);
		} catch (EntityNotFoundException enfe) {
			logger.warn("Prefix mapping not found for id: {}", id);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("Error retrieving prefix mapping: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error retrieving prefix mapping");
		}
	}

	@GetMapping("/batch-type-prefix-mappings")
	public ResponseEntity<List<BatchTypePrefixMappings>> getAllPrefixMappings() {
		List<BatchTypePrefixMappings> mappings = batchTypePrefixMappingsService.getAllPrefixMappings();
		return ResponseEntity.ok(mappings);
	}

	@PutMapping("/batch-type-prefix-mappings/{id}")
	public ResponseEntity<?> updatePrefixMapping(@PathVariable Long id, @RequestParam String prefix) {
		try {
			BatchTypePrefixMappings updated = batchTypePrefixMappingsService.updatePrefixMapping(id, prefix);
			return ResponseEntity.ok(updated);
		} catch (EntityNotFoundException enfe) {
			logger.warn("Prefix mapping not found for update id: {}", id);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("Error updating prefix mapping: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error updating prefix mapping");
		}
	}

	@DeleteMapping("/batch-type-prefix-mappings/{id}")
	public ResponseEntity<?> deletePrefixMapping(@PathVariable Long id) {
		try {
			batchTypePrefixMappingsService.deletePrefixMapping(id);
			return ResponseEntity.noContent().build();
		} catch (EntityNotFoundException enfe) {
			logger.warn("Prefix mapping not found for delete id: {}", id);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("Error deleting prefix mapping: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error deleting prefix mapping");
		}
	}

}
