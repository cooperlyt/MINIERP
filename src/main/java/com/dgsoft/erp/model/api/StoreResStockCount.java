package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 4/12/14.
 */
public class StoreResStockCount extends StoreResCount{

    private Store store;

    public StoreResStockCount(StoreRes storeRes, BigDecimal count, Store store) {
        super(storeRes, count);
        this.store = store;
    }

    public boolean isEnough(){
        if (getStock() == null){
            return false;
        }
        return getStock().getCount().compareTo(getCount()) >= 0;
    }

    public Stock getStock(){
        return getStoreRes().getStock(store);
    }

    public StoreResCount getDisparity(){
        if (getStock() == null){
            return new StoreResCount(getStoreRes(),getCount());
        }
        return new StoreResCount(getStoreRes(),getCount().subtract(getStock().getCount()));
    }

}
