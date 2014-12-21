package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class StoreResSaleTotalData {

    private String storeResId;

    private BigDecimal count;

    //private String unitId;

    private Double avgMoney;

    private BigDecimal money;

    private BigDecimal needCount;

    public StoreResSaleTotalData(String storeResId, BigDecimal count, Double avgMoney, BigDecimal money, BigDecimal needCount) {
        this.storeResId = storeResId;
        this.count = count;
       // this.unitId = unitId;
        this.avgMoney = avgMoney;
        this.money = money;
        this.needCount = needCount;
    }

    public String getStoreResId() {
        return storeResId;
    }

    public void setStoreResId(String storeResId) {
        this.storeResId = storeResId;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }


    public Double getAvgMoney() {
        return avgMoney;
    }

    public void setAvgMoney(Double avgMoney) {
        this.avgMoney = avgMoney;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getNeedCount() {
        return needCount;
    }

    public void setNeedCount(BigDecimal needCount) {
        this.needCount = needCount;
    }
}
