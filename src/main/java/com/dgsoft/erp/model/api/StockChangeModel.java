package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StockChange;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:36 AM
 */
@Deprecated
public interface StockChangeModel {

    public abstract String getId() ;

    public abstract void setId(String id);

    public abstract StockChange getStockChange() ;

    public abstract void setStockChange(StockChange stockChange);
}
