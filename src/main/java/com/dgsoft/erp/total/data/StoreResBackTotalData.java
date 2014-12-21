package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class StoreResBackTotalData {

    private String storeResId;

    //private String unitId;

    private BigDecimal count;

    private BigDecimal money;

    public StoreResBackTotalData(String storeResId,BigDecimal count, BigDecimal money) {
        this.storeResId = storeResId;
        this.count = count;
        this.money = money;
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

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
