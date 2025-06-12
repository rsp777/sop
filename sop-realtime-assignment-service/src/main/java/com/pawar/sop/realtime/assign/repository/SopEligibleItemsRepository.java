package com.pawar.sop.realtime.assign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pawar.sop.realtime.assign.model.SopEligibleItems;

import jakarta.persistence.NamedNativeQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SopEligibleItemsRepository extends JpaRepository<SopEligibleItems, Integer> {

	Optional<SopEligibleItems> findByAsnBrcdAndItemBrcd(String asnBrcd, String itemBrcd);

	List<SopEligibleItems> findByItemBrcd(String itemBrcd);

	List<SopEligibleItems> findByCategory(String category);

	public SopEligibleItems findBysopEligibleItemsId(Integer sopEligibleItemsId);

	@Transactional
	void deleteByItemId(int item_id);

	List<SopEligibleItems> findByIsAssigned(String isAssigned);

	List<SopEligibleItems> findByCategoryAndIsAssigned(String category, String isAssigned);

	@Query(value = "select distinct s.category from sop_eligible_items s where s.item_brcd = :item_brcd", nativeQuery = true)
	String findDistinctByItemBrcd(@Param("item_brcd") String itemBrcd);

}
