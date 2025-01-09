package com.pawar.sop.config.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.SopEligibleLocationsDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sop_eligible_locations")
public class SopEligibleLocations {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sop_eligible_locations_id")
	private Integer sopEligibleLocationsId;

	@JsonProperty("locn_id")
	@Column(name = "locn_id")
	private int locn_id;

	@JsonProperty("locn_brcd")
	@Column(name = "locn_brcd")
	private String locn_brcd;

	@JsonProperty("grp")
	@Column(name = "grp")
	private String grp;

	@JsonProperty("assigned_nbr_of_upc")
	@Column(name = "assigned_nbr_of_upc")
	private int assignedNbrOfUpc;

	@JsonProperty("max_nbr_of_sku")
	@Column(name = "max_nbr_of_sku")
	private int maxNbrOfSku;

	@ManyToOne
	@JoinColumn(name = "sop_location_range_id")
	private SopLocationRange sopLocationRange;

	@JsonProperty("category")
	@Column(name = "category")
	private String category;

	@JsonProperty("length")
	@Column(name = "length")
	private float length;

	@JsonProperty("width")
	@Column(name = "width")
	private float width;

	@JsonProperty("height")
	@Column(name = "height")
	private float height;

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

	public SopEligibleLocations() {
	}

	public SopEligibleLocations(Integer sopEligibleLocationsId, int locn_id, String locn_brcd, String grp,
			int assignedNbrOfUpc, int maxNbrOfSku, SopLocationRange sopLocationRange, String category, float length,
			float width, float height, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource,
			String lastUpdatedSource) {
		this.sopEligibleLocationsId = sopEligibleLocationsId;
		this.locn_id = locn_id;
		this.locn_brcd = locn_brcd;
		this.grp = grp;
		this.assignedNbrOfUpc = assignedNbrOfUpc;
		this.maxNbrOfSku = maxNbrOfSku;
		this.sopLocationRange = sopLocationRange;
		this.category = category;
		this.length = length;
		this.width = width;
		this.height = height;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public SopEligibleLocations(SopEligibleLocationsDto sopEligibleLocationsDto) {
		this.sopEligibleLocationsId = sopEligibleLocationsDto.getSopEligibleLocationsId();
		this.locn_id = sopEligibleLocationsDto.getLocn_id();
		this.locn_brcd = sopEligibleLocationsDto.getLocn_brcd();
		this.grp = sopEligibleLocationsDto.getGrp();
		this.assignedNbrOfUpc = sopEligibleLocationsDto.getAssignedNbrOfUpc();
		this.maxNbrOfSku = sopEligibleLocationsDto.getMaxNbrOfSku();
		this.sopLocationRange = convertSopLocationRangeDtoToEntity(sopEligibleLocationsDto.getSopLocationRange());
		this.category = sopEligibleLocationsDto.getCategory();
		this.length = sopEligibleLocationsDto.getLength();
		this.width = sopEligibleLocationsDto.getWidth();
		this.height = sopEligibleLocationsDto.getHeight();
		this.createdDttm = sopEligibleLocationsDto.getCreatedDttm();
		this.lastUpdatedDttm = sopEligibleLocationsDto.getLastUpdatedDttm();
		this.createdSource = sopEligibleLocationsDto.getCreatedSource();
		this.lastUpdatedSource = sopEligibleLocationsDto.getLastUpdatedSource();
	}
	
	public SopLocationRange convertSopLocationRangeDtoToEntity (SopLocationRangeDto sopLocationRangeDto ) {
		SopLocationRange SopLocationRange = new SopLocationRange(sopLocationRangeDto);
		return SopLocationRange;
	}
	
	public Integer getSopEligibleLocationsId() {
		return sopEligibleLocationsId;
	}

	public void setSopEligibleLocationsId(Integer sopEligibleLocationsId) {
		this.sopEligibleLocationsId = sopEligibleLocationsId;
	}

	public int getLocn_id() {
		return locn_id;
	}

	public void setLocn_id(int locn_id) {
		this.locn_id = locn_id;
	}

	public String getLocn_brcd() {
		return locn_brcd;
	}

	public void setLocn_brcd(String locn_brcd) {
		this.locn_brcd = locn_brcd;
	}

	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}

	public int getAssignedNbrOfUpc() {
		return assignedNbrOfUpc;
	}

	public void setAssignedNbrOfUpc(int assignedNbrOfUpc) {
		this.assignedNbrOfUpc = assignedNbrOfUpc;
	}

	public int getMaxNbrOfSku() {
		return maxNbrOfSku;
	}

	public void setMaxNbrOfSku(int maxNbrOfSku) {
		this.maxNbrOfSku = maxNbrOfSku;
	}

	public SopLocationRange getSopLocationRange() {
		return sopLocationRange;
	}

	public void setSopLocationRange(SopLocationRange sopLocationRange) {
		this.sopLocationRange = sopLocationRange;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
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
		return "SopEligibleLocations [sopEligibleLocationsId=" + sopEligibleLocationsId + ", locn_id=" + locn_id
				+ ", locn_brcd=" + locn_brcd + ", grp=" + grp + ", assignedNbrOfUpc=" + assignedNbrOfUpc
				+ ", maxNbrOfSku=" + maxNbrOfSku + ", sopLocationRange=" + sopLocationRange + ", category=" + category
				+ ", length=" + length + ", width=" + width + ", height=" + height + ", createdDttm=" + createdDttm
				+ ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource="
				+ lastUpdatedSource + "]";
	}

}
