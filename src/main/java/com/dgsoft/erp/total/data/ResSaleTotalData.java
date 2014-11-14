package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/10/14.
 */
public class ResSaleTotalData {

    private ResCount totalCount;

    private BigDecimal money;


    public ResSaleTotalData(ResCount totalCount, BigDecimal money) {
        this.totalCount = totalCount;
        this.money = money;
    }

    public ResSaleTotalData add(ResSaleTotalData other){
          return new ResSaleTotalData(this.totalCount.add(other.getTotalCount()),this.money.add(other.getMoney()));
    }

    public ResCount getTotalCount() {
        return totalCount;
    }

    public BigDecimal getMoney() {
        return money;
    }


}
