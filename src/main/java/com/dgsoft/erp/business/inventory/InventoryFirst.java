package com.dgsoft.erp.business.inventory;

import com.dgsoft.erp.model.Inventory;
import com.dgsoft.erp.model.InventoryItem;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/8/13
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("inventoryFirst")
@Scope(ScopeType.CONVERSATION)
public class InventoryFirst extends InventoryTaskHandle {

    private boolean abort;


    public boolean isAbort() {
        return abort;
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    private boolean valid(){
        for(InventoryItem item : inventoryHome.getInstance().getInventoryItems()){
            if (BigDecimal.ZERO.compareTo(item.getLastCount()) > 0){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"InventoryLessZeroError",item.getStock().getStoreRes().getCode());
                return false;
            }
        }
        return true;
    }

    private String changeStock(){
        if (!valid()){
            return null;
        }
        for(InventoryItem item : inventoryHome.getInstance().getInventoryItems()){
            switch (item.getChangeType()){
                case NO_CHANGE:
                    break;
                case INVENTORY_ADD:
                    StockChange addChange = inventoryHome.getInstance().getStockChangeAdd();
                    if (addChange == null) {
                        addChange = new StockChange(inventoryHome.getId() + "-A", inventoryHome.getInstance().getStore(),
                                inventoryHome.getInstance().getCheckDate(), credentials.getUsername(),
                                StockChange.StoreChangeType.STORE_CHECK_ADD, inventoryHome.getInstance().getMemo(), true);
                        addChange.setInventory(inventoryHome.getInstance());
                        inventoryHome.getInstance().getStockChanges().add(addChange);
                    }

                    item.setStockChangeItem(new StockChangeItem(addChange,item.getStock(),item.getChangeCount()));
                    addChange.getStockChangeItems().add(item.getStockChangeItem());
                    item.getStock().setCount(item.getStock().getCount().add(item.getChangeCount()));
                    break;
                case INVENTORY_LOSS:

                    StockChange lossChange = inventoryHome.getInstance().getStockChangeLoss();
                    if (lossChange == null) {
                        lossChange = new StockChange(inventoryHome.getId() + "-L", inventoryHome.getInstance().getStore(),
                                inventoryHome.getInstance().getCheckDate(), credentials.getUsername(),
                                StockChange.StoreChangeType.STORE_CHECK_LOSS, inventoryHome.getInstance().getMemo(), true);
                        lossChange.setInventory(inventoryHome.getInstance());
                        inventoryHome.getInstance().getStockChanges().add(lossChange);
                    }

                    item.setStockChangeItem(new StockChangeItem(lossChange,item.getStock(),item.getChangeCount()));
                    lossChange.getStockChangeItems().add(item.getStockChangeItem());
                    item.getStock().setCount(item.getStock().getCount().subtract(item.getChangeCount()));
                    break;
            }
        }
        inventoryHome.getInstance().setStockChanged(true);
        inventoryHome.getInstance().setStatus(Inventory.InvertoryStatus.CHECKING);
        return ("updated".equals(inventoryHome.update())) ? "taskComplete" : null;
    }



    @Override
    protected String completeInventoryTask() {
        if (!abort){
           return changeStock();
        }else{
            return ("removed".equals(inventoryHome.deleteInventory())) ? "taskComplete" : null;

        }
    }
}
