package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.Stock;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/20/14.
 */
public class StockView {

    private Stock stock;

    private StoreResCount saleCount;

    public StockView(Stock stock, BigDecimal saleMasterCount) {
        this.stock = stock;
        saleCount = new StoreResCount(stock.getStoreRes(),(saleMasterCount == null) ? BigDecimal.ZERO : saleMasterCount);
    }

    public Stock getStock() {
        return stock;
    }

    public StoreResCount getSaleCount() {
        return saleCount;
    }


    public com.dgsoft.erp.total.data.ResCount getCanUseCount(){
        return stock.getStoreResCount().subtract(saleCount);
    }
}
