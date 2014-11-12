package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/12/14.
 */
public class StockChangeGroup {

    private StoreRes storeRes;

    private StockChange.StoreChangeType type;

    private BigDecimal mastCount;

    public StockChangeGroup(StoreRes storeRes, StockChange.StoreChangeType type, BigDecimal mastCount) {
        this.storeRes = storeRes;
        this.type = type;
        this.mastCount = mastCount;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public StockChange.StoreChangeType getType() {
        return type;
    }

    public BigDecimal getMastCount() {
        return mastCount;
    }
}
