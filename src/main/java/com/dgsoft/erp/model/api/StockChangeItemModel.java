package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/8/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StockChangeItemModel {

    public abstract StockChange getStockChange();

    public abstract StoreRes getStoreRes();

    public abstract ResCount getResCount();
}
