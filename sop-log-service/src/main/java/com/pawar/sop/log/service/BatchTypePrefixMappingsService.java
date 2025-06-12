package com.pawar.sop.log.service;

import java.util.List;

import com.pawar.sop.log.model.BatchTypePrefixMappings;

public interface BatchTypePrefixMappingsService {
	BatchTypePrefixMappings addPrefixMapping(Long batchTypeId, String prefix);

	BatchTypePrefixMappings getPrefixMapping(Long id);

	List<BatchTypePrefixMappings> getAllPrefixMappings();

	BatchTypePrefixMappings updatePrefixMapping(Long id, String newPrefix);

	void deletePrefixMapping(Long id);
}
