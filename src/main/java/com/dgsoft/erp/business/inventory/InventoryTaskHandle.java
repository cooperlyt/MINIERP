package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.InventoryHome;
import com.dgsoft.erp.model.PrepareStockChange;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.api.StockChangeItemModel;
import org.jboss.seam.annotations.In;

import java.util.ArrayList;
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
        if (inventoryItems == null){
            generateItems();
        }
        return inventoryItems;
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
        List<PrepareStockChange> loseStocks;

        if (inventoryHome.getInstance().getStockChangeAdd() != null) {
            addStocks = inventoryHome.getInstance().getStockChangeAdd().getPrepareStockChangeList();
        } else {
            addStocks = new ArrayList<PrepareStockChange>(0);
        }

        if (inventoryHome.getInstance().getStockChangeLoss() != null) {
            loseStocks = inventoryHome.getInstance().getStockChangeLoss().getPrepareStockChangeList();
        } else {
            loseStocks = new ArrayList<PrepareStockChange>(0);
        }

        for (Stock stock : inventoryHome.getInstance().getStore().getStocks()) {
            InventoryItem item = new InventoryItem(stock);
            for (PrepareStockChange changeItem: addStocks){
                if (changeItem.getStoreRes().equals(stock.getStoreRes())){
                    item.setAddStock(changeItem);
                    break;
                }
            }
            for (PrepareStockChange changeItem: loseStocks){
                if (changeItem.getStoreRes().equals(stock.getStoreRes())){
                    item.setLoseStock(changeItem);
                    break;
                }
            }
            inventoryItems.add(item);
        }
    }


    public static class InventoryItem {

        private Stock stock;

        private StockChangeItemModel addStock;

        private StockChangeItemModel loseStock;

        public InventoryItem(Stock stock) {
            this.stock = stock;
        }

        public Stock getStock() {
            return stock;
        }

        public void setStock(Stock stock) {
            this.stock = stock;
        }

        public StockChangeItemModel getAddStock() {
            return addStock;
        }

        public void setAddStock(StockChangeItemModel addStock) {
            this.addStock = addStock;
        }

        public StockChangeItemModel getLoseStock() {
            return loseStock;
        }

        public void setLoseStock(StockChangeItemModel loseStock) {
            this.loseStock = loseStock;
        }
    }

}
