CREATE DEFINER=`ravi`@`%` PROCEDURE `AddEligibleLocations`(IN p_locationRangeId INT)
BEGIN
    -- Step 1: Declare variables
    DECLARE v_sop_location_range_id INT;
    DECLARE v_category VARCHAR(255);
    DECLARE v_fromLocation VARCHAR(255);
    DECLARE v_toLocation VARCHAR(255);
    DECLARE v_locn_id INT;
    DECLARE v_locn_brcd VARCHAR(255);
    DECLARE v_grp VARCHAR(255);
    DECLARE v_length FLOAT;
    DECLARE v_width FLOAT;
    DECLARE v_height FLOAT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_action_type_id INT;
    DECLARE v_on_hand_qty DOUBLE;
    DECLARE v_item_id INT;

    DECLARE locn_cursor CURSOR FOR 
    SELECT locn_id, locn_brcd, grp 
    FROM inventory.location 
    WHERE locn_brcd BETWEEN v_fromLocation AND v_toLocation;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- Step 2: Drop temporary table if it exists
    DROP TEMPORARY TABLE IF EXISTS TempEligibleLocations;

    -- Step 3: Create temporary table to hold eligible locations
    CREATE TEMPORARY TABLE IF NOT EXISTS TempEligibleLocations (
        locn_id INT,
        locn_brcd VARCHAR(255),
        grp VARCHAR(255),
        assigned_nbr_of_upc INT DEFAULT 0,
        max_nbr_of_sku INT DEFAULT 1,
        category VARCHAR(255),
        length FLOAT,
        width FLOAT,
        height FLOAT,
        created_source VARCHAR(255),
        created_dttm DATETIME,
        last_updated_source VARCHAR(255),
        last_updated_dttm DATETIME,
        sop_location_range_id INT
    );

    -- Step 4: Get location range details
SELECT 
    slr.category,
    slr.from_location,
    slr.to_location,
    slr.sop_location_range_id,
    slr.sop_action_type_id
INTO v_category , v_fromLocation , v_toLocation , v_sop_location_range_id , v_action_type_id FROM
    sop_location_range slr
WHERE
    sop_location_range_id = p_locationRangeId; 

    -- Log the action type
    INSERT INTO procedure_log (log_message) VALUES (CONCAT('Processing location range ID: ', p_locationRangeId, ' with action type ID: ', v_action_type_id));

    -- Step 5: Iterate through the cursor and handle inventory checks
    OPEN locn_cursor;

    read_loop: LOOP
        FETCH locn_cursor INTO v_locn_id, v_locn_brcd, v_grp;

        IF done THEN 
            LEAVE read_loop; 
        END IF;

        -- Log the current location being processed
        INSERT INTO procedure_log (log_message) VALUES (CONCAT('Processing location: ', v_locn_brcd));

        -- Get location dimensions
SELECT 
    length, width, height
INTO v_length , v_width , v_height FROM
    inventory.location
WHERE
    locn_brcd = v_locn_brcd;

        -- Log dimensions
        INSERT INTO procedure_log (log_message) VALUES (CONCAT('Dimensions for location ', v_locn_brcd, ': Length = ', v_length, ', Width = ', v_width, ', Height = ', v_height));

        -- Check if the location is eligible for assignment
        IF EXISTS (SELECT 
    1
FROM
    sop_eligible_locations sel
        INNER JOIN
    sop_location_range slr ON sel.sop_location_range_id = slr.sop_location_range_id
WHERE
    sel.sop_location_range_id = p_locationRangeId
        AND sel.locn_id = v_locn_id
        AND slr.sop_action_type_id = v_action_type_id) 
           AND NOT EXISTS (SELECT 1 
                           FROM inventory.inventory 
                           WHERE locn_id = v_locn_id 
                             AND locn_class = 'A') THEN
            -- Insert the location into the temporary table
            INSERT INTO TempEligibleLocations (locn_id, locn_brcd, grp, assigned_nbr_of_upc, max_nbr_of_sku, category, sop_location_range_id, length, width, height,created_source, 
        created_dttm,
        last_updated_source,
        last_updated_dttm) 
            VALUES (v_locn_id, v_locn_brcd, v_grp, 0, 1, v_category, v_sop_location_range_id, v_length, v_width, v_height,"add-eligible-locations",sysdate(), "add-eligible-locations",sysdate());
            INSERT INTO procedure_log (log_message) VALUES (CONCAT('Marked location ', v_locn_brcd, ' as eligible for assignment.'));
        END IF;

        -- Check if the location is eligible for unassignment
