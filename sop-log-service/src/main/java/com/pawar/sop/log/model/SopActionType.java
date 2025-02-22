package com.pawar.sop.log.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sop_action_type")
public class SopActionType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sopActionTypeId")
	private Integer sopActionTypeId;

	@JsonProperty("actionDesc")
	@Column(name = "action_desc")
	private String actionDesc;

	@JsonProperty("actionType")
	@Column(name = "action_type")
	private String actionType;

	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("createdDttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;

	@JsonInclude(value = Include.CUSTOM)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("lastUpdatedDttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "createdSource")
	private String createdSource;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "lastUpdatedSource")
	private String lastUpdatedSource;

	public SopActionType() {
	}

	public SopActionType(Integer sopActionTypeId, String actionDesc, String actionType, LocalDateTime createdDttm,
			LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {

		this.sopActionTypeId = sopActionTypeId;
		this.actionDesc = actionDesc;
		this.actionType = actionType;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public SopActionType(SopActionTypeDto sopActionTypeDto) {
		this.sopActionTypeId = sopActionTypeDto.getSopActionTypeId();
		this.actionDesc = sopActionTypeDto.getActionDesc();
		this.actionType = sopActionTypeDto.getActionType();
		this.createdDttm = sopActionTypeDto.getCreatedDttm();
		this.lastUpdatedDttm = sopActionTypeDto.getLastUpdatedDttm();
		this.createdSource = sopActionTypeDto.getCreatedSource();
		this.lastUpdatedSource = sopActionTypeDto.getLastUpdatedSource();
	}

	public Integer getSopActionTypeId() {
		return sopActionTypeId;
	}

	public void setSopActionTypeId(Integer sopActionTypeId) {
		this.sopActionTypeId = sopActionTypeId;
	}

	public String getActionDesc() {
		return actionDesc;
	}

	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public LocalDateTime getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(LocalDateTime createdDttm) {
		this.createdDttm = createdDttm;
	}

	public LocalDateTime getLastUpdatedDttm() {
		return lastUpdatedDttm;
	}

	public void setLastUpdatedDttm(LocalDateTime lastUpdatedDttm) {
		this.lastUpdatedDttm = lastUpdatedDttm;
	}

	public String getCreatedSource() {
		return createdSource;
	}

	public void setCreatedSource(String createdSource) {
		this.createdSource = createdSource;
	}

	public String getLastUpdatedSource() {
		return lastUpdatedSource;
	}

	public void setLastUpdatedSource(String lastUpdatedSource) {
		this.lastUpdatedSource = lastUpdatedSource;
	}

	

	@Override
	public String toString() {
		return "SopActionType [sopActionTypeId=" + sopActionTypeId + ", actionDesc=" + actionDesc + ", actionType="
				+ actionType + ", createdDttm=" + createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm
				+ ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}
}
