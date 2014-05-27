package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-27
 * Time: 下午5:27
 */
@Entity
@Table(name = "RES_SALE_REBATE", catalog = "MINI_ERP")
public class ResSaleRebate implements Serializable{

    private String id;

    private CustomerOrder customerOrder;
    private Res res;
    private ResUnit resUnit;

    private BigDecimal price;
    private BigDecimal count;
    private BigDecimal money;
    private BigDecimal rebateBasicCount;
    private BigDecimal rebateRateCount;
    private BigDecimal rebateCount;
    private BigDecimal rebateMoney;
    private String memo;


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


    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name = "CUSTOMER_ORDER")
    @NotNull
    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "RES",nullable = false)
    @NotNull
    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "RES_UNIT",nullable = false)
    @NotNull
    public ResUnit getResUnit() {
        return resUnit;
    }

    public void setResUnit(ResUnit resUnit) {
        this.resUnit = resUnit;
    }

    @Column(name = "PRICE",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name="COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Column(name="MONEY",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Column(name="REBATE_BASIC_COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getRebateBasicCount() {
        return rebateBasicCount;
    }

    public void setRebateBasicCount(BigDecimal rebateBasicCount) {
        this.rebateBasicCount = rebateBasicCount;
    }

    @Column(name="REBATE_RATE_COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getRebateRateCount() {
        return rebateRateCount;
    }

    public void setRebateRateCount(BigDecimal rebateRateCount) {
        this.rebateRateCount = rebateRateCount;
    }

    @Column(name="REBATE_COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getRebateCount() {
        return rebateCount;
    }

    public void setRebateCount(BigDecimal rebateCount) {
        this.rebateCount = rebateCount;
    }

    @Column(name="REBATE_MONEY",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getRebateMoney() {
        return rebateMoney;
    }

    public void setRebateMoney(BigDecimal rebateMoney) {
        this.rebateMoney = rebateMoney;
    }

    @Column(name="MEMO",nullable = true,length = 200)
    @Size(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
