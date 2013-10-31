package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.MaterialStoreIn;
import com.dgsoft.erp.model.ProductStoreIn;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/21/13
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("produceStoreInHome")
public class ProduceStoreInHome extends StoreInAction<ProductStoreIn> {


    @DataModelSelection
    private StoreInItem selectedStoreInItem;

    @DataModel("porduceStoreInItems")
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
    public String beginStoreIn() {
        stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.PRODUCE_IN);
        return "ProcduceBeginStoreIn";
    }

    @Override
    protected String storeIn() {
        return "ProcduceStoreInComplete";
    }

    @Override
    public String cancel() {
        clearInstance();
        storeInItems.clear();
        return "ProcduceStoreInCancel";
    }

}
