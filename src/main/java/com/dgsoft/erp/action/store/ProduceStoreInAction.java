package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.List;

/**
 * Created by cooper on 3/30/14.
 */
@Name("produceStoreInAction")
@Scope(ScopeType.CONVERSATION)
public class ProduceStoreInAction extends StoreInAction{


    @DataModel(value = "porduceStoreInItems")
    public List<StockChangeItem> getStoreChangeItems() {
        return storeChangeItems;
    }

    @DataModelSelection
    private StockChangeItem stockChangeItem;

    @Override
    protected StockChangeItem getSelectInItem() {
        return stockChangeItem;
    }



}
