package com.pawar.sop.realtime.unassign.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.SopEligibleItemsDto;
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
@Table(name = "sop_eligible_items")
public class SopEligibleItems {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sop_eligible_items_id")
	private Integer sopEligibleItemsId;

	@JsonProperty("item_id")
	@Column(name = "item_id")
	private int itemId;

	@JsonProperty("item_brcd")
	@Column(name = "item_brcd")
	private String itemBrcd;

	@JsonProperty("asnBrcd")
	@Column(name = "asn_brcd")
	private String asnBrcd;

	@JsonProperty("asn_lpn_info")
	@Column(name = "asn_lpn_info")
	private String asnLpnInfo;

	@JsonProperty("asn_intran_qty")
	@Column(name = "asn_intran_qty")
	private int asnInTranQty;

	@JsonProperty("asn_rcv_qty")
	@Column(name = "asn_rcv_qty")
	private int asnRcvQty;

	@JsonProperty("resv_qty")
	@Column(name = "resv_qty")
	private int resvQty;

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

	@JsonProperty("isAssigned")
	@Column(name = "is_assigned")
	private String isAssigned;

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

	public SopEligibleItems() {
	}

	public SopEligibleItems(SopEligibleItemsDto sopEligibleItemsDto) {
		super();
		this.sopEligibleItemsId = sopEligibleItemsDto.getSopEligibleItemsId();
		this.itemId = sopEligibleItemsDto.getItem_id();
		this.itemBrcd = sopEligibleItemsDto.getItem_brcd();
		this.asnBrcd = sopEligibleItemsDto.getAsnBrcd();
		this.asnLpnInfo = sopEligibleItemsDto.getAsnLpnInfo();
		this.asnInTranQty = sopEligibleItemsDto.getAsnInTranQty();
		this.asnRcvQty = sopEligibleItemsDto.getAsnRcvQty();
		this.resvQty = sopEligibleItemsDto.getResvQty();
		this.category = sopEligibleItemsDto.getCategory();
		this.length = sopEligibleItemsDto.getLength();
		this.width = sopEligibleItemsDto.getWidth();
		this.height = sopEligibleItemsDto.getHeight();
		this.isAssigned = sopEligibleItemsDto.getIsAssigned();
		this.createdDttm = sopEligibleItemsDto.getCreatedDttm();
		this.lastUpdatedDttm = sopEligibleItemsDto.getLastUpdatedDttm();
		this.createdSource = sopEligibleItemsDto.getCreatedSource();
		this.lastUpdatedSource = sopEligibleItemsDto.getLastUpdatedSource();
	}

	public SopEligibleItems(Integer sopEligibleItemsId, int itemId, String itemBrcd, String asnBrcd, String asnLpnInfo,
			int asnInTranQty, int asnRcvQty, int resvQty, String category, float length, float width,
			float height, String isAssigned, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm,
			String createdSource, String lastUpdatedSource) {
		this.sopEligibleItemsId = sopEligibleItemsId;
		this.itemId = itemId;
		this.itemBrcd = itemBrcd;
		this.asnBrcd = asnBrcd;
		this.asnLpnInfo = asnLpnInfo;
		this.asnInTranQty = asnInTranQty;
		this.asnRcvQty = asnRcvQty;
		this.resvQty = resvQty;
		this.category = category;
		this.length = length;
		this.width = width;
		this.height = height;
		this.isAssigned = isAssigned;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	
	
	public Integer getSopEligibleItemsId() {
		return sopEligibleItemsId;
	}

	public void setSopEligibleItemsId(Integer sopEligibleItemsId) {
		this.sopEligibleItemsId = sopEligibleItemsId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int item_id) {
		this.itemId = item_id;
	}

	public String getItemBrcd() {
		return itemBrcd;
	}

	public void setItemBrcd(String itemBrcd) {
		this.itemBrcd = itemBrcd;
	}

	public String getAsnBrcd() {
		return asnBrcd;
	}

	public void setAsnBrcd(String asnBrcd) {
		this.asnBrcd = asnBrcd;
	}

	public String getAsnLpnInfo() {
		return asnLpnInfo;
	}

	public void setAsnLpnInfo(String asnLpnInfo) {
		this.asnLpnInfo = asnLpnInfo;
	}

	public int getAsnInTranQty() {
		return asnInTranQty;
	}

	public void setAsnInTranQty(int asnInTranQty) {
		this.asnInTranQty = asnInTranQty;
	}

	public int getAsnRcvQty() {
		return asnRcvQty;
	}

	public void setAsnRcvQty(int asnRcvQty) {
		this.asnRcvQty = asnRcvQty;
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

	public String getIsAssigned() {
		return isAssigned;
	}

	public void setIsAssigned(String isAssigned) {
		this.isAssigned = isAssigned;
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
	
//	public SopEligibleItemsDto convertEntityToDto(SopEligibleItems sopEligibleItem) {
//		
//	}

	

	public SopEligibleItemsDto convertEntityToDto(SopEligibleItems sopEligibleItem) {
		SopEligibleItemsDto sopEligibleItemsDto = new SopEligibleItemsDto();
		sopEligibleItemsDto.setSopEligibleItemsId(sopEligibleItem.getSopEligibleItemsId());
		sopEligibleItemsDto.setItem_id(sopEligibleItem.getItemId());
		sopEligibleItemsDto.setItem_brcd(sopEligibleItem.getItemBrcd());
		sopEligibleItemsDto.setLength(sopEligibleItem.getLength());
		sopEligibleItemsDto.setWidth(sopEligibleItem.getWidth());
		sopEligibleItemsDto.setHeight(sopEligibleItem.getHeight());
		sopEligibleItemsDto.setAsnBrcd(sopEligibleItem.getAsnBrcd());
		sopEligibleItemsDto.setAsnLpnInfo(sopEligibleItem.getAsnLpnInfo());
		sopEligibleItemsDto.setAsnInTranQty(sopEligibleItem.getAsnInTranQty());
		sopEligibleItemsDto.setAsnRcvQty(sopEligibleItem.getAsnRcvQty());
		sopEligibleItemsDto.setResvQty(sopEligibleItem.getResvQty());
		sopEligibleItemsDto.setCategory(sopEligibleItem.getCategory());
		sopEligibleItemsDto.setCategory(sopEligibleItem.getCategory());
		sopEligibleItemsDto.setIsAssigned(sopEligibleItem.getIsAssigned());
		sopEligibleItemsDto.setCreatedDttm(sopEligibleItem.getCreatedDttm());
		sopEligibleItemsDto.setLastUpdatedDttm(sopEligibleItem.getLastUpdatedDttm());
		sopEligibleItemsDto.setCreatedSource(sopEligibleItem.getCreatedSource());
		sopEligibleItemsDto.setLastUpdatedSource(sopEligibleItem.getLastUpdatedSource());
		return sopEligibleItemsDto;
	}

	@Override
	public String toString() {
		return "SopEligibleItems [sopEligibleItemsId=" + sopEligibleItemsId + ", itemId=" + itemId + ", itemBrcd="
				+ itemBrcd + ", asnBrcd=" + asnBrcd + ", asnLpnInfo=" + asnLpnInfo + ", asnInTranQty=" + asnInTranQty
				+ ", asnRcvQty=" + asnRcvQty + ", resvQty=" + resvQty + ", category=" + category + ", length=" + length
				+ ", width=" + width + ", height=" + height + ", isAssigned=" + isAssigned + ", createdDttm="
				+ createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
				+ ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}


}
