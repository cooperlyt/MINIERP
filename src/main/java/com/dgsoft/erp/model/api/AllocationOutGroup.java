package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/12/14.
 */
public class AllocationOutGroup {

    private StoreRes storeRes;

    private Store store;

    private BigDecimal mastCount;

    public AllocationOutGroup(StoreRes storeRes, Store store, BigDecimal mastCount) {
        this.storeRes = storeRes;
        this.store = store;
        this.mastCount = mastCount;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public BigDecimal getMastCount() {
        return mastCount;
    }

    public Store getStore() {
        return store;
    }
}
