package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.MaterialStoreIn;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:23 AM
 */

@Name("materialStoreInHome")
public class MaterialStoreInHome extends StoreInAction{

    @Override
    protected StockChangeItem getSelectInItem() {
        return null;
    }
}
