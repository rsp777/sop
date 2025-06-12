package com.pawar.sop.realtime.assign.service;

import java.util.List;

import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.sop.realtime.assign.model.SopEligibleItems;

public interface SopEligibleItemsService {

	public SopEligibleItemsDto findBySopEligibleItemsId(Integer sopEligibleItemsId);
	public List<SopEligibleItemsDto> findByAll();
	public List<SopEligibleItemsDto> findByCategory(String category);
}