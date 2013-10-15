package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/8/13
 * Time: 2:35 PM
 */
@Name("inventoryList")
@Scope(ScopeType.CONVERSATION)
public class InventoryList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select inventory from Stock inventory";

    private static final String[] RESTRICTIONS = {
            "inventory.storeArea.id in (#{storeAreaHome.allStoreAreaIds})",
            "inventory.storeArea.store.id = #{inventoryList.store.id}",
            "inventory.storeRes.id in (#{storeResFormatFilter.agreeStoreResIds})",
            "inventory.storeRes.res.id = #{storeResFormatFilter.res.id}"};

    @Logger
    protected Log log;

    @In(create = true)
    private StoreAreaHome storeAreaHome;

    @In(create = true)
    private StoreResFormatFilter storeResFormatFilter;

    public InventoryList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setMaxResults(25);
    }

    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}
