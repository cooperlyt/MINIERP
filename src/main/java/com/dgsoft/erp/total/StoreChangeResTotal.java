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

    public StoreResCountGroup<StoreResCountEntity> getTotalInResultList() {
        return getTotalResCountGroup(false);
    }

    public StoreResCountGroup<StoreResCountEntity> getTotalOutResultList() {
        return getTotalResCountGroup(true);
    }

    public StoreResCountGroup<StoreResCountEntity> getTotalResCountGroup(){
        return new StoreResCountGroup<StoreResCountEntity>(getResultList());
    }

    private StoreResCountGroup<StoreResCountEntity> getTotalResCountGroup(boolean out){
        StoreResCountGroup<StoreResCountEntity> result = new StoreResCountGroup<StoreResCountEntity>();
        for (StockChangeItem item : getResultList()) {
            if (item.isStoreOut() == out) {
                result.put(item);
            }
        }
        return result;
    }

}
