package com.pawar.sop.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.sop.log.model.SopActionType;



@Repository
public interface SopActionTypeRepository extends JpaRepository<SopActionType, Integer> {

	SopActionType findByActionType(String actionType);

}
