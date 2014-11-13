package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.action.AllocationHome;
import com.dgsoft.erp.action.store.StoreOutAction;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.total.SameFormatResGroupStrategy;
import com.dgsoft.erp.total.StoreResGroupStrategy;
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
public class AllocationCreateStoreOut extends StoreOutAction {

    @In
    private AllocationHome allocationHome;


    @DataModel(value = "allocationCreateStockOutItems")
    public List<StockChangeItem> getStoreOutItems() {
        return storeOutItems;
    }

    public List<TotalDataGroup<Res, StockChangeItem>> getStockOutGroup() {
        return TotalDataGroup.groupBy(getStoreOutItems(), new StoreResGroupStrategy<StockChangeItem>(), new SameFormatResGroupStrategy<StockChangeItem>());
    }


    @DataModelSelection
    private StockChangeItem stockChangeItem;

    @Override
    protected StockChangeItem getSelectOutItem() {
        return stockChangeItem;
    }

    @End
    @CreateProcess(definition = "stockAllocation", processKey = "#{allocationHome.instance.id}")
    @Transactional
    public String create() {
        super.storeChange(true);

        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setAllocation(allocationHome.getInstance());
        allocationHome.getInstance().setCreateDate(stockChangeHome.getInstance().getOperDate());
        allocationHome.getInstance().setStockChangeByStoreOut(stockChangeHome.getReadyInstance());
        if ("persisted".equals(allocationHome.persist())) {
            return "/business/startPrepare/erp/store/AllocationComplete.xhtml";
        } else {
            return null;
        }

    }

}
