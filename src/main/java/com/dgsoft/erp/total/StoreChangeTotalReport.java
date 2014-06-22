package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 6/22/14.
 */
@Name("storeChangeTotalReport")
public class StoreChangeTotalReport extends ErpEntityQuery<StockChangeItem> {


    private static final String EJBQL = "select item from StockChangeItem item  left join fetch item.storeRes storeRes "  +
            " left join fetch storeRes.res res left join fetch res.unitGroup unitGroup " +
            " left join fetch item.stockChange storeChange left join fetch storeChange.allocationForStoreOuts where item.stockChange.verify = true";


    private static final String[] RESTRICTIONS = {
            "item.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "item.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "item.stockChange.store.id = #{storeChangeTotalReport.storeId}",
            "item.storeRes.res.resCategory.id in (#{storeResFilter.resCategoryIds})",
            "item.storeRes.res.id = #{storeResFilter.resId}",
            "item.storeRes.floatConversionRate = #{storeResFilter.floatConvertRate}",
            "item.storeRes.id in (#{storeResFilter.storeResIds})"};


    public StoreChangeTotalReport() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
