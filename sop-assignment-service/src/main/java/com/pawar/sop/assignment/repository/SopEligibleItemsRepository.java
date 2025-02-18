package com.pawar.sop.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pawar.sop.assignment.model.SopEligibleItems;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;



@Repository
public interface SopEligibleItemsRepository extends JpaRepository<SopEligibleItems, Integer> {
	
	Optional<SopEligibleItems> findByAsnBrcdAndItemBrcd(String asnBrcd,String itemBrcd);
	List<SopEligibleItems> findByItemBrcd(String itemBrcd);

	
	
	@Transactional
	void deleteByItemId(int item_id);
	List<SopEligibleItems> findByIsAssigned(String string);

	
}
