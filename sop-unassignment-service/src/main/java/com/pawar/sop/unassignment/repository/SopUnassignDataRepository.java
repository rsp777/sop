package com.pawar.sop.unassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.sop.unassignment.model.SopUnassignData;

@Repository
public interface SopUnassignDataRepository extends JpaRepository<SopUnassignData, Integer>{

}
