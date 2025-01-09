package com.pawar.sop.config.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sop_location_range")
public class SopLocationRange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sop_location_range_id")
	private Integer sopLocationRangeId;
	
	@ManyToOne
	@JoinColumn(name = "sop_action_type_id")
	private SopActionType sopActionType;
	
	@JsonProperty("category")
	@Column(name = "category")
	private String category;
	
	@JsonProperty("from_location")
	@Column(name = "from_location")
	private String fromLocation;
	
	@JsonProperty("to_location")
	@Column(name = "to_location")
	private String toLocation;
	
	@JsonProperty("total_active_locations")
	@Column(name = "total_active_locations")
	private int totalActiveLocations;
	
	@JsonProperty("used_active_locations")
	@Column(name = "used_active_locations")
	private int usedActiveLocations;
	
	@JsonProperty("avl_active_locations")
	@Column(name = "avl_active_locations")
	private int avlActiveLocations;
	
	@JsonProperty("is_active")
	@Column(name = "is_active")
	private String isActive;
	
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
	@Column(name = "created_source")
	private String createdSource;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;
	
	public SopLocationRange() {
	}

	public SopLocationRange(SopLocationRangeDto sopLocationRangeDto) {
		this.sopLocationRangeId = sopLocationRangeDto.getId();
		this.sopActionType = convertSopActionTypeDtoToEntity(sopLocationRangeDto.getSopActionType());
		this.category = sopLocationRangeDto.getCategory();
		this.fromLocation = sopLocationRangeDto.getFromLocation();
		this.toLocation = sopLocationRangeDto.getToLocation();
		this.totalActiveLocations = sopLocationRangeDto.getTotalActiveLocations();
		this.usedActiveLocations = sopLocationRangeDto.getUsedActiveLocations();
		this.avlActiveLocations = sopLocationRangeDto.getAvlActiveLocations();
		this.isActive = sopLocationRangeDto.getIsActive();
		this.createdDttm = sopLocationRangeDto.getCreatedDttm();
		this.lastUpdatedDttm = sopLocationRangeDto.getLastUpdatedDttm();
		this.createdSource = sopLocationRangeDto.getCreatedSource();
		this.lastUpdatedSource = sopLocationRangeDto.getLastUpdatedSource();
	}
	
	public SopLocationRange(Integer sopLocationRangeId, SopActionType sopActionType, String category, String fromLocation,
			String toLocation, int totalActiveLocations, int usedActiveLocations, int avlActiveLocations,
			String isActive, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource,
			String lastUpdatedSource) {
		this.sopLocationRangeId = sopLocationRangeId;
		this.sopActionType = sopActionType;
		this.category = category;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.totalActiveLocations = totalActiveLocations;
		this.usedActiveLocations = usedActiveLocations;
		this.avlActiveLocations = avlActiveLocations;
		this.isActive = isActive;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public SopActionType convertSopActionTypeDtoToEntity(SopActionTypeDto sopActionTypeDto) {
		
		SopActionType sopActionType = new SopActionType(sopActionTypeDto);
		return sopActionType;
		
	}
	
public SopActionTypeDto convertSopActionTypeEntityToDto(SopActionType sopActionType) {
		
	SopActionTypeDto sopActionTypeDto = new SopActionTypeDto(
			sopActionType.getSopActionTypeId(),sopActionType.getActionDesc(),
			sopActionType.getActionType(),sopActionType.getCreatedDttm(),sopActionType.getLastUpdatedDttm(),
			sopActionType.getCreatedSource(),sopActionType.getLastUpdatedSource());
		return sopActionTypeDto;
		
	}
	
	

	public Integer getSopLocationRangeId() {
		return sopLocationRangeId;
	}

	public void setSopLocationRangeId(Integer sopLocationRangeId) {
		this.sopLocationRangeId = sopLocationRangeId;
	}

	public SopActionType getSopActionType() {
		return sopActionType;
	}

	public void setSopActionType(SopActionType sopActionType) {
		this.sopActionType = sopActionType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public int getTotalActiveLocations() {
		return totalActiveLocations;
	}

	public void setTotalActiveLocations(int totalActiveLocations) {
		this.totalActiveLocations = totalActiveLocations;
	}

	public int getUsedActiveLocations() {
		return usedActiveLocations;
	}

	public void setUsedActiveLocations(int usedActiveLocations) {
		this.usedActiveLocations = usedActiveLocations;
	}

	public int getAvlActiveLocations() {
		return avlActiveLocations;
	}

	public void setAvlActiveLocations(int avlActiveLocations) {
		this.avlActiveLocations = avlActiveLocations;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
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
		return "LocationRange [sopLocationRangeId=" + sopLocationRangeId + ", sopActionType=" + sopActionType + ", category=" + category
				+ ", fromLocation=" + fromLocation + ", toLocation=" + toLocation + ", totalActiveLocations="
				+ totalActiveLocations + ", usedActiveLocations=" + usedActiveLocations + ", avlActiveLocations="
				+ avlActiveLocations + ", isActive=" + isActive + ", createdDttm=" + createdDttm + ", lastUpdatedDttm="
				+ lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource
				+ "]";
	}

	public int convertSopActionTypeEntityToDto(SopActionType sopActionType2, String category2, String fromLocation2,
			String toLocation2, int totalActiveLocations2, int usedActiveLocations2, int avlActiveLocations2,
			String isActive2, LocalDateTime createdDttm2, LocalDateTime lastUpdatedDttm2, String createdSource2,
			String lastUpdatedSource2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
