package com.pawar.sop.assignment.model;

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
	private int item_id;

	@JsonProperty("item_brcd")
	@Column(name = "item_brcd")
	private String itemBrcd;

	@JsonProperty("asnBrcd")
	@Column(name = "asn_brcd")
	private String asnBrcd;

	@JsonProperty("asn_lpn_info")
	@Column(name = "asn_lpn_info")
	private String asnLpnInfo;

	@JsonProperty("locn_info")
	@Column(name = "locn_info")
	private String locnInfo;

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
		this.item_id = sopEligibleItemsDto.getItem_id();
		this.itemBrcd = sopEligibleItemsDto.getItem_brcd();
		this.asnBrcd = sopEligibleItemsDto.getAsnBrcd();
		this.asnLpnInfo = sopEligibleItemsDto.getAsnLpnInfo();
		this.locnInfo = sopEligibleItemsDto.getLocnInfo();
		this.asnInTranQty = sopEligibleItemsDto.getAsnInTranQty();
		this.asnRcvQty = sopEligibleItemsDto.getAsnRcvQty();
		this.resvQty = sopEligibleItemsDto.getResvQty();
		this.category = sopEligibleItemsDto.getCategory();
		this.length = sopEligibleItemsDto.getLength();
		this.width = sopEligibleItemsDto.getWidth();
		this.height = sopEligibleItemsDto.getHeight();
		this.createdDttm = sopEligibleItemsDto.getCreatedDttm();
		this.lastUpdatedDttm = sopEligibleItemsDto.getLastUpdatedDttm();
		this.createdSource = sopEligibleItemsDto.getCreatedSource();
		this.lastUpdatedSource = sopEligibleItemsDto.getLastUpdatedSource();
	}

	public SopEligibleItems(Integer sopEligibleItemsId, int item_id, String itemBrcd, String asnBrcd, String asnLpnInfo,
			String locnInfo, int asnInTranQty, int asnRcvQty, int resvQty, String category, float length, float width,
			float height, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource,
			String lastUpdatedSource) {
		super();
		this.sopEligibleItemsId = sopEligibleItemsId;
		this.item_id = item_id;
		this.itemBrcd = itemBrcd;
		this.asnBrcd = asnBrcd;
		this.asnLpnInfo = asnLpnInfo;
		this.locnInfo = locnInfo;
		this.asnInTranQty = asnInTranQty;
		this.asnRcvQty = asnRcvQty;
		this.resvQty = resvQty;
		this.category = category;
		this.length = length;
		this.width = width;
		this.height = height;
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

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
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

	public String getLocnInfo() {
		return locnInfo;
	}

	public void setLocnInfo(String locnInfo) {
		this.locnInfo = locnInfo;
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
		return "SopEligibleItems [sopEligibleItemsId=" + sopEligibleItemsId + ", item_id=" + item_id + ", itemBrcd="
				+ itemBrcd + ", asnBrcd=" + asnBrcd + ", asnLpnInfo=" + asnLpnInfo + ", locnInfo=" + locnInfo
				+ ", asnInTranQty=" + asnInTranQty + ", asnRcvQty=" + asnRcvQty + ", resvQty=" + resvQty + ", category="
				+ category + ", length=" + length + ", width=" + width + ", height=" + height + ", createdDttm="
				+ createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource
				+ ", lastUpdatedSource=" + lastUpdatedSource + "]";
	}

}
