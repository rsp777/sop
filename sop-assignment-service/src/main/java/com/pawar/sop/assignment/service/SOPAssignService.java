package com.pawar.sop.assignment.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

public interface SOPAssignService {

	List<SopEligibleItemsDto> getEligibleUpcs(String category) throws ClientProtocolException, IOException;

	String createAssignment(AssignmentModel assignmentModel) throws ClientProtocolException, IOException;

	
}
