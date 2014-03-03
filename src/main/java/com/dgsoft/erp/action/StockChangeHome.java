package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Batch;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import java.math.BigDecimal;

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
    protected boolean wire() {
        getInstance().setOperEmp(credentials.getUsername());
        return true;
    }


    public void resStockChange(StoreResCountEntity inCount, Batch batch) {
        Stock inStock = null;
        for (Stock stock : inCount.getStoreRes().getStocks()) {
            if ((stock.getBatch() == batch) &&
                    (stock.getStore().getId().equals(getInstance().getStore().getId()))) {
                inStock = stock;
                break;
            }
        }
        if (inStock == null) {
            inStock = new Stock(getInstance().getStore(), batch, inCount.getStoreRes(), BigDecimal.ZERO);
        }

        if (getInstance().getOperType().isOut()) {
            inStock.setCount(inStock.getCount().subtract(inCount.getMasterCount()));
        } else
            inStock.setCount(inStock.getCount().add(inCount.getMasterCount()));
        getInstance().getStockChangeItems().add(new StockChangeItem(getInstance(), inStock,
                inCount.getMasterCount()));

    }


}
