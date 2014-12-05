package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/12/14.
 */
public class AllocationOutGroup {

    //private StoreRes storeRes;
    private String storeResId;

    private Store store;

    private BigDecimal mastCount;

    public AllocationOutGroup(String storeResId, Store store, BigDecimal mastCount) {
        this.storeResId = storeResId;
        this.store = store;
        this.mastCount = mastCount;
    }

    public String getStoreResId() {
        return storeResId;
    }

    public BigDecimal getMastCount() {
        return mastCount;
    }

    public Store getStore() {
        return store;
    }
}
