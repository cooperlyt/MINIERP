package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.InventoryItem;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooper on 1/26/15.
 */
@Name("inventoryItemList")
public class InventoryItemList extends ErpEntityQuery<InventoryItem> {

    private static final String EJBQL = "select item from InventoryItem item";

    private static final String[] RESTRICTIONS = {
            "item.inventory.id = #{inventoryItemList.inventoryId}",
            "item.stock.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "item.stock.storeRes.res.id = #{storeResCondition.searchResId}",
            "item.stock.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "item.stock.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public InventoryItemList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private String inventoryId;

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }


    public Map<StoreRes,InventoryItem> getStoreResMap(){
        Map<StoreRes,InventoryItem> result = new HashMap<StoreRes, InventoryItem>();
        for(InventoryItem item : getResultList()){
            result.put(item.getStock().getStoreRes(),item);
        }
        return result;
    }
}
