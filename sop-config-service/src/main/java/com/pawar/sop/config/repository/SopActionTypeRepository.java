package com.pawar.sop.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.sop.config.model.SopActionType;


@Repository
public interface SopActionTypeRepository extends JpaRepository<SopActionType, Integer> {

	SopActionType findByActionType(String actionType);

}
