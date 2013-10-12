package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Inventory;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/8/13
 * Time: 2:35 PM
 */
@Name("inventoryList")
@Scope(ScopeType.CONVERSATION)
public class InventoryList extends ErpEntityQuery<Inventory> {

    private static final String EJBQL = "select inventory from Inventory inventory";

    @In(create = true)
    private StoreAreaHome storeAreaHome;

    @In(create = true)
    private StoreResFormatFilter storeResFormatFilter;

    public InventoryList() {
        setEjbql(EJBQL);
        setMaxResults(25);
    }

    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void search() {
        List<String> restrictions = new ArrayList<String>();
        if (storeAreaHome.isIdDefined()){
            restrictions.add("inventory.storeArea.id in (#{storeAreaHome.allStoreAreaIds})");
        }else{
            restrictions.add("inventory.storeArea.store.id = #{store.id}");
        }
        if (storeResFormatFilter.typedFormat()){
            restrictions.add("inventory.storeRes.id in (#{storeResFormatFilter.agreeStoreResIds})");
        }else if (storeResFormatFilter.getRes() != null){
            restrictions.add("inventory.storeRes.res.id = #{storeResFormatFilter.res.id}");
        }
        setRestrictionExpressionStrings(restrictions);
        refresh();
    }

}
