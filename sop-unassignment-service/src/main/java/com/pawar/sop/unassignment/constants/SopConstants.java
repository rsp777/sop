package com.pawar.sop.unassignment.constants;

public interface SopConstants {
	public static final String ELIGIBLE_LOCATIONS_MODULE = "Eligible Locations module";

	public static final String SERVICE_NAME = "unassignment-service";
	public static final String NEW_BATCH = "New batch started: ";
	public static final String TRIGGERED = " triggered for category: %s";
	public static final String UNASSIGNMENT_INTERRUPTED = "Unassignment interrupted";
	public static final String NO_ELIGIBLE_UPCS = "No eligible UPCs found";
	public static final String NO_ELIGIBLE_LOCATIONS_FOR_UNASSIGN = "No eligible locations found for unassign";
	public static final String UNASSIGNMENT_COMPLETED_SUCCESSFULY = "Unassignment completed successfully";
	public static final String FAILED_TO_COMPLETE_UNASSIGNMENT = "Failed to complete unassignment";
//	public static final String ASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_UPCS = UNASSIGNMENT_INTERRUPTED + " : "
//			+ NO_ELIGIBLE_UPCS;
	public static final String UNASSIGNMENT_INTERRUPTED_NO_ELIGIBLE_LOCATIONS = UNASSIGNMENT_INTERRUPTED + " : "
			+ NO_ELIGIBLE_LOCATIONS_FOR_UNASSIGN;
	public static final String INVALID_ACTION_TYPE_PLEASE_USE_UNASSIGN_SERVICE = ""
			+ "Invalid Action Type %s Please use assign service.";

	public static final String ELIGIBLE_ACTIVE_INVENTORY_FETCHED = "Eligible Active Inventory Fetched";
	public static final String ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND = "Eligible Active Inventory Not Found";

	
	public static final String CURRENT_ACTIVE_INVENTORY = "Current Active Inventory : Item - %s | Location - %s";
	public static final String UNASSIGN_COMPLETED_FOR_ITEM_LOCATION = "Unassign Completed for Item - %s | Location - %s";
	public static final String UNASSIGN_BATCH_COMPLETED_SUCCESSFULLY = "Unassign Batch Completed Successfully";


}
