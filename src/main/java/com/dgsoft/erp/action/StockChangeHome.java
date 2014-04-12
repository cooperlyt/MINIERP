package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Batch;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:01 AM
 */

@Name("stockChangeHome")
public class StockChangeHome extends ErpEntityHome<StockChange> {

    @In
    private org.jboss.seam.security.Credentials credentials;

    @Override
    protected StockChange createInstance(){
        return new StockChange(true);
    }

    @Override
    protected boolean wire() {
        if (!isManaged())
            getInstance().setOperEmp(credentials.getUsername());
        return true;
    }

    public void resStockChange(Collection<StockChangeItem> items) {
        for (StockChangeItem item : items) {
            resStockChange(item);
        }
    }

    public void resStockChange(StockChangeItem item) {
        if (item.getStock() == null) {
            item.setStock(findOrCreactStock(item));
        } else {
            if (getInstance().getOperType().isOut()) {
                item.getStock().setCount(item.getStock().getCount().subtract(item.getMasterCount()));
            } else
                item.getStock().setCount(item.getStock().getCount().add(item.getMasterCount()));
        }
        getInstance().getStockChangeItems().add(item);
    }

    public void resStockChange(StoreResCountEntity inCount) {
        Stock inStock = findOrCreactStock(inCount);
        getInstance().getStockChangeItems().add(new StockChangeItem(getInstance(), inStock,
                inCount.getMasterCount()));

    }

    private Stock findOrCreactStock(StoreResCountEntity inCount) {
        Stock inStock = null;
        for (Stock stock : inCount.getStoreRes().getStocks()) {
            if (stock.getStore().getId().equals(getInstance().getStore().getId())) {
                inStock = stock;
                break;
            }
        }
        if (inStock == null) {
            inStock = new Stock(getInstance().getStore(), inCount.getStoreRes(), BigDecimal.ZERO);
        }

        if (getInstance().getOperType().isOut()) {
            inStock.setCount(inStock.getCount().subtract(inCount.getMasterCount()));
        } else
            inStock.setCount(inStock.getCount().add(inCount.getMasterCount()));
        return inStock;
    }


}
