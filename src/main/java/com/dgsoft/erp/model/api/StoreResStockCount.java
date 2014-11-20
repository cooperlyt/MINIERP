package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;

/**
 * Created by cooper on 4/12/14.
 */
public class StoreResStockCount extends StoreResCount{

    private StockView stockView;

    public StoreResStockCount(StoreRes storeRes, BigDecimal count, StockView stockView) {
        super(storeRes, count);
        this.stockView = stockView;
    }

    public boolean isEnough(){
        if (getStockView() == null){
            return false;
        }
        return getStockView().getCanUseCount().compareTo(this) >= 0;
    }

    public StockView getStockView() {
        return stockView;
    }

    public com.dgsoft.erp.total.data.ResCount getDisparity(){
        if (getStockView() == null){
            return new StoreResCount(getStoreRes(),getMasterCount());
        }
        return  this.subtract(stockView.getCanUseCount());
    }

}
