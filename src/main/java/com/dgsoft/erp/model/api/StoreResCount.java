package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.total.data.*;
import com.dgsoft.erp.total.data.ResCount;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by cooper on 2/27/14.
 */
public class StoreResCount extends StoreResCountEntity implements com.dgsoft.erp.total.data.ResCount,Serializable{

    private BigDecimal count;

    private StoreRes storeRes;

    public StoreResCount(StoreRes storeRes, BigDecimal count) {
        this.count = count;
        this.storeRes = storeRes;
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


    @Override
    public ResCount add(ResCount other) {
        if (!(other instanceof StoreResCount) || !((StoreResCount)other).getStoreRes().equals(storeRes)){
            throw new IllegalArgumentException("not same StoreRes canot oper");
        }
        return new StoreResCount(storeRes,getMasterCount().add(other.getMasterCount()));
    }

    @Override
    public ResCount subtract(ResCount other) {
        if (!(other instanceof StoreResCount) || !((StoreResCount)other).getStoreRes().equals(storeRes)){
            throw new IllegalArgumentException("not same StoreRes canot oper");
        }
        return new StoreResCount(storeRes,getMasterCount().subtract(other.getMasterCount()));
    }

    @Override
    public BigDecimal getCountByUnit(ResUnit resUnit) {
        return getCountByResUnit(resUnit);
    }
}
