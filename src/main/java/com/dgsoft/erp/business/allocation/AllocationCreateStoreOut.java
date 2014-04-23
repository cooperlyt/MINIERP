package com.dgsoft.erp.business.allocation;

import com.dgsoft.erp.action.AllocationHome;
import com.dgsoft.erp.action.store.StoreOutAction;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.CreateProcess;
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



    //TODO move to process EL
    //----------------
    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessDescription;

    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessName;

    //-----------------

    @CreateProcess(definition = "stockAllocation" , processKey = "#{allocationHome.instance.id}")
    @Transactional
    public String create(){
        super.storeChange(true);

        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setAllocationForStoreOut(allocationHome.getInstance());
        allocationHome.getInstance().setCreateDate(stockChangeHome.getInstance().getOperDate());
        allocationHome.getInstance().setStockChangeByStoreOut(stockChangeHome.getReadyInstance());
        businessDescription = allocationHome.getInstance().getOutStore().getName() + "->" + allocationHome.getInstance().getInStore().getName();
        businessName =  "仓库调拨";

        if ("persisted".equals(allocationHome.persist())){
            return "businessCreated";
        }else{
            return null;
        }

    }

}
