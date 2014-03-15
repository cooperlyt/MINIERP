package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;

import java.util.*;

/**
 * Created by cooper on 3/3/14.
 */
public class StoreChangeResTotal extends ErpEntityQuery<StockChangeItem> {

    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem  where stockChangeItem.stockChange.verify = true";

    public StoreChangeResTotal() {

        setEjbql(EJBQL);
        setRestrictionLogicOperator("and");
        setOrderColumn("stockChangeItem.stockChange.operDate");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public StoreResCountTotalGroup getTotalInResultList() {
        return getTotalResCountGroup(false);
    }

    public StoreResCountTotalGroup getTotalOutResultList() {
        return getTotalResCountGroup(true);
    }

    public StoreResCountTotalGroup getTotalResCountGroup(){
        return new StoreResCountTotalGroup(getResultList());
    }

    private StoreResCountTotalGroup getTotalResCountGroup(boolean out){
        StoreResCountTotalGroup result = new StoreResCountTotalGroup();
        for (StockChangeItem item : getResultList()) {
            if (item.isStoreOut() == out) {
                result.put(item);
            }
        }
        return result;
    }

}
