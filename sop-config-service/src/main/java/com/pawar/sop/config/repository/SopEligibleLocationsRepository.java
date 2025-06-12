package com.pawar.sop.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopEligibleLocations;

@Repository
public interface SopEligibleLocationsRepository extends JpaRepository<SopEligibleLocations, Integer> {

	List<SopEligibleLocations> findByCategory(String category);

	@Query(value = "select sel.* from sop_location_range slr inner join sop_eligible_locations sel on slr.sop_location_range_id = sel.sop_location_range_id where slr.sop_action_type_id = :sopActionTypeId and slr.category= :category",nativeQuery = true)
	List<SopEligibleLocations> findSopEligibleLocationsByCategoryAndSopActionType(int sopActionTypeId, String category);

}
