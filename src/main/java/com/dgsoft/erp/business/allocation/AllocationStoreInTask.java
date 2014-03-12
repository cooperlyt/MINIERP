package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.Allocation;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/03/14
 * Time: 13:41
 */
@Name("allocationStoreInTask")
public class AllocationStoreInTask extends AllocationTaskHandle {


    @In
    protected RunParam runParam;

    @In(create = true)
    private StockChangeHome stockChangeHome;

    private StoreResCountGroup<StockChangeItem> inItemGroup;

    public StoreResCountGroup<StockChangeItem> getInItemGroup() {
        return inItemGroup;
    }

    @Override
    protected void initOrderTask() {
        stockChangeHome.clearInstance();

        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
            stockChangeHome.getInstance().setId("AI-" + allocationHome.getInstance().getId());
        }

        stockChangeHome.getInstance().setStore(allocationHome.getInstance().getInStore());
        stockChangeHome.getInstance().setAllocationForStoreIn(allocationHome.getInstance());
        stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.ALLOCATION_IN);

        inItemGroup = new StoreResCountGroup<StockChangeItem>();

        for (StockChangeItem outItem : allocationHome.getInstance().getStockChangeByStoreOut().getStockChangeItems()) {
            inItemGroup.put(new StockChangeItem(stockChangeHome.getInstance(), outItem.getStoreRes(), outItem.getCount()));
        }

    }

    @Override
    protected String completeOrderTask() {
        stockChangeHome.resStockChange(inItemGroup.getStoreResCountList());

        allocationHome.getInstance().setStockChangeByStoreIn(stockChangeHome.getReadyInstance());
        allocationHome.getInstance().setState(Allocation.AllocationState.ALLOCATION_COMPLETE);

        if ("updated".equals(allocationHome.update())) {
            return "taskComplete";
        } else
            return null;
    }
}
