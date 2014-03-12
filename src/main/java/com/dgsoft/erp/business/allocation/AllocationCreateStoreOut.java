package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.AllocationHome;
import com.dgsoft.erp.action.store.StoreOutAction;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.List;

/**
 * Created by cooper on 3/11/14.
 */
@Name("allocationCreateStoreOut")
@Scope(ScopeType.CONVERSATION)
public class AllocationCreateStoreOut extends StoreOutAction{

    @In
    private AllocationHome allocationHome;

    @In(create=true)
    private BusinessCreate businessCreate;

    @DataModel(value = "allocationCreateStockOutItems")
    public List<StockChangeItem> getStoreOutItems(){
        return storeOutItems;
    }

    @DataModelSelection
    private StockChangeItem stockChangeItem;

    @Override
    protected StockChangeItem getSelectOutItem() {
        return stockChangeItem;
    }

    public String create(){
        super.storeChange(true);

        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setAllocationForStoreOut(allocationHome.getInstance());
        allocationHome.getInstance().setCreateDate(stockChangeHome.getInstance().getOperDate());
        allocationHome.getInstance().setStockChangeByStoreOut(stockChangeHome.getReadyInstance());

        if ("persisted".equals(allocationHome.persist())){
            return businessCreate.create();
        }else{
            return null;
        }

    }

}
