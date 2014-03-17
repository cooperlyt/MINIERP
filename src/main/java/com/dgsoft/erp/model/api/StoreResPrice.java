package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.StoreRes;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/03/14
 * Time: 15:50
 */
public class StoreResPrice extends StoreResPriceEntity implements Serializable{

    private BigDecimal money;

    private ResUnit resUnit;

    private BigDecimal rebate;

    private BigDecimal count;

    private StoreRes storeRes;

    private boolean presentation;

    public StoreResPrice(BigDecimal money, ResUnit resUnit,
                         BigDecimal rebate, BigDecimal count, StoreRes storeRes, boolean presentation) {
        this.money = money;
        this.resUnit = resUnit;
        this.rebate = rebate;
        this.count = count;
        this.storeRes = storeRes;
        this.presentation = presentation;
    }

    @Override
    public BigDecimal getMoney() {
        return money;
    }

    @Override
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public ResUnit getResUnit() {
        return resUnit;
    }

    @Override
    public void setResUnit(ResUnit resUnit) {
       this.resUnit = resUnit;
    }

    @Override
    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    @Override
    public BigDecimal getRebate() {
        return rebate;
    }

    @Override
    public boolean isPresentation() {
        return presentation;
    }

    @Override
    public void setPresentation(boolean presentation) {
        this.presentation = presentation;
    }

    @Override
    public BigDecimal getCount() {
        return count;
    }

    @Override
    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Override
    public StoreRes getStoreRes() {
        return storeRes;
    }

    @Override
    public void setStoreRes(StoreRes storeRes) {
       this.storeRes = storeRes;
    }
}
