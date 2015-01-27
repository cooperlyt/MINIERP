package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.api.AllocationOutGroup;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 1/27/15.
 */
@Name("storeAllocationTotal")
public class StoreAllocationTotal extends ErpEntityQuery<AllocationOutGroup> {


    private static final String EJBQL = "select new com.dgsoft.erp.model.api.AllocationOutGroup(item.storeRes.id,item.stockChange.allocation.inStore,sum(item.count)) from StockChangeItem item where item.stockChange.operType = 'ALLOCATION_OUT'";

    private static final String[] RESTRICTIONS = {
            "item.stockChange.operDate > #{storeChangeTotal.dateFrom}",
            "item.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "item.stockChange.allocation.outStore.id = #{stockStoreResList.storeId}",
            "item.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "item.storeRes.res.id = #{storeResCondition.searchResId}",
            "item.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "item.storeRes.id in (#{storeResCondition.matchStoreResIds})"};




    public StoreAllocationTotal() {
        super();
        setEjbql(EJBQL);
        setGroupBy("item.storeRes.id,item.stockChange.allocation.inStore");
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }
}
