package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.Res;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class ResRebateTotalData {

    private Res res;

    private BigDecimal count;

    private BigDecimal money;

    public ResRebateTotalData(Res res, BigDecimal count, BigDecimal money) {
        this.res = res;
        this.count = count;
        this.money = money;
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
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
