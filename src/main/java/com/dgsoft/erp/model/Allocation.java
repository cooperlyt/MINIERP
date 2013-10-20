package com.dgsoft.erp.model;
// Generated Oct 17, 2013 5:33:51 PM by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Allocation generated by hbm2java
 */
@Entity
@Table(name = "ALLOCATION", catalog = "MINI_ERP")
public class Allocation implements java.io.Serializable {

	private String id;
	private Store storeByTargetStore;
	private StockChange stockChangeByStoreIn;
	private Store storeByApplyStore;
	private StockChange stockChangeByStoreOut;
	private String applyEmp;
	private String allocationEmp;
	private String reason;
	private String memo;
	private String state;
	private Date createDate;
	private Date completeDate;
	private Set<AllocationRes> allocationReses = new HashSet<AllocationRes>(0);

	public Allocation() {
	}

	public Allocation(String id, Store storeByTargetStore,
			Store storeByApplyStore, String applyEmp, String reason,
			String state, Date createDate, Date completeDate) {
		this.id = id;
		this.storeByTargetStore = storeByTargetStore;
		this.storeByApplyStore = storeByApplyStore;
		this.applyEmp = applyEmp;
		this.reason = reason;
		this.state = state;
		this.createDate = createDate;
		this.completeDate = completeDate;
	}


	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	@NotNull
	@Size(max = 32)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TARGET_STORE", nullable = false)
	@NotNull
	public Store getStoreByTargetStore() {
		return this.storeByTargetStore;
	}

	public void setStoreByTargetStore(Store storeByTargetStore) {
		this.storeByTargetStore = storeByTargetStore;
	}

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_IN", nullable = true)
	public StockChange getStockChangeByStoreIn() {
		return this.stockChangeByStoreIn;
	}

	public void setStockChangeByStoreIn(StockChange stockChangeByStoreIn) {
		this.stockChangeByStoreIn = stockChangeByStoreIn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APPLY_STORE", nullable = false)
	@NotNull
	public Store getStoreByApplyStore() {
		return this.storeByApplyStore;
	}

	public void setStoreByApplyStore(Store storeByApplyStore) {
		this.storeByApplyStore = storeByApplyStore;
	}

	@OneToOne(optional = true,fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_OUT", nullable = true)
	public StockChange getStockChangeByStoreOut() {
		return this.stockChangeByStoreOut;
	}

	public void setStockChangeByStoreOut(StockChange stockChangeByStoreOut) {
		this.stockChangeByStoreOut = stockChangeByStoreOut;
	}

	@Column(name = "APPLY_EMP", nullable = false, length = 32)
	@NotNull
	@Size(max = 32)
	public String getApplyEmp() {
		return this.applyEmp;
	}

	public void setApplyEmp(String applyEmp) {
		this.applyEmp = applyEmp;
	}

	@Column(name = "ALLOCATION_EMP", length = 32)
	@Size(max = 32)
	public String getAllocationEmp() {
		return this.allocationEmp;
	}

	public void setAllocationEmp(String allocationEmp) {
		this.allocationEmp = allocationEmp;
	}

	@Column(name = "REASON", nullable = false, length = 32)
	@NotNull
	@Size(max = 32)
	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "MEMO", length = 200)
	@Size(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "STATE", nullable = false, length = 20)
	@NotNull
	@Size(max = 20)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false, length = 19)
	@NotNull
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "COMPLETE_DATE", nullable = false, length = 19)
	@NotNull
	public Date getCompleteDate() {
		return this.completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "allocation")
	public Set<AllocationRes> getAllocationReses() {
		return this.allocationReses;
	}

	public void setAllocationReses(Set<AllocationRes> allocationReses) {
		this.allocationReses = allocationReses;
	}

}
