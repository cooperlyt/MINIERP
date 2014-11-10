package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCount;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/10/14.
 */
public class ResTotalCount {

    private Res res;

    private BigDecimal masterCount;

    private BigDecimal auxCount;

    public ResTotalCount(StoreResCount storeResCount){
        this.res = storeResCount.getRes();
        this.masterCount = storeResCount.getMasterCount();
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            this.auxCount = storeResCount.getAuxCount();
        }
    }

    public ResTotalCount(Res res, BigDecimal masterCount) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            throw new IllegalArgumentException("float Convert unit must set auxCount");
        }
        this.res = res;
        this.masterCount = masterCount;
    }

    public ResTotalCount(Res res, BigDecimal masterCount, BigDecimal auxCount) {
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            throw new IllegalArgumentException("only float Convert unit can set auxCount");
        }
        this.res = res;
        this.masterCount = masterCount;
        this.auxCount = auxCount;
    }

    public ResTotalCount add(ResTotalCount other){
        if (!getRes().equals(other.getRes())){
            throw new IllegalArgumentException("only same res can do this oper");
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            return new ResTotalCount(res,masterCount.add(other.getMasterCount()),auxCount.add(other.getAuxCount()));
        }else{
            return new ResTotalCount(res,masterCount.add(other.getMasterCount()));
        }
    }

    public ResTotalCount add(StoreResCount storeResCount){
        if (!getRes().equals(storeResCount.getRes())){
            throw new IllegalArgumentException("only same res can do this oper");
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            return add(new ResTotalCount(storeResCount.getRes(),storeResCount.getMasterCount(),storeResCount.getAuxCount()));
        }else{
            return add(new ResTotalCount(storeResCount.getRes(),storeResCount.getMasterCount()));
        }
    }

    public Res getRes() {
        return res;
    }

    public BigDecimal getMasterCount() {
        return masterCount;
    }

    public BigDecimal getAuxCount() {
        return auxCount;
    }
}
