package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.OtherStockIn;
import com.dgsoft.erp.action.store.OtherStockOut;
import com.dgsoft.erp.model.StoreChange;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/03/14
 * Time: 15:10
 */
@Name("otherStoreChangeHome")
public class OtherStoreChangeHome extends ErpEntityHome<StoreChange>{

    @In(create = true)
    private StockChangeHome stockChangeHome;

    @Factory(value = "stockChangeReasons" ,scope = ScopeType.CONVERSATION)
    public  StoreChange.StoreChangeReason[] getStockChangeReasons(){
        return StoreChange.StoreChangeReason.values();
    }

    private StoreChange.StoreChangeReason storeChangeReason;

    public StoreChange.StoreChangeReason getStoreChangeReason() {
        return storeChangeReason;
    }

    public void setStoreChangeReason(StoreChange.StoreChangeReason storeChangeReason) {
        this.storeChangeReason = storeChangeReason;
    }

    public String begin() {
        getInstance().setReason(storeChangeReason);
        stockChangeHome.getInstance().setOperType(storeChangeReason.getStoreChangeType());
        if (!storeChangeReason.getStoreChangeType().isOut()){

            OtherStockIn otherStockIn = ((OtherStockIn) Component.getInstance("otherStockIn", true, true));

            return otherStockIn.begin();
        }else{
            OtherStockOut otherStockOut = (OtherStockOut) Component.getInstance("otherStockOut", true, true);


            ((StockList)Component.getInstance("stockList", true, true)).setStoreId(stockChangeHome.getInstance().getStore().getId());
            return otherStockOut.begin();
        }
    }
}
