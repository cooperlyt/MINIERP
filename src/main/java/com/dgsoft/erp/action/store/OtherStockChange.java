package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.StoreChange;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 8:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("otherStockChange")
public class OtherStockChange {

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
         if (!storeChangeReason.getStoreChangeType().isOut()){

             OtherStockIn otherStockIn = ((OtherStockIn) Component.getInstance("otherStockIn",true,true));
             otherStockIn.getInstance().setReason(storeChangeReason);
             return otherStockIn.begin();
         }else{
             OtherStockOut otherStockOut = (OtherStockOut) Component.getInstance("otherStockOut", true, true);
             otherStockOut.getInstance().setReason(storeChangeReason);
             return otherStockOut.begin();
         }
    }
}
