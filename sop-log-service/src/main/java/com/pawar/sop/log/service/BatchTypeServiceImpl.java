package com.pawar.sop.log.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pawar.sop.log.model.BatchType;
import com.pawar.sop.log.repository.BatchTypeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BatchTypeServiceImpl implements BatchTypeService {

	private static final Logger logger = LoggerFactory.getLogger(BatchTypeServiceImpl.class);

	@Autowired
	private BatchTypeRepository batchTypeRepository;

	@Override
	public void addBatchType(BatchType batchType) throws EntityNotFoundException{
		try {
			
			Optional<BatchType> batchType2 =  batchTypeRepository.findByBatchType(batchType.getBatchType());

			if (batchType2.isPresent()) {
				if (batchType2.get().getBatchType().equals(batchType.getBatchType())) {
					logger.error("batch Type already exists");
				}
			}
			else {
				batchType.setCreatedAt(LocalDateTime.now());
				batchType.setUpdatedAt(LocalDateTime.now());
				batchTypeRepository.save(batchType);
				logger.info("BatchType added successfully: {}", batchType);
			}
			
		} catch (Exception e) {
			logger.error("Error adding BatchType: {}", e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}

	@Override
	public BatchType getBatchType(String batchType) {
		try {
			Optional<BatchType> batchType2 = batchTypeRepository.findByBatchType(batchType);
			if (batchType2 == null) {
				logger.warn("BatchType not found for type: {}", batchType);
				throw new EntityNotFoundException("BatchType not found for type: " + batchType);
			}
			logger.info("Retrieved BatchType: {}", batchType2);
			return batchType2.get();
		} catch (Exception e) {
			logger.error("Error retrieving BatchType: {}", e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}

	@Override
	public BatchType getBatchType(Long id) {
		try {
			Optional<BatchType> batchTypeOptional = batchTypeRepository.findById(id);
			if (!batchTypeOptional.isPresent()) {
				logger.warn("BatchType not found for id: {}", id);
				throw new EntityNotFoundException("BatchType not found for id: " + id);
			}
			BatchType batchType2 = batchTypeOptional.get();
			logger.info("Retrieved BatchType: {}", batchType2);
			return batchType2;
		} catch (Exception e) {
			logger.error("Error retrieving BatchType by id: {}", e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}

	@Override
	public List<BatchType> getAllBatchTypes() {
		try {
			List<BatchType> batchTypes = batchTypeRepository.findAll();
			logger.info("Retrieved all BatchTypes, count: {}", batchTypes.size());
			return batchTypes;
		} catch (Exception e) {
			logger.error("Error retrieving all BatchTypes: {}", e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}

	@Override
	public void updateBatchType(BatchType batchType, Long id) {
		try {
			BatchType existingBatchType = getBatchType(id);
			existingBatchType.setBatchType(batchType.getBatchType());
			batchTypeRepository.save(existingBatchType);
			logger.info("Updated BatchType with id {}: {}", id, existingBatchType);
		} catch (Exception e) {
			logger.error("Error updating BatchType with id {}: {}", id, e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}

	@Override
	public void deleteBatchType(Long id) {
		try {
			BatchType batchType2 = getBatchType(id);
			batchTypeRepository.delete(batchType2);
			logger.info("Deleted BatchType with id: {}", id);
		} catch (Exception e) {
			logger.error("Error deleting BatchType with id {}: {}", id, e.getMessage(), e);
			throw e; // Rethrow or handle as needed
		}
	}
}