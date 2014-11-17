package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/03/14
 * Time: 13:40
 */
@Name("allocationStoreOutTask")
public class AllocationStoreOutTask extends AllocationTaskHandle {

    public class AllocationStockOutItem {

        private StockChangeItem stockChangeItem;

        private AllocationRes allocationRes;

        private Stock stock;

        public AllocationStockOutItem(AllocationRes allocationRes) {
            this.allocationRes = allocationRes;
        }

        public double getMaxOutCount() {
            if (stock == null) {
                return BigDecimal.ZERO.doubleValue();
            } else
                return stock.getCountByResUnit(stockChangeItem.getUseUnit()).doubleValue();
        }

        @Transient
        public boolean isEnough() {
            Stock stock = getStock();
            if (stock == null) {
                return false;
            } else {
                return getStock().getCount().compareTo(allocationRes.getCount()) >= 0;
            }
        }

        public StoreResCount getDisparity() {
            if (stock == null) {
                return new StoreResCount(allocationRes.getStoreRes(), allocationRes.getCount());
            } else {
                return new StoreResCount(allocationRes.getStoreRes(), allocationRes.getCount().subtract(stock.getCount()));
            }
        }

        public Stock getStock() {
            return stock;
        }

        public void setStock(Stock stock) {
            this.stock = stock;
        }

        public StockChangeItem getStockChangeItem() {
            return stockChangeItem;
        }

        public void setStockChangeItem(StockChangeItem stockChangeItem) {
            this.stockChangeItem = stockChangeItem;
        }

        public AllocationRes getAllocationRes() {
            return allocationRes;
        }

        public void setAllocationRes(AllocationRes allocationRes) {
            this.allocationRes = allocationRes;
        }
    }

    @In
    protected RunParam runParam;



    @DataModel("allocationStoreOutItems")
    private List<AllocationStockOutItem> outItems;

    @DataModelSelection
    private AllocationStockOutItem selectItem;

    private boolean cancelOrder;

    public boolean isCancelOrder() {
        return cancelOrder;
    }

    public void setCancelOrder(boolean cancelOrder) {
        this.cancelOrder = cancelOrder;
    }

    @Override
    protected void initOrderTask() {
        outItems = new ArrayList<AllocationStockOutItem>();

        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
            stockChangeHome.getInstance().setId("AO-" + allocationHome.getInstance().getId());
        }

        stockChangeHome.getInstance().setStore(allocationHome.getInstance().getOutStore());
        stockChangeHome.getInstance().setAllocation(allocationHome.getInstance());
        stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.ALLOCATION_OUT);

        for (AllocationRes allocationRes : allocationHome.getInstance().getAllocationReses()) {


            //StockChangeItem outChangeItem = new StockChangeItem(allocationRes.getStoreRes() )
            AllocationStockOutItem item = new AllocationStockOutItem(allocationRes);
            for (Stock stock : allocationRes.getStoreRes().getStocks()) {
                if (stock.getStore().getId().equals(allocationHome.getInstance().getOutStore().getId())) {
                    item.setStock(stock);
                    break;
                }
            }
            if (item.getStock() != null) {
                StockChangeItem stockChangeItem =
                        new StockChangeItem(stockChangeHome.getInstance(), item.getStock(),
                                (allocationRes.getCount().compareTo(item.getStock().getCount()) > 0) ? item.getStock().getCount() : allocationRes.getCount());
                stockChangeItem.setUseUnit(allocationRes.getRes().getResUnitByInDefault());
                item.setStockChangeItem(stockChangeItem);
            }
            outItems.add(item);

        }
    }

    public void passedItem() {
        selectItem.getStockChangeItem().setCount(selectItem.getAllocationRes().getCount());
    }

    public void resetItem() {
        selectItem.getStockChangeItem().setCount(BigDecimal.ZERO);
        selectItem.getStockChangeItem().setUseUnit(selectItem.allocationRes.getRes().getResUnitByInDefault());
    }

    @Override
    protected String completeOrderTask() {
        if (cancelOrder) {
            allocationHome.getInstance().setState(Allocation.AllocationState.ALLOCATION_CANCEL);
        } else {
            for (AllocationStockOutItem item : outItems) {
                if ((item.getStockChangeItem() != null) && (item.getStockChangeItem().getCount().compareTo(BigDecimal.ZERO) > 0)) {
                    stockChangeHome.resStockChange(item.getStockChangeItem());
                }
            }
            if (stockChangeHome.getInstance().getStockChangeItems().isEmpty()) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "allocationStoreOutIsNullWarn");
                allocationHome.getInstance().setState(Allocation.AllocationState.ALLOCATION_CANCEL);
            } else {
                allocationHome.getInstance().setStockChangeByStoreOut(stockChangeHome.getReadyInstance());
                allocationHome.getInstance().setState(Allocation.AllocationState.WAITING_IN);
            }
        }

        if ("updated".equals(allocationHome.update())) {
            return "taskComplete";
        } else
            return null;

    }
}
