package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;

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

    public StoreResCountGroup getTotalInResultList() {
        return getTotalResCountGroup(false);
    }

    public StoreResCountGroup getTotalOutResultList() {
        return getTotalResCountGroup(true);
    }

    public StoreResCountGroup getTotalResCountGroup(){
        return new StoreResCountGroup(getResultList());
    }

    private StoreResCountGroup getTotalResCountGroup(boolean out){
        StoreResCountGroup result = new StoreResCountGroup();
        for (StockChangeItem item : getResultList()) {
            if (item.isStoreOut() == out) {
                result.put(item);
            }
        }
        return result;
    }

}
