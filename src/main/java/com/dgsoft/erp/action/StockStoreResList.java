package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooper on 1/26/15.
 */
@Name("stockStoreResList")
public class StockStoreResList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select stock from Stock stock";

    private static final String[] RESTRICTIONS = {
            "stock.store.id = #{stockStoreResList.storeId}",
            "stock.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stock.storeRes.res.id = #{storeResCondition.searchResId}",
            "stock.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stock.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public StockStoreResList() {
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

    public Map<StoreRes,Stock> getStoreResMap(){
        Map<StoreRes,Stock> result = new HashMap<StoreRes, Stock>();
        for(Stock stock:  getResultList()){
            result.put(stock.getStoreRes(),stock);
        }
        return result;
    }
}
