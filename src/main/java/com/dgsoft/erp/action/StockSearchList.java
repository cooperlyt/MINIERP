package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Stock;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;

/**
 * Created by cooper on 5/13/14.
 */
@Name("stockSearchList")
@Scope(ScopeType.CONVERSATION)
public class StockSearchList extends ErpEntityQuery<Stock>{

    private static final String EJBQL = "select stock from Stock stock";

    private static final String[] RESTRICTIONS = {
            "stock.store.id =  #{stockSearchList.storeId}",
            "stock.storeRes.code = #{storeResCondition.storeResCode}",
            "stock.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stock.storeRes.res.id = #{storeResCondition.searchResId}",
            "stock.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stock.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public StockSearchList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    private String storeId;


    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