SELECT 
    SUM(on_hand_qty), item_id
INTO v_on_hand_qty , v_item_id FROM
    inventory.inventory
WHERE
    locn_id = v_locn_id AND locn_class = 'A'
GROUP BY item_id;
		-- Step 2: Check if v_on_hand_qty is NULL (no records found)
		IF v_on_hand_qty IS NULL THEN
			-- Handle the case where there are no records for the location
			INSERT INTO procedure_log (log_message) VALUES (CONCAT('No records found for location ', v_locn_brcd, '. Skipping unassignment checks.'));
		ELSE
        IF v_on_hand_qty = 0 THEN
            -- Check for ASN in status 20 and LPN with facility status 0
            IF EXISTS (SELECT 1 
                       FROM inventory.asn a 
                       INNER JOIN inventory.lpn l ON a.asn_id = l.asn_id 
                       WHERE l.item_id = v_item_id 
                         AND a.asn_status = 20 
                         AND l.lpn_facility_status = 0) THEN
                INSERT INTO procedure_log (log_message) VALUES (CONCAT('Found ASN in status 20 and LPN in facility status 0 for item ', v_item_id));
            END IF;

            -- Check for LPN with facility status 30
            IF EXISTS (SELECT 1 
                       FROM inventory.lpn l 
                       WHERE l.item_id = v_item_id 
                         AND l.lpn_facility_status = 30) THEN
                INSERT INTO procedure_log (log_message) VALUES (CONCAT('Found LPN in facility status 30 for item ', v_item_id));
            END IF;

            -- Check for LPN with facility status 50
            IF EXISTS (SELECT 1 
                       FROM inventory.lpn l 
                       WHERE l.item_id = v_item_id 
                         AND l.lpn_facility_status = 50) THEN
                INSERT INTO procedure_log (log_message) VALUES (CONCAT('Found LPN in facility status 50 for item ', v_item_id));
            END IF;

            -- If none of the conditions are met, insert the location into the temporary table
            IF NOT EXISTS (SELECT 1 
                           FROM inventory.asn a 
                           INNER JOIN inventory.lpn l ON a.asn_id = l.asn_id 
                           WHERE l.item_id = v_item_id 
                             AND (a.asn_status = 20 AND l.lpn_facility_status = 0 
                                  OR l.lpn_facility_status = 30 
                                  OR l.lpn_facility_status = 50)) THEN
                INSERT INTO TempEligibleLocations (locn_id, locn_brcd, grp, assigned_nbr_of_upc, max_nbr_of_sku, category, sop_location_range_id, length, width, height,created_source, 
        created_dttm,
        last_updated_source,
        last_updated_dttm) 
            VALUES (v_locn_id, v_locn_brcd, v_grp, 0, 1, v_category, v_sop_location_range_id, v_length, v_width, v_height,"add-eligible-locations",sysdate(), "add-eligible-locations",sysdate());
                INSERT INTO procedure_log (log_message) VALUES (CONCAT('Marked location ', v_locn_brcd, ' as eligible for unassignment.'));
            ELSE
                INSERT INTO procedure_log (log_message) VALUES (CONCAT('Location ', v_locn_brcd, ' is not eligible for unassignment. Skipping insertion.'));
            END IF;
        ELSE
            -- If on_hand_qty is not 0, mark as ineligible for unassignment
            INSERT INTO procedure_log (log_message) VALUES (CONCAT('Location ', v_locn_brcd, ' is not eligible for unassignment due to on-hand quantity: ', v_on_hand_qty));
        END IF;
    END LOOP;

    CLOSE locn_cursor;

    -- Step 6: Log entry
    INSERT INTO procedure_log (log_message) VALUES ('Completed processing of eligible locations.');

    -- Step 7: Insert eligible locations into the main table
    INSERT INTO sop_eligible_locations (locn_id, locn_brcd, grp, assigned_nbr_of_upc, max_nbr_of_sku, category, sop_location_range_id, length, width, height)
    SELECT locn_id, locn_brcd, grp, assigned_nbr_of_upc, max_nbr_of_sku, category, sop_location_range_id, length, width, height
    FROM TempEligibleLocations;

    -- Step 8: Final log entry
    INSERT INTO procedure_log (log_message) VALUES ('Inserted eligible locations into sop_eligible_locations table');
END