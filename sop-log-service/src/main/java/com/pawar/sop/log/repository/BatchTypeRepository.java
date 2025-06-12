package com.pawar.sop.log.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pawar.sop.log.model.BatchType;

@Repository
public interface BatchTypeRepository extends JpaRepository<BatchType, Long> {

	public Optional<BatchType> findByBatchType(String batchType);

	@Query("SELECT b.id FROM BatchType b WHERE b.batchType = :batchType")
	public Optional<Long> findIdByBatchType(@Param("batchType") String batchType);
	
	@Query("SELECT  COUNT(b) FROM BatchType b where b.id = :id and b.sopActionType.sopActionTypeId = :sopActionTypeId")
	int checkMappingsExists(@Param("id")Long batchTypeId, @Param("sopActionTypeId")Integer sopActionTypeId);
}
