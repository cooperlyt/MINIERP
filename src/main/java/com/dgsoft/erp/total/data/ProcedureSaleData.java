package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.Res;

import java.math.BigDecimal;

/**
 * Created by cooper on 7/25/14.
 */
public class ProcedureSaleData {

    private String resId;

    private Object areaName;

    private String resName;

    private String saleUnitId;

    private String saleUnitName;

    private BigDecimal saleCount;

    private BigDecimal salePrice;

    private BigDecimal rebateMoney = BigDecimal.ZERO;

    private BigDecimal rebateCount = BigDecimal.ZERO;

    private BigDecimal backCount = BigDecimal.ZERO;

    private BigDecimal backMoney = BigDecimal.ZERO;

    public ProcedureSaleData(Object areaName, String resId, BigDecimal salePrice, BigDecimal saleCount, String unitId) {
        this.resId = resId;
        this.areaName = areaName;
        this.saleCount = saleCount;
        this.salePrice = salePrice;
        this.saleUnitId = unitId;
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

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public BigDecimal getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(BigDecimal saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getRebateMoney() {
        return rebateMoney;
    }

    public void setRebateMoney(BigDecimal rebateMoney) {
        this.rebateMoney = rebateMoney;
    }

    public BigDecimal getBackCount() {
        return backCount;
    }

    public void setBackCount(BigDecimal backCount) {
        this.backCount = backCount;
    }

    public BigDecimal getBackMoney() {
        return backMoney;
    }

    public void setBackMoney(BigDecimal backMoney) {
        this.backMoney = backMoney;
    }

    public BigDecimal getRebateCount() {
        return rebateCount;
    }

    public void setRebateCount(BigDecimal rebateCount) {
        this.rebateCount = rebateCount;
    }



    public String getSaleUnitId() {
        return saleUnitId;
    }

    public void setSaleUnitId(String saleUnitId) {
        this.saleUnitId = saleUnitId;
    }

    public String getSaleUnitName() {
        return saleUnitName;
    }

    public void setSaleUnitName(String saleUnitName) {
        this.saleUnitName = saleUnitName;
    }


    public BigDecimal getSaleMoney() {
        return salePrice.subtract(rebateMoney);
    }

    public BigDecimal getMoney(){
        return getSaleMoney().subtract(getBackMoney());
    }

    public BigDecimal getCount(){
        return getSaleCount().subtract(getBackCount());
    }

}
