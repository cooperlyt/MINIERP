package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Store;

import java.util.Collection;

/**
 * Created by cooper on 4/12/14.
 */
public class ResStockCountTotalGroup extends StoreResCountGroup<StoreResStockCount> implements java.io.Serializable {

    private Store store;

    public ResStockCountTotalGroup(Collection<? extends StoreResCountEntity> values, Store store) {
        super();
        this.store = store;
        put(values);
    }

    public ResStockCountTotalGroup(Store store) {
        this.store = store;
    }

    public <E extends StoreResCountEntity> StoreResCount put(E v) {
        return super.put(new StoreResStockCount(v.getStoreRes(), v.getMasterCount(),store));
    }

    public void put(Collection<? extends StoreResCountEntity> values) {
        for (StoreResCountEntity v : values) {
            put(v);
        }
    }

}
