package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
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

    private Date dateFrom;
    private Date dateTo;

    public Date getSearchDateTo() {
        if (dateTo == null) {
            return null;
        }
        return new Date(dateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public List<StoreResCount> getTotalInResultList() {
        return getTotalResCounts(false);
    }

    public List<StoreResCount> getTotalOutResultList() {
        return getTotalResCounts(true);
    }

    private List<StoreResCount> getTotalResCounts(boolean out) {
        Map<StoreRes, StoreResCount> result = new HashMap<StoreRes, StoreResCount>();
        List<StockChangeItem> changeItems = getResultList();
        for (StockChangeItem item : changeItems) {
            if (item.getStockChange().getOperType().isOut() == out) {
                StoreResCount storeResCount = result.get(item.getStoreRes());
                if (storeResCount == null) {
                    storeResCount = new StoreResCount(item.getStoreRes(), item.getCount());
                    result.put(item.getStoreRes(), storeResCount);
                } else {
                    storeResCount.setMasterCount(storeResCount.getMasterCount().add(item.getCount()));
                }
            }

        }
        List<StoreResCount> resultList = new ArrayList<StoreResCount>(result.values());
        Collections.sort(resultList, new Comparator<StoreResCount>() {
            @Override
            public int compare(StoreResCount o1, StoreResCount o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });
        return resultList;
    }
}
