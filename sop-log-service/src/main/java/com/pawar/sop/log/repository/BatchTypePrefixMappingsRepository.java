package com.pawar.sop.log.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pawar.sop.log.model.BatchType;
import com.pawar.sop.log.model.BatchTypePrefixMappings;

import jakarta.persistence.Tuple;

@Repository
public interface BatchTypePrefixMappingsRepository extends JpaRepository<BatchTypePrefixMappings , Long> {

	Optional<BatchType> findByBatchType_Id(Long batchTypeId);

	@Query("SELECT b.prefix FROM BatchTypePrefixMappings b WHERE b.batchType.id = :batchTypeId")
	Optional<String> findPrefixByBatchTypeId(Long batchTypeId);

}
