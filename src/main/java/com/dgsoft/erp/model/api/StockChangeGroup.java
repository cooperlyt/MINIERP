package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/12/14.
 */
public class StockChangeGroup {

    //private StoreRes storeRes;
    private String storeResId;

    private StockChange.StoreChangeType type;

    private BigDecimal mastCount;

    public StockChangeGroup(String storeResId, StockChange.StoreChangeType type, BigDecimal mastCount) {
        this.storeResId = storeResId;
        this.type = type;
        this.mastCount = mastCount;
    }

    public String getStoreResId() {
        return storeResId;
    }

    public StockChange.StoreChangeType getType() {
        return type;
    }

    public BigDecimal getMastCount() {
        return mastCount;
    }
}
