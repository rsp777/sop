package com.pawar.sop.unassignment.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.entity.SopUnassignDataDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sop_unassign_data")
public class SopUnassignData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sop_unassign_data_id")
	private Integer sopUnassignDataId;

	@JsonProperty("item_id")
	@Column(name = "item_id")
	private int itemId;

	@JsonProperty("item_brcd")
	@Column(name = "item_brcd")
	private String itemBrcd;

	@JsonProperty("location_id")
	@Column(name = "location_id")
	private int locationId;

	@JsonProperty("location_brcd")
	@Column(name = "location_brcd")
	private String locationBrcd;

	@JsonProperty("on_hand_qty")
	@Column(name = "on_hand_qty")
	private String onHandQty;

	@JsonProperty("asn_intran_qty")
	@Column(name = "asn_intran_qty")
	private int asnInTranQty;

	@JsonProperty("resv_qty")
	@Column(name = "resv_qty")
	private int resvQty;

	@JsonProperty("category")
	@Column(name = "category")
	private String category;

	@JsonProperty("log_message")
	@Column(name = "log_message")
	private String logMessage;

	@JsonProperty("condition_log_message")
	@Column(name = "condition_log_message")
	private String conditionLogMessage;

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

	public SopUnassignData() {
	}

	public SopUnassignData(Integer sopUnassignDataId, int itemId, String itemBrcd, int locationId, String locationBrcd,
			String onHandQty, int asnInTranQty, int resvQty, String category, String logMessage,
			String conditionLogMessage, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource,
			String lastUpdatedSource) {
		this.sopUnassignDataId = sopUnassignDataId;
		this.itemId = itemId;
		this.itemBrcd = itemBrcd;
		this.locationId = locationId;
		this.locationBrcd = locationBrcd;
		this.onHandQty = onHandQty;
		this.asnInTranQty = asnInTranQty;
		this.resvQty = resvQty;
		this.category = category;
		this.logMessage = logMessage;
		this.conditionLogMessage = conditionLogMessage;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public SopUnassignData(SopUnassignDataDto sopUnassignDataDto) {
		this.sopUnassignDataId = sopUnassignDataDto.getSopUnassignDataId();
		this.itemId = sopUnassignDataDto.getItemId();
		this.itemBrcd = sopUnassignDataDto.getItemBrcd();
		this.locationId = sopUnassignDataDto.getLocationId();
		this.locationBrcd = sopUnassignDataDto.getLocationBrcd();
		this.onHandQty = sopUnassignDataDto.getOnHandQty();
		this.asnInTranQty = sopUnassignDataDto.getAsnInTranQty();
		this.resvQty = sopUnassignDataDto.getResvQty();
		this.category = sopUnassignDataDto.getCategory();
		this.logMessage = sopUnassignDataDto.getLogMessage();
		this.conditionLogMessage = sopUnassignDataDto.getConditionLogMessage();
		this.createdDttm = sopUnassignDataDto.getCreatedDttm();
		this.lastUpdatedDttm = sopUnassignDataDto.getLastUpdatedDttm();
		this.createdSource = sopUnassignDataDto.getCreatedSource();
		this.lastUpdatedSource = sopUnassignDataDto.getLastUpdatedSource();
	}

	public SopUnassignDataDto convertEntityToDto(SopUnassignData sopUnassignData) {
		SopUnassignDataDto sopUnassignDataDto = new SopUnassignDataDto();
		sopUnassignDataDto.setSopUnassignDataId(sopUnassignData.getSopUnassignDataId());
		sopUnassignDataDto.setItemId(sopUnassignData.getItemId());
		sopUnassignDataDto.setItemBrcd(sopUnassignData.getItemBrcd());
		sopUnassignDataDto.setLocationId(sopUnassignData.getLocationId());
		sopUnassignDataDto.setLocationBrcd(sopUnassignData.getLocationBrcd());
		sopUnassignDataDto.setOnHandQty(sopUnassignData.getOnHandQty());
		sopUnassignDataDto.setAsnInTranQty(sopUnassignData.getAsnInTranQty());
		sopUnassignDataDto.setResvQty(sopUnassignData.getResvQty());
		sopUnassignDataDto.setCategory(sopUnassignData.getCategory());
		sopUnassignDataDto.setLogMessage(sopUnassignData.getLogMessage());
		sopUnassignDataDto.setConditionLogMessage(sopUnassignData.getConditionLogMessage());
		sopUnassignDataDto.setCreatedDttm(sopUnassignData.getCreatedDttm());
		sopUnassignDataDto.setLastUpdatedDttm(sopUnassignData.getLastUpdatedDttm());
		sopUnassignDataDto.setCreatedSource(sopUnassignData.getCreatedSource());
		sopUnassignDataDto.setLastUpdatedSource(sopUnassignData.getLastUpdatedSource());
		return sopUnassignDataDto;
	}

	public Integer getSopUnassignDataId() {
		return sopUnassignDataId;
	}

	public void setSopUnassignDataId(Integer sopUnassignDataId) {
		this.sopUnassignDataId = sopUnassignDataId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemBrcd() {
		return itemBrcd;
	}

	public void setItemBrcd(String itemBrcd) {
		this.itemBrcd = itemBrcd;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getLocationBrcd() {
		return locationBrcd;
	}

	public void setLocationBrcd(String locationBrcd) {
		this.locationBrcd = locationBrcd;
	}

	public String getOnHandQty() {
		return onHandQty;
	}

	public void setOnHandQty(String onHandQty) {
		this.onHandQty = onHandQty;
	}

	public int getAsnInTranQty() {
		return asnInTranQty;
	}

	public void setAsnInTranQty(int asnInTranQty) {
		this.asnInTranQty = asnInTranQty;
	}

	public int getResvQty() {
		return resvQty;
	}

	public void setResvQty(int resvQty) {
		this.resvQty = resvQty;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public String getConditionLogMessage() {
		return conditionLogMessage;
	}

	public void setConditionLogMessage(String conditionLogMessage) {
		this.conditionLogMessage = conditionLogMessage;
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
		return "SopUnassignData [sopUnassignDataId=" + sopUnassignDataId + ", itemId=" + itemId + ", itemBrcd="
				+ itemBrcd + ", locationId=" + locationId + ", locationBrcd=" + locationBrcd + ", onHandQty="
				+ onHandQty + ", asnInTranQty=" + asnInTranQty + ", resvQty=" + resvQty + ", category=" + category
				+ ", logMessage=" + logMessage + ", conditionLogMessage=" + conditionLogMessage + ", createdDttm="
				+ createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
				+ ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}
}