package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 7/25/14.
 */
public class ResSaleRebateTotalData {

    private Object areaName;

    private String resId;

    private String resUnitId;

    private BigDecimal count;

    private BigDecimal money;

    public ResSaleRebateTotalData(Object areaName, String resId, String resUnitId, BigDecimal count, BigDecimal money) {
        this.areaName = areaName;
        this.resId = resId;
        this.resUnitId = resUnitId;
        this.count = count;
        this.money = money;
    }

    public Object getAreaName() {
        return areaName;
    }

    public void setAreaName(Object areaName) {
        this.areaName = areaName;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResUnitId() {
        return resUnitId;
    }

    public void setResUnitId(String resUnitId) {
        this.resUnitId = resUnitId;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
