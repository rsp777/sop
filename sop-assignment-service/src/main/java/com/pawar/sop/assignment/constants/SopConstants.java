package com.pawar.sop.assignment.constants;

public interface SopConstants {
	public static final String ELIGIBLE_UPCS_MODULE = "Eligible UPCs module";
	public static final String SERVICE_NAME = "assignment-service";
	public static final String NEW_BATCH = "New batch started: ";
	public static final String TRIGGERED = " triggered for category: %s";
	public static final String ASSIGNMENT_INTERRUPTED = "Assignment interrupted";
	public static final String NO_ELIGIBLE_UPCS = "No eligible UPCs found";
	public static final String NO_ELIGIBLE_LOCATIONS = "No eligible locations found";
	public static final String ASSIGNMENT_CREATED_SUCCESSFULLY = "Assignment created successfully";
	public static final String FAILED_TO_CREATE_ASSIGNMENT = "Failed to create assignment";
	public static final String ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS = ASSIGNMENT_INTERRUPTED + " : "
			+ NO_ELIGIBLE_UPCS;
	public static final String ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS = ASSIGNMENT_INTERRUPTED + " : "
			+ NO_ELIGIBLE_LOCATIONS;
	public static final String INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE = ""
			+ "Invalid Action Type %s Please use unassign service.";
}
