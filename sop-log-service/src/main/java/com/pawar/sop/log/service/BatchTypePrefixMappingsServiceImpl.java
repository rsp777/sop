package com.pawar.sop.log.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pawar.sop.log.model.BatchType;
import com.pawar.sop.log.model.BatchTypePrefixMappings;
import com.pawar.sop.log.repository.BatchTypePrefixMappingsRepository;
import com.pawar.sop.log.repository.BatchTypeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BatchTypePrefixMappingsServiceImpl implements BatchTypePrefixMappingsService {

	private static final Logger logger = LoggerFactory.getLogger(BatchTypePrefixMappingsServiceImpl.class);

	private final BatchTypePrefixMappingsRepository prefixMappingsRepository;
    private final BatchTypeRepository batchTypeRepository;
    
    public BatchTypePrefixMappingsServiceImpl(BatchTypePrefixMappingsRepository prefixMappingsRepository,
                                              BatchTypeRepository batchTypeRepository) {
        this.prefixMappingsRepository = prefixMappingsRepository;
        this.batchTypeRepository = batchTypeRepository;
    }
	
	@Override
	public BatchTypePrefixMappings addPrefixMapping(Long batchTypeId, String prefix) {
		BatchType batchType = batchTypeRepository.findById(batchTypeId)
	            .orElseThrow(() -> new EntityNotFoundException("BatchType not found with id: " + batchTypeId));
	        BatchTypePrefixMappings mapping = new BatchTypePrefixMappings();
	        mapping.setBatchType(batchType);
	        mapping.setPrefix(prefix);
	        BatchTypePrefixMappings saved = prefixMappingsRepository.save(mapping);
	        logger.info("Added BatchTypePrefixMappings: id={}, batchType={}, prefix={}",
	                saved.getId(), batchType.getBatchType(), prefix);
	        return saved;
	}

	@Override
	public BatchTypePrefixMappings getPrefixMapping(Long id) {
		return prefixMappingsRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Prefix mapping not found with id: " + id));
	}

	@Override
	public List<BatchTypePrefixMappings> getAllPrefixMappings() {
		 return prefixMappingsRepository.findAll();
	}

	@Override
	public BatchTypePrefixMappings updatePrefixMapping(Long id, String newPrefix) {
		BatchTypePrefixMappings existing = getPrefixMapping(id);
        existing.setPrefix(newPrefix);
        BatchTypePrefixMappings updated = prefixMappingsRepository.save(existing);
        logger.info("Updated PrefixMapping id={} with new prefix={}", id, newPrefix);
        return updated;
	}

	@Override
	public void deletePrefixMapping(Long id) {
		BatchTypePrefixMappings existing = getPrefixMapping(id);
        prefixMappingsRepository.delete(existing);
        logger.info("Deleted PrefixMapping id={}", id);

	}
}