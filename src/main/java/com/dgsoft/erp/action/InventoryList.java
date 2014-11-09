package com.dgsoft.erp.action;

import com.dgsoft.common.SearchDateArea;
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


    private static final String EJBQL = "select inventory from Inventory inventory left join fetch inventory.store";

    private static final String[] RESTRICTIONS = {
            "inventory.checkDate >= #{inventoryList.searchDateArea.dateFrom}",
            "inventory.checkDate <= #{inventoryList.searchDateArea.searchDateTo}",
            "inventory.status = #{inventoryList.status}",
            "inventory.store.id in (#{inventoryList.searchStoreIds})",
            "inventory.type = #{inventoryList.type}"};


    private String storeId;

    private Inventory.InvertoryStatus status;

    private Inventory.InventoryType type;

    private SearchDateArea searchDateArea = new SearchDateArea(null,null);

    public InventoryList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("inventory.applyDate");
        setOrderDirection("desc");
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public void setSearchDateArea(SearchDateArea searchDateArea) {
        this.searchDateArea = searchDateArea;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Inventory.InvertoryStatus getStatus() {
        return status;
    }

    public void setStatus(Inventory.InvertoryStatus status) {
        this.status = status;
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
                if (identity.hasRole("erp.sale.manager") || identity.hasRole("erp.storage.manager") ||
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
