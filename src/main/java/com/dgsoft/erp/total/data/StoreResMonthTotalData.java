package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/10/14.
 */
public class StoreResMonthTotalData extends StoreResTotalData {

    private Integer month;

    public StoreResMonthTotalData(StoreRes storeRes, Integer month, BigDecimal masterCount, BigDecimal money) {
        super(storeRes, masterCount, money);
        this.month = month;
    }

    public Integer getMonth() {
        return month;
    }
}
