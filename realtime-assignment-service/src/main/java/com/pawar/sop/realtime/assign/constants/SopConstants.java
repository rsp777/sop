package com.pawar.sop.realtime.assign.constants;

public interface SopConstants {
	public static final String ELIGIBLE_LOCATIONS_MODULE = "Eligible Locations module";
	public static final String ASSIGN = "ASSIGN";
	public static final String REALTIMEASSIGN = "REALTIMEASSIGN";
	public static final String SERVICE_NAME = "realtime-assignment-service";
	public static final String NEW_BATCH = "New batch started: ";
	public static final String TRIGGERED = " triggered for item : ";
	public static final String ASSIGNMENT_INTERRUPTED = "Unassignment interrupted";
	public static final String NO_ELIGIBLE_UPCS = "No eligible UPCs found";
	public static final String NO_ELIGIBLE_LOCATIONS_FOR_UNASSIGN = "No eligible locations found for unassign";
	public static final String UNASSIGNMENT_COMPLETED_SUCCESSFULY = "Unassignment completed successfully";
	public static final String FAILED_TO_COMPLETE_UNASSIGNMENT = "Failed to complete unassignment";
	public static final String ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS = ASSIGNMENT_INTERRUPTED + " : "
			+ NO_ELIGIBLE_UPCS;
	public static final String ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS = ASSIGNMENT_INTERRUPTED + " : "
			+ NO_ELIGIBLE_LOCATIONS_FOR_UNASSIGN;
	public static final String INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE = ""
			+ "Invalid Action Type %s Please use assign service.";

	public static final String ELIGIBLE_ACTIVE_INVENTORY_FETCHED = "Eligible Active Inventory Fetched";
	public static final String ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND = "Eligible Active Inventory Not Found";

	public static final String MESSAGE_PUBLISHED_TO_TOPIC = "Message published to topic - %s for RealTime Assignment";
	public static final String MESSAGE_FAILED_TO_PUBLISHED_TO_TOPIC = "Message failed to published to topic - %s for RealTime Assignment";

	public static final String CURRENT_ACTIVE_INVENTORY = "Current Active Inventory : Item - %s | Location - %s";
	public static final String ASSIGN_COMPLETED_FOR_ITEM_LOCATION = "Unassign Completed for Item - %s | Location - %s";
	public static final String ASSIGN_BATCH_COMPLETED_SUCCESSFULLY = "Unassign Batch Completed Successfully";
	public static final String ASSIGNMENT_CREATED_SUCCESSFULLY = "Assignment Created Successfully";
	public static final String FAILED_TO_CREATE_ASSIGNMENT = "Failed to Create Assignment";




}
