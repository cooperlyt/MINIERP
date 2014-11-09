package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Inventory;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cooper on 11/9/14.
 */
@Name("inventoryList")
public class InventoryList extends ErpEntityQuery<Inventory>{


    private static final String EJBQL = "select inventory from Inventory inventory";

    private static final String[] RESTRICTIONS = {
            "inventory.checkedDate >= #{searchDateArea.dateFrom}",
            "inventory.checkedDate <= #{searchDateArea.searchDateTo}",
            "inventory.stockChanged = #{inventoryList.stockChanged}",
            "inventory.store.id in (#{inventoryList.searchStoreIds})",
            "inventory.type = #{inventoryList.type}"};


    private String storeId;

    private Boolean stockChanged;

    private Inventory.InventoryType type;

    public InventoryList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Boolean getStockChanged() {
        return stockChanged;
    }

    public void setStockChanged(Boolean stockChanged) {
        this.stockChanged = stockChanged;
    }

    public Inventory.InventoryType getType() {
        return type;
    }

    public void setType(Inventory.InventoryType type) {
        this.type = type;
    }

    @In
    private Identity identity;

    public List<String> getSearchStoreIds(){
        if (storeId == null){
            List<String> result = new ArrayList<String>();

            for (Store store : getEntityManager().createQuery("select store from Store store where store.enable = true", Store.class).getResultList()) {
                if (identity.hasRole("erp.sale.manager") ||
                        identity.hasRole("erp.finance.accountancy") || identity.hasRole(store.getRole())) {
                    result.add(store.getId());
                }
            }
            return result;
        }else{
            List<String> result = new ArrayList<String>(1);
            result.add(storeId);
            return result;
        }
    }

}
