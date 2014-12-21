package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class StoreResBackTotalData {

    private StoreRes storeRes;

    private BigDecimal count;

    private BigDecimal money;

    public StoreResBackTotalData(StoreRes storeRes,BigDecimal count, BigDecimal money) {
        this.storeRes = storeRes;
        this.count = count;
        this.money = money;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public ResCount getResCount(){
        return new StoreResCount(storeRes,count);
    }
}
