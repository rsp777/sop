package com.pawar.sop.config.service;

import java.util.List;

import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopEligibleLocations;

public interface SOPConfigService {

	String addLocationRange(SopLocationRangeDto sopLocationRangeDto);

	String addActionType(SopActionTypeDto sopLocationRangeDto);

	List<SopLocationRangeDto> getAllLocationRanges();

	SopLocationRangeDto getLocationRangeById(Integer id);

	String deleteLocationRange(Integer id);

	String updateLocationRange(Integer id, SopLocationRangeDto sopLocationRangeDto);

	List<SopEligibleLocationsDto> getEligibleLocations(String sopActionType, String category);

	List<SopEligibleLocationsDto> getEligibleLocationsByCategory(String category);

	String updateEligibleLocation(SopEligibleLocationsDto sopEligibleLocationsDto);

	SopActionType getActionType(String actionType);

	List<SopActionType> getActionTypes();

	void deleteEligibleLocation(Integer sopEligibleLocationsId);

}
