package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.MaterialStoreIn;
import com.dgsoft.erp.model.ProductStoreIn;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/21/13
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("produceStoreInHome")
public class ProduceStoreInHome extends StoreInAction<ProductStoreIn>{

    @Override
    public void removeItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isIdAvailable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String storeChange() {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String beginStoreChange() {
        stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.PRODUCE_IN);
        return super.beginStoreChange();
    }

    @Override
    public String cancel() {
        clearInstance();
        storeInItems.clear();
        return "/func/erp/store/storeChange/ProduceIn.xhtml";
    }

}
