package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.MaterialStoreIn;
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
    public void addItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String cancel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isIdAvailable(String newId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String storeChange() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
