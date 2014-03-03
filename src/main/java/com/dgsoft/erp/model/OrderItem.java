package com.dgsoft.erp.model;
// Generated Oct 30, 2013 3:06:10 PM by Hibernate Tools 4.0.0

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.erp.model.api.ResCount;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * OrderItem generated by hbm2java
 */
@Entity
@Table(name = "ORDER_ITEM", catalog = "MINI_ERP")
public class OrderItem implements java.io.Serializable {

    public enum MiddleMoneyCalcType {
        COUNT_FIX, MONEY_RATE;
    }

    private String id;
    private StoreRes storeRes;
    private ResUnit middleUnit;
    private ResUnit moneyUnit;
    private NeedRes needRes;
    private BigDecimal count;
    private BigDecimal money;
    private BigDecimal rebate;
    private BigDecimal middleMoney;
    private BigDecimal middleRate;
    private MiddleMoneyCalcType middleMoneyCalcType;
    private String memo;

    public OrderItem() {
    }

    public OrderItem(NeedRes needRes, StoreRes storeRes,
                     ResUnit moneyUnit, BigDecimal count, BigDecimal money, BigDecimal rebate, String memo) {
        this.storeRes = storeRes;
        this.moneyUnit = moneyUnit;
        this.count = count;
        this.money = money;
        this.rebate = rebate;
        this.needRes = needRes;
        this.memo = memo;
    }

    public OrderItem(NeedRes needRes, StoreRes storeRes) {
        this.storeRes = storeRes;
        this.needRes = needRes;
    }

    public OrderItem(StoreRes storeRes, ResUnit moneyUnit,
                     BigDecimal count, BigDecimal money, BigDecimal rebate) {
        this.storeRes = storeRes;
        this.moneyUnit = moneyUnit;
        this.count = count;
        this.money = money;
        this.rebate = rebate;
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

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_RES", nullable = true)
    public StoreRes getStoreRes() {
        return this.storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MIDDLE_UNIT", nullable = true)
    public ResUnit getMiddleUnit() {
        return middleUnit;
    }

    public void setMiddleUnit(ResUnit middleUnit) {
        this.middleUnit = middleUnit;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MONEY_UNIT", nullable = false)
    @NotNull
    public ResUnit getMoneyUnit() {
        return moneyUnit;
    }

    public void setMoneyUnit(ResUnit moneyUnit) {
        this.moneyUnit = moneyUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEED_RES", nullable = false)
    @NotNull
    public NeedRes getNeedRes() {
        return this.needRes;
    }

    public void setNeedRes(NeedRes needRes) {
        this.needRes = needRes;
    }

    @Column(name = "COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return this.count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Column(name = "MONEY", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getMoney() {
        return this.money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Column(name = "REBATE", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getRebate() {
        return this.rebate;
    }

    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    @Column(name = "MIDDLE_MONEY", nullable = true, scale = 3)
    public BigDecimal getMiddleMoney() {
        return this.middleMoney;
    }

    public void setMiddleMoney(BigDecimal middleMoney) {
        this.middleMoney = middleMoney;
    }

    @Column(name = "MIDDLE_RATE", nullable = true, scale = 4)
    public BigDecimal getMiddleRate() {
        return this.middleRate;
    }

    public void setMiddleRate(BigDecimal middleRate) {
        this.middleRate = middleRate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MIDDLE_CALC_TYPE", nullable = true)
    public MiddleMoneyCalcType getMiddleMoneyCalcType() {
        return middleMoneyCalcType;
    }

    public void setMiddleMoneyCalcType(MiddleMoneyCalcType middleMoneyCalcType) {
        this.middleMoneyCalcType = middleMoneyCalcType;
    }

    @Column(name = "MEMO", nullable = true, length = 200)
    @Size(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    private ResCount resCount = null;

    @Transient
    public ResCount getStoreResCount() {

        if (resCount == null) {
            generateResCount();
        }
        return resCount;
    }

    @Transient
    public void generateResCount() {

        if (getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            resCount = new ResCount(getCount(), getMoneyUnit(), getStoreRes().getFloatConversionRate());
        } else {
            resCount = new ResCount(getCount(), getMoneyUnit());
        }

    }

    @Transient
    public void addCount(OrderItem orderItem) {
        if (!getStoreRes().getId().equals(orderItem.getStoreRes().getId())) {
            throw new IllegalArgumentException("only same storeRes item can add!");
        }
        if (resCount == null) {
            generateResCount();
        }
        resCount.add(orderItem.getStoreResCount());


    }

    @Transient
    public BigDecimal getTotalMoney() {
        return DataFormat.halfUpCurrency(getMoney().multiply(getCount()).multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    @Transient
    public BigDecimal getRebateUnitPrice(){
        return DataFormat.halfUpCurrency(getMoney().multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));

    }

    @Transient
    public OrderItem cloneNew() {
        OrderItem result;
        result = new OrderItem(getNeedRes(), getStoreRes(), getMoneyUnit(), getCount(), getMoney(), getRebate(), getMemo());

        return result;
    }

    @Transient
    public boolean isSameItem(OrderItem orderItem) {
        return getStoreRes().equals(orderItem.getStoreRes()) &&
                        getMoneyUnit().getId().equals(orderItem.getMoneyUnit().getId()) &&
                        getMoney().equals(orderItem.getMoney()) &&
                        getRebate().equals(orderItem.getRebate());
    }

}
