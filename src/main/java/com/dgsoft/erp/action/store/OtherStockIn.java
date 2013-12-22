package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreChange;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("otherStockIn")
public class OtherStockIn extends StoreInAction<StoreChange> {


    @DataModelSelection
    private StoreInItem selectedStoreInItem;

    @DataModel("otherStoreInItems")
    public List<StoreInItem> getStoreInItems() {
        return storeInItems;
    }

    public void setStoreInItems(List<StoreInItem> storeInItems) {
        this.storeInItems = storeInItems;
    }

    @Override
    public void removeItem() {
        storeInItems.remove(selectedStoreInItem);
    }

    @Override
    protected String beginStoreIn() {
        return "BeginOtherStockIn";
    }

    @Override
    protected String storeIn() {
        return "OtherStockChangeComplete";
    }

    @Override
    protected StockChange.StoreChangeType getStoreChangeType() {
        return getInstance().getReason().getStoreChangeType();
    }

    @Override
    public String cancel() {
        storeInItems.clear();
        clearInstance();
        stockChangeHome.clearInstance();
        return "OtherStockChangeCancel";
    }
}
