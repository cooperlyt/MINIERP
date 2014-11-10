package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/10/14.
 */
public class StoreResTotalData {

    private StoreRes storeRes;

    private StoreResCount storeResCount;

    private BigDecimal money;


    public StoreResTotalData(StoreRes storeRes, BigDecimal masterCount, BigDecimal money) {
        this.storeRes = storeRes;
        this.storeResCount = new StoreResCount(storeRes,masterCount);
        this.money = money;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public StoreResCount getStoreResCount() {
        return storeResCount;
    }

    public BigDecimal getMoney() {
        return money;
    }


}
