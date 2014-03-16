package com.dgsoft.erp.action;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 05/03/14
 * Time: 09:07
 */
@Name("stockChangeList")
public class StockChangeList extends ErpEntityQuery<StockChange> {

    private static final String EJBQL = "select stockChange from StockChange stockChange where stockChange.verify = true";

    private static final String[] RESTRICTIONS = {
            "stockChange.operDate >=  #{stockChangeList.searchDateArea.dateFrom}",
            "stockChange.operDate <= #{stockChangeList.searchDateArea.searchDateTo}",
            "stockChange.store.id = #{stockChangeList.storeId}",
            "stockChange.operType in (#{stockChangeList.searchTypes})",
    };

    public StockChangeList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("stockChange.operDate");
    }

    @Factory(value = "allStoreChangeTypes", scope = ScopeType.SESSION)
    public StockChange.StoreChangeType[] getAllStoreChangeTypes() {
        return StockChange.StoreChangeType.values();
    }

    private String storeId = "noSelected";

    private Boolean out;

    private StockChange.StoreChangeType storeChangeType;

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public StockChange.StoreChangeType getStoreChangeType() {
        return storeChangeType;
    }

    public void setStoreChangeType(StockChange.StoreChangeType storeChangeType) {
        this.storeChangeType = storeChangeType;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public void setSearchDateArea(SearchDateArea searchDateArea) {
        this.searchDateArea = searchDateArea;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Boolean getOut() {
        return out;
    }

    public void setOut(Boolean out) {
        this.out = out;
    }

    public List<StockChange.StoreChangeType> getSearchTypes() {
        List<StockChange.StoreChangeType> result = new ArrayList<StockChange.StoreChangeType>();
        if (out != null) {
            for (StockChange.StoreChangeType type : EnumSet.allOf(StockChange.StoreChangeType.class)) {
                if (type.isOut() == out) {
                    result.add(type);
                }
            }
        } else {
            if (getStoreChangeType() != null)
                result.add(getStoreChangeType());
        }
        return result;
    }
}
