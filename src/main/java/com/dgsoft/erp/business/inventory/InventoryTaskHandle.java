package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.InventoryHome;
import com.dgsoft.erp.model.PrepareStockChange;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/8/13
 * Time: 10:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InventoryTaskHandle extends TaskHandle {

    @In(create = true)
    protected InventoryHome inventoryHome;

    private List<InventoryItem> inventoryItems;

    protected String initInventoryTask() {
        return "success";
    }

    protected String completeInventoryTask() {
        return "taskComplete";
    }

    public List<InventoryItem> getInventoryItems() {
        if (inventoryItems == null) {
            generateItems();
        }
        return inventoryItems;
    }


    @Observer(value = "org.jboss.seam.afterTransactionSuccess.Inventory", create = false)
    public void inventoryChange() {
        inventoryItems = null;
    }

    @Override
    protected String completeTask() {
        return completeInventoryTask();
    }

    @Override
    protected String initTask() {
        inventoryHome.setId(taskInstance.getProcessInstance().getKey());
        return initInventoryTask();
    }

    private void generateItems() {

        inventoryItems = new ArrayList<InventoryItem>();
        List<PrepareStockChange> addStocks;
        List<PrepareStockChange> newStocks;
        List<PrepareStockChange> loseStocks;

        if (inventoryHome.getInstance().getStockChangeAdd() != null) {
            addStocks = inventoryHome.getInstance().getStockChangeAdd().getPrepareStockChangeList();
            newStocks = new ArrayList<PrepareStockChange>(addStocks);
        } else {
            addStocks = new ArrayList<PrepareStockChange>(0);
            newStocks = new ArrayList<PrepareStockChange>(0);
        }

        if (inventoryHome.getInstance().getStockChangeLoss() != null) {
            loseStocks = inventoryHome.getInstance().getStockChangeLoss().getPrepareStockChangeList();
        } else {
            loseStocks = new ArrayList<PrepareStockChange>(0);
        }

        for (Stock stock : inventoryHome.getInstance().getStore().getStocks()) {
            InventoryItem item = new InventoryItem(stock.getStoreRes(), stock.getResCount());
            for (PrepareStockChange changeItem : addStocks) {
                if (changeItem.getStoreRes().equals(stock.getStoreRes())) {
                    item.setAddCount(changeItem.getResCount());
                    newStocks.remove(changeItem);
                    break;
                }
            }
            for (PrepareStockChange changeItem : loseStocks) {
                if (changeItem.getStoreRes().equals(stock.getStoreRes())) {
                    item.setLoseCount(changeItem.getResCount());
                    break;
                }
            }
            inventoryItems.add(item);
        }

        for (PrepareStockChange prepareStockChange: newStocks){
            InventoryItem item = new InventoryItem(prepareStockChange.getStoreRes());
            item.setAddCount(prepareStockChange.getResCount());
            inventoryItems.add(item);
        }

        Collections.sort(inventoryItems,new Comparator<InventoryItem>() {
            @Override
            public int compare(InventoryItem o1, InventoryItem o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });

    }


    public static class InventoryItem {

        private StoreRes storeRes;

        private ResCount stockCount;

        private ResCount addCount;

        private ResCount loseCount;

        public InventoryItem(StoreRes storeRes){
            this.storeRes = storeRes;
        }

        public InventoryItem(StoreRes storeRes, ResCount stockCount) {
            this.storeRes = storeRes;
            this.stockCount = stockCount;
        }

        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public ResCount getStockCount() {
            return stockCount;
        }

        public void setStockCount(ResCount stockCount) {
            this.stockCount = stockCount;
        }

        public ResCount getAddCount() {
            return addCount;
        }

        public void setAddCount(ResCount addCount) {
            this.addCount = addCount;
        }

        public ResCount getLoseCount() {
            return loseCount;
        }

        public void setLoseCount(ResCount loseCount) {
            this.loseCount = loseCount;
        }
    }

}
