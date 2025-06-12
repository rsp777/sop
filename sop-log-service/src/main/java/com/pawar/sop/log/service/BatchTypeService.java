package com.pawar.sop.log.service;

import java.util.List;

import com.pawar.sop.log.model.BatchType;

public interface BatchTypeService {

	public void addBatchType(BatchType batchType);
	public BatchType getBatchType(String batchType);
	public BatchType getBatchType(Long id);
	public List<BatchType> getAllBatchTypes();
	public void updateBatchType(BatchType batchType,Long id);
	public void deleteBatchType(Long id);
}
