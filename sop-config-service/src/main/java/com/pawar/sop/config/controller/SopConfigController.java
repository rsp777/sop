package com.pawar.sop.config.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopEligibleLocations;
import com.pawar.sop.config.service.SOPConfigService;

@RestController
@RequestMapping("/sop-config-service")
public class SopConfigController {

	private final static Logger logger = LoggerFactory.getLogger(SopConfigController.class);

	@Autowired
	private SOPConfigService sopConfigService;
	
	
	@GetMapping(value = "/action-type")
	public ResponseEntity<?> getActionTypes() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());


		try {
			List<SopActionType> actionTypes = sopConfigService.getActionTypes();
			logger.info(""+actionTypes);
			return new ResponseEntity<List<SopActionType>>(actionTypes, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping(value = "/action-type/{actionType}")
	public ResponseEntity<?> getActionType(@PathVariable String actionType) {
		logger.info("Action Type  : " + actionType);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());


		try {
			SopActionType response = sopConfigService.getActionType(actionType);
			logger.info(""+response);
			return new ResponseEntity<SopActionType>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/action-type/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addActionType(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		SopActionTypeDto sopLocationRangeDto = mapper.convertValue(payload, SopActionTypeDto.class);

		try {
			String response = sopConfigService.addActionType(sopLocationRangeDto);
			logger.info(response);
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/location-range/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addLocationRange(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		logger.info("Location Range : " + payload);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		SopLocationRangeDto sopLocationRangeDto = mapper.convertValue(payload, SopLocationRangeDto.class);
		logger.info("payload Location Range : " + sopLocationRangeDto);

		try {
			String response = sopConfigService.addLocationRange(sopLocationRangeDto);
			logger.info(response);
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping(value = "/location-range/list", produces = "application/json")
	public ResponseEntity<List<SopLocationRangeDto>> getAllLocationRanges() {
		List<SopLocationRangeDto> locationRanges = sopConfigService.getAllLocationRanges();
		logger.info("Location Ranges : {}",locationRanges);
		return new ResponseEntity<>(locationRanges, HttpStatus.OK);
	}

	@GetMapping(value = "/location-range/{id}", produces = "application/json")
	public ResponseEntity<?> getLocationRangeById(@PathVariable Integer id) {
		SopLocationRangeDto locationRange = sopConfigService.getLocationRangeById(id);
		if (locationRange != null) {
			return new ResponseEntity<>(locationRange, HttpStatus.OK);
		}
		return new ResponseEntity<>("Location range not found!", HttpStatus.NOT_FOUND);
	}

	@PutMapping(value = "/location-range/update/{id}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateLocationRange(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		SopLocationRangeDto sopLocationRangeDto = mapper.convertValue(payload, SopLocationRangeDto.class);

		String response = sopConfigService.updateLocationRange(id, sopLocationRangeDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = "/location-range/delete/{id}", produces = "application/json")
	public ResponseEntity<?> deleteLocationRange(@PathVariable Integer id) {
		String response = sopConfigService.deleteLocationRange(id);
		if (response.contains("deleted")) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@GetMapping(value = "/eligible-locations/sopActionType/{sopActionType}/category/{category}", produces = "application/json")
	public ResponseEntity<List<SopEligibleLocationsDto>> getEligibleLocations(@PathVariable String sopActionType,@PathVariable String category) {
		List<SopEligibleLocationsDto> eligibleLocations = sopConfigService.getEligibleLocations(sopActionType,category);
		return new ResponseEntity<>(eligibleLocations, HttpStatus.OK);
	}
	
	@PutMapping(value = "/eligible-locations/update", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateEligibleLocation(@RequestBody Map<String, Object> payload) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		SopEligibleLocationsDto sopEligibleLocationsDto = mapper.convertValue(payload, SopEligibleLocationsDto.class);
		logger.info("sopEligibleLocationsDto : "+sopEligibleLocationsDto);
		String response = sopConfigService.updateEligibleLocation(sopEligibleLocationsDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/eligible-locations/delete/id/{id}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> deleteEligibleLocation(@PathVariable Integer id) {
		logger.info("Deleting Eligible Location with Id : {]",id);
		sopConfigService.deleteEligibleLocation(id);
		String response = "Success";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/eligible-locations/category/{category}", produces = "application/json")
	public ResponseEntity<List<SopEligibleLocationsDto>> getEligibleLocationsByCategory(@PathVariable String category) {
		logger.info("category: {}",category);
		List<SopEligibleLocationsDto> eligibleLocations = sopConfigService.getEligibleLocationsByCategory(category);
		return new ResponseEntity<>(eligibleLocations, HttpStatus.OK);
	}

}
