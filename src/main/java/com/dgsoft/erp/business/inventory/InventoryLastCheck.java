package com.dgsoft.erp.business.inventory;

import com.dgsoft.erp.model.PrepareStockChange;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/16/13
 * Time: 3:01 PM
 */

@Name("inventoryLastCheck")
@Scope(ScopeType.CONVERSATION)
public class InventoryLastCheck extends InventoryTaskHandle {

    private boolean pass;

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    @Override
    protected String completeInventoryTask() {
        if (pass) {
            //TODO batch
            StockChange addStockChange = inventoryHome.getInstance().getStockChangeAdd();

            if (addStockChange != null) {
                for (PrepareStockChange prepareStockChange : addStockChange.getPrepareStockChanges()) {

                    Stock stock = null;
                    for (Stock exisStock : inventoryHome.getInstance().getStore().getStocks()) {
                        if (exisStock.getStoreRes().equals(prepareStockChange.getStoreRes())) {
                            stock = exisStock;
                            break;
                        }
                    }
                    if (stock == null) {
                        stock = new Stock(inventoryHome.getInstance().getStore(), prepareStockChange.getStoreRes(), BigDecimal.ZERO);
                    }

                    StockChangeItem item = new StockChangeItem(addStockChange, stock, prepareStockChange.getCount(), false);


                    addStockChange.getStockChangeItems().add(item);
                    stock.setCount(item.getAfterCount());
                }
            }

            StockChange loseStockChange = inventoryHome.getInstance().getStockChangeLoss();

            if (loseStockChange != null) {
                for (PrepareStockChange prepareStockChange : loseStockChange.getPrepareStockChanges()) {
                    Stock stock = null;
                    for (Stock exisStock : inventoryHome.getInstance().getStore().getStocks()) {
                        if (exisStock.getStoreRes().equals(prepareStockChange.getStoreRes())) {
                            stock = exisStock;
                            break;
                        }
                    }

                    if (stock == null){
                        throw new IllegalArgumentException("stock not exists");
                    }

                    if (stock.getCount().compareTo(prepareStockChange.getCount()) < 0 ){
                        throw new IllegalArgumentException("lose count less stock");
                    }


                    StockChangeItem item = new StockChangeItem(loseStockChange, stock, prepareStockChange.getCount(), true);


                    loseStockChange.getStockChangeItems().add(item);
                    stock.setCount(item.getAfterCount());
                }
            }
            inventoryHome.getInstance().setStockChanged(true);
            if ("updated".equals(inventoryHome.update())){
                return "taskComplete";
            }else{
                return "fail";
            }
        }


        return "taskComplete";
    }
}
