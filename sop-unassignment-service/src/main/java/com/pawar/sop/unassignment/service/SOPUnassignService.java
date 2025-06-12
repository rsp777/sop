package com.pawar.sop.unassignment.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.entity.AssignmentModel;

public interface SOPUnassignService {

	String unassign(AssignmentModel assignmentModel)  throws ClientProtocolException, IOException ;

}
