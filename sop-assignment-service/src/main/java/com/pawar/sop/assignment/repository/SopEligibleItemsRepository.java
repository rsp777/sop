package com.pawar.sop.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pawar.sop.assignment.model.SopEligibleItems;
import java.util.List;



@Repository
public interface SopEligibleItemsRepository extends JpaRepository<SopEligibleItems, Integer> {
	
	List<SopEligibleItems> findByAsnBrcdAndItemBrcd(String asnBrcd,String itemBrcd);

	
}
