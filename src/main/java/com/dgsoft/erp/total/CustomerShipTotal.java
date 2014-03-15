package com.dgsoft.erp.total;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;

/**
 * Created by cooper on 2/18/14.
 */
@Name("customerShipTotal")
@Scope(ScopeType.CONVERSATION)
public class CustomerShipTotal extends CustomerResSellTotal {

    //private static final String EJBQL = "select dispatchItem from DispatchItem dispatchItem  where dispatchItem.dispatch.storeOut = true";


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{customerShipTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{customerShipTotal.searchDateArea.searchDateTo}",
            "stockChangeItem.storeRes.res.id = #{storeResList.resultSearchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResList.resultSearchFloatConvertRate}",
            "stockChangeItem.storeRes in (#{storeResList.filterResultList})"};

    public CustomerShipTotal() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


    //private Map<Customer,List<StoreResCount>> resultMap;


    public String showReport(){
        return "/report/SaleCustomerShip.xhtml";
    }
}
