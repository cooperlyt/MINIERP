package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.MaterialStoreIn;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:23 AM
 */

@Name("materialStoreInHome")
public class MaterialStoreInHome extends StoreInAction<MaterialStoreIn>{

    @Override
    protected String beginStoreIn() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String storeIn() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String cancel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
