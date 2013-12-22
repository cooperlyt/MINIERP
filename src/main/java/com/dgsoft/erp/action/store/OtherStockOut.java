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
    protected String beginStoreChange() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String storeChange(boolean verify) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected StockChange.StoreChangeType getStoreChangeType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String addItem() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String cancel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
