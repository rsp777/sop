package com.pawar.sop.log.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.pawar.sop.log.model.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	@Query(value = "SELECT * FROM batch WHERE DATE(created_at) = CURRENT_DATE ORDER BY created_at DESC LIMIT 1",nativeQuery = true)
	Batch fetchLatestBatch();
	Batch findByBatchId(String batchId);

}
