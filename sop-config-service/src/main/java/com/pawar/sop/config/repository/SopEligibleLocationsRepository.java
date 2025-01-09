package com.pawar.sop.config.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopEligibleLocations;


@Repository
public interface SopEligibleLocationsRepository extends JpaRepository<SopEligibleLocations, Integer> {

	List<SopEligibleLocations> findByCategory(String category);

}
