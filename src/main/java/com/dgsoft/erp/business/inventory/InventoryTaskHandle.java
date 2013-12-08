package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.InventoryHome;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.api.StockChangeItemModel;
import org.jboss.seam.annotations.In;

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

    protected String initInventoryTask(){
        return "success";
    }

    protected String completeInventoryTask(){
        return "taskComplete";
    }

    public List<InventoryItem> getInventoryItems() {
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

    private void generateItems(){
        for(Stock stock: inventoryHome.getInstance().getStore().getStocks()){

        }
    }



    public static class InventoryItem{

        private Stock stock;

        private StockChangeItemModel addStock;

        private StockChangeItemModel loseStock;


    }

}
