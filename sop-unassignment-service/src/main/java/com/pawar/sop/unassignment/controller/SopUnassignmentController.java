package com.pawar.sop.unassignment.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
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
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.sop.unassignment.service.SOPUnassignService;


@RestController
@RequestMapping("/sop-unassignment-service")
public class SopUnassignmentController {

	private final static Logger logger = LoggerFactory.getLogger(SopUnassignmentController.class);
	
	@Autowired
	private SOPUnassignService sopUnassignService;
	
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/unassignment/unassign", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> unassign(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		
		AssignmentModel assignmentModel = mapper.convertValue(payload, AssignmentModel.class);
		try {
			
			String response = sopUnassignService.unassign(assignmentModel);
			logger.info(response);
			return new ResponseEntity<String>(response, HttpStatus.OK);
		}  catch (Exception e) {
			logger.error("Failed to Complete Unassignment: ", e);
			return new ResponseEntity<String>("Failed to Complete Unassignment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}  

//	 @GetMapping(value = "/assignment/upcs", produces = "application/json")
//	    public ResponseEntity<List<SopEligibleItemsDto>> getEligibleUpcs() throws ClientProtocolException, IOException {
//	        List<SopEligibleItemsDto> eligibleItems = sopAssignService.getEligibleUpcs("Electronics");
//	        return new ResponseEntity<>(eligibleItems, HttpStatus.OK);
//	    }
//
//	    @GetMapping(value = "location-range/{id}", produces = "application/json")
//	    public ResponseEntity<?> getLocationRangeById(@PathVariable Integer id) {
//	    	SopLocationRangeDto locationRange = sopConfigService.getLocationRangeById(id);
//	        if (locationRange != null) {
//	            return new ResponseEntity<>(locationRange, HttpStatus.OK);
//	        }
//	        return new ResponseEntity<>("Location range not found!", HttpStatus.NOT_FOUND);
//	    }
//
//	    @PutMapping(value = "location-range/update/{id}", consumes = "application/json", produces = "application/json")
//	    public ResponseEntity<?> updateLocationRange(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
//	        ObjectMapper mapper = new ObjectMapper();
//	        mapper.registerModule(new JavaTimeModule());
//	        SopLocationRangeDto sopLocationRangeDto = mapper.convertValue(payload, SopLocationRangeDto.class);
//
//	        String response = sopConfigService.updateLocationRange(id, sopLocationRangeDto);
//	        return new ResponseEntity<>(response, HttpStatus.OK);
//	    }
//
//	    @DeleteMapping(value = "location-range/delete/{id}", produces = "application/json")
//	    public ResponseEntity<?> deleteLocationRange(@PathVariable Integer id ) {
//	        String response = sopConfigService.deleteLocationRange(id);
//	        if (response.contains("deleted")) {
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        }
//	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//	    }
//	
}
