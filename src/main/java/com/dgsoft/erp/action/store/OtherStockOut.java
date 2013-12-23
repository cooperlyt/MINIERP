package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreChange;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("otherStockOut")
public class OtherStockOut extends StoreOutAction<StoreChange> {

    @Override
    protected String storeOut() {
        return "OtherStockChangeComplete";
    }

    @Override
    protected String beginStoreOut() {
        return "BeginOtherStockOut";
    }

    @Override
    protected StockChange.StoreChangeType getStoreChangeType() {
        return getInstance().getReason().getStoreChangeType();
    }

    @Override
    public String cancel() {
        storeOutItems.clear();
        clearInstance();
        stockChangeHome.clearInstance();
        return "OtherStockChangeCancel";
    }
}
