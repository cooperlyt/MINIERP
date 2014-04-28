package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 4/28/14.
 */
@Name("stockChangeItemTotal")
public class StockChangeItemTotal extends ErpEntityQuery<StockChangeItem> {

    private static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem  " +
            "where stockChangeItem.stockChange.verify = true";

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "stockChangeItem.stockChange.type in (#{stockChangeTypeCondition.searchTypes})",
            "stockChangeItem.storeRes.res.id = #{storeResCondition.searchResId}",
            "stockChangeItem.storeRes in (#{storeResCondition.matchStoreResIds})"};


    public StockChangeItemTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("stockChangeItem.stockChange.operDate");
    }


    private boolean groupByDay = false;

    public boolean isGroupByDay() {
        return groupByDay;
    }

    public void setGroupByDay(boolean groupByDay) {
        this.groupByDay = groupByDay;
    }
}
