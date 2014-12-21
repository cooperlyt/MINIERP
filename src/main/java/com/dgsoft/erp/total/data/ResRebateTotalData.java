package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class ResRebateTotalData {

    private String resId;

    private BigDecimal count;

    private BigDecimal money;

    public ResRebateTotalData(String resId, BigDecimal count, BigDecimal money) {
        this.resId = resId;
        this.count = count;
        this.money = money;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
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
