package com.dgsoft.erp.model;
// Generated Oct 17, 2013 5:33:51 PM by Hibernate Tools 4.0.0

import com.dgsoft.common.OrderModel;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ResUnit generated by hbm2java
 */
@Entity
@Table(name = "RES_UNIT", catalog = "MINI_ERP")
public class ResUnit implements java.io.Serializable,OrderModel {

	private String id;
	private UnitGroup unitGroup;
	private String name;
	private BigDecimal conversionRate;
	private int priority;
	private Set<Res> resesForOutDefault = new HashSet<Res>(0);
	private Set<Res> resesForMasterUnit = new HashSet<Res>(0);
	private Set<Res> resesForInDefault = new HashSet<Res>(0);
    private Set<NoConvertCount> noConvertCounts = new HashSet<NoConvertCount>(0);
    private Set<OrderItem> orderItems = new HashSet<OrderItem>(0);

	public ResUnit() {
        this.conversionRate = new BigDecimal(0);
	}

    public ResUnit(UnitGroup unitGroup){
        this.unitGroup = unitGroup;
        this.conversionRate = new BigDecimal(0);
    }

	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
	@NotNull
	@Size(max = 32)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNIT_GROUP", nullable = false)
	@NotNull
	public UnitGroup getUnitGroup() {
		return this.unitGroup;
	}

	public void setUnitGroup(UnitGroup unitGroup) {
		this.unitGroup = unitGroup;
	}

	@Column(name = "NAME", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CONVERSION_RATE", scale = 10)
	public BigDecimal getConversionRate() {
		return this.conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	@Column(name = "PRIORITY", nullable = false)
	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "resUnitByOutDefault")
	public Set<Res> getResesForOutDefault() {
		return this.resesForOutDefault;
	}

	public void setResesForOutDefault(Set<Res> resesForOutDefault) {
		this.resesForOutDefault = resesForOutDefault;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "resUnitByMasterUnit")
	public Set<Res> getResesForMasterUnit() {
		return this.resesForMasterUnit;
	}

	public void setResesForMasterUnit(Set<Res> resesForMasterUnit) {
		this.resesForMasterUnit = resesForMasterUnit;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "resUnitByInDefault")
	public Set<Res> getResesForInDefault() {
		return this.resesForInDefault;
	}

	public void setResesForInDefault(Set<Res> resesForInDefault) {
		this.resesForInDefault = resesForInDefault;
	}

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resUnit")
    public Set<NoConvertCount> getNoConvertCounts() {
        return this.noConvertCounts;
    }

    public void setNoConvertCounts(Set<NoConvertCount> noConvertCounts) {
        this.noConvertCounts = noConvertCounts;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "middleUnit")
    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
