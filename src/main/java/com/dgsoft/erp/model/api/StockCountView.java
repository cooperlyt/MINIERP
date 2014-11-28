package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Res;

/**
 * Created by cooper on 11/28/14.
 */
public abstract class StockCountView {

    public abstract Res getRes();

    public abstract com.dgsoft.erp.total.data.ResCount getStockCount();

    public abstract com.dgsoft.erp.total.data.ResCount getSaleCount();

    public com.dgsoft.erp.total.data.ResCount getCanUseCount(){
        return getStockCount().subtract(getSaleCount());
    }
}
