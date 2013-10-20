package com.dgsoft.erp.model;
// Generated Oct 17, 2013 5:33:51 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Assembly generated by hbm2java
 */
@Entity
@Table(name = "ASSEMBLY", catalog = "MINI_ERP")
public class Assembly implements java.io.Serializable {

	private String id;
	private StockChange stockChangeByStoreIn;
	private StockChange stockChangeByStoreOut;
	private String reason;
	private String memo;
	private String assemblyEmp;
    private BigDecimal loseCount;

	public Assembly() {
	}

	public Assembly(String id, StockChange stockChangeByStoreIn,
			StockChange stockChangeByStoreOut, String reason) {
		this.id = id;
		this.stockChangeByStoreIn = stockChangeByStoreIn;
		this.stockChangeByStoreOut = stockChangeByStoreOut;
		this.reason = reason;
	}
	public Assembly(String id, StockChange stockChangeByStoreIn,
			StockChange stockChangeByStoreOut, String reason, String memo,
			String assemblyEmp) {
		this.id = id;
		this.stockChangeByStoreIn = stockChangeByStoreIn;
		this.stockChangeByStoreOut = stockChangeByStoreOut;
		this.reason = reason;
		this.memo = memo;
		this.assemblyEmp = assemblyEmp;
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
	@JoinColumn(name = "STORE_IN", nullable = false)
	@NotNull
	public StockChange getStockChangeByStoreIn() {
		return this.stockChangeByStoreIn;
	}

	public void setStockChangeByStoreIn(StockChange stockChangeByStoreIn) {
		this.stockChangeByStoreIn = stockChangeByStoreIn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORE_OUT", nullable = false)
	@NotNull
	public StockChange getStockChangeByStoreOut() {
		return this.stockChangeByStoreOut;
	}

	public void setStockChangeByStoreOut(StockChange stockChangeByStoreOut) {
		this.stockChangeByStoreOut = stockChangeByStoreOut;
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

	@Column(name = "ASSEMBLY_EMP", length = 32)
	@Size(max = 32)
	public String getAssemblyEmp() {
		return this.assemblyEmp;
	}

	public void setAssemblyEmp(String assemblyEmp) {
		this.assemblyEmp = assemblyEmp;
	}

    @Column(name = "LOSE_COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(BigDecimal loseCount) {
        this.loseCount = loseCount;
    }
}
