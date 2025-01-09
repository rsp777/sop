package com.pawar.sop.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.sop.config.model.SopActionType;
import com.pawar.sop.config.model.SopLocationRange;


@Repository
public interface SopLocationRangeRepository extends JpaRepository<SopLocationRange, Integer> {
}
