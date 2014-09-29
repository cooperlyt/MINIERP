package com.dgsoft.erp.model;

import com.dgsoft.common.NamedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/03/14
 * Time: 13:55
 */
@Entity
@Table(name = "REBATE_PROGRAM", catalog = "MINI_ERP")
public class RebateProgram implements java.io.Serializable,NamedEntity {

    public enum OrderRebateMode {
        NOT_CALC, CONSULT_FIX, TOTAL_MONEY_RATE;
    }

    private String id;
    private OrderRebateMode orderMode;
    private String name;
    private String description;
    private boolean enable;
    private BigDecimal rebate;
    private boolean calcItem;
    private boolean zeroItem;
    private boolean patchItem;
    private Set<OrderItemRebate> orderItemRebates = new HashSet<OrderItemRebate>(0);
    private Set<MiddleMan> middleMans = new HashSet<MiddleMan>(0);
    private Set<Customer> customers = new HashSet<Customer>(0);

    public RebateProgram() {
    }

    public RebateProgram(OrderRebateMode orderMode, boolean calcItem, boolean enable) {
        this.orderMode = orderMode;
        this.calcItem = calcItem;
        this.enable = enable;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_MODE", nullable = false, length = 20)
    @NotNull
    public OrderRebateMode getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(OrderRebateMode orderMode) {
        this.orderMode = orderMode;
    }

    @Column(name = "NAME", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", nullable = true, length = 200)
    @Size(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(name = "REBATE", nullable = true, scale = 6)
    public BigDecimal getRebate() {
        return rebate;
    }

    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    @Column(name = "CALC_ITEM",nullable = false)
    public boolean isCalcItem() {
        return calcItem;
    }

    public void setCalcItem(boolean calcItem) {
        this.calcItem = calcItem;
    }

    @OneToMany(orphanRemoval = true,fetch = FetchType.LAZY,mappedBy = "rebateProgram", cascade = {CascadeType.ALL})
    public Set<OrderItemRebate> getOrderItemRebates() {
        return orderItemRebates;
    }

    public void setOrderItemRebates(Set<OrderItemRebate> orderItemRebates) {
        this.orderItemRebates = orderItemRebates;
    }

    @OneToMany(orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "rebateProgram")
    public Set<MiddleMan> getMiddleMans() {
        return middleMans;
    }

    public void setMiddleMans(Set<MiddleMan> middleMans) {
        this.middleMans = middleMans;
    }

    @Column(name="ZERO_ITEM", nullable = false)
    public boolean isZeroItem() {
        return zeroItem;
    }

    public void setZeroItem(boolean zeroItem) {
        this.zeroItem = zeroItem;
    }

    @Column(name= "PATCH_ITEM", nullable = false)
    public boolean isPatchItem() {
        return patchItem;
    }

    public void setPatchItem(boolean onlyMaster) {
        this.patchItem = onlyMaster;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rebateProgram")
    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    @Transient
    public List<OrderItemRebate> getOrderItemRebateList(){
        List<OrderItemRebate> result = new ArrayList<OrderItemRebate>(getOrderItemRebates());
        Collections.sort(result);
        return result;
    }
}
