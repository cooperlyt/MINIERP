package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 2/27/14.
 */
public class StoreResCount extends StoreResCountEntity{

    private BigDecimal count;

    private StoreRes storeRes;

    public StoreResCount(StoreRes storeRes, BigDecimal count) {
        this.count = count;
        this.storeRes = storeRes;
    }

    @Override
    public BigDecimal getMasterCount() {
        return count;
    }

    @Override
    public void setMasterCount(BigDecimal count) {
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