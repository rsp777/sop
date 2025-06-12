package com.pawar.sop.realtime.assign.constants;

public interface BatchStatus {
	public static final int ELIGIBLE_ACTIVE_INVENTORY_FETCHED = 35;
	public static final int ELIGIBLE_ACTIVE_INVENTORY_NOT_FOUND = 39;

	public static final int COMPLETED = 90;
	public static final int FAILED =96;
	public static final int UNASSIGN_BATCH_COMPLETED = 90;

	public static final int ELIGIBLE_UPCS_FOUND = 20;
	public static final int ELIGIBLE_LOCATIONS_FOUND = 30;
	public static final int ASSIGNMENT_IN_PROGRESS = 45;
}