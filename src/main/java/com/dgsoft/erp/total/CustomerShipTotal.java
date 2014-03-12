package com.dgsoft.erp.total;

import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;

/**
 * Created by cooper on 2/18/14.
 */
@Name("customerShipTotal")
@Scope(ScopeType.CONVERSATION)
public class CustomerShipTotal extends StoreChangeResTotal {

    //private static final String EJBQL = "select dispatchItem from DispatchItem dispatchItem  where dispatchItem.dispatch.storeOut = true";

    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem left join fetch stockChangeItem.stockChange sc left join fetch sc.orderDispatch od left join fetch od.needRes nr left join fetch nr.customerOrder co left join fetch co.customer customer where stockChangeItem.stockChange.verify = true";



    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{customerShipTotal.changeType}",
            "stockChangeItem.stockChange.operDate >= #{customerShipTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{customerShipTotal.searchDateArea.searchDateTo}",
            "stockChangeItem.storeRes.res.id = #{storeResList.resultSearchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResList.resultSearchFloatConvertRate}",
            "stockChangeItem.storeRes in (#{storeResList.filterResultList})"};

    public CustomerShipTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


    public StockChange.StoreChangeType getChangeType(){
        return StockChange.StoreChangeType.SELL_OUT;
    }

    //private Map<Customer,List<StoreResCount>> resultMap;


    public Map<Customer,StoreResCountTotalGroup> getCustomerTotalResultMap() {

        Map<Customer,StoreResCountTotalGroup> result =new HashMap<Customer,StoreResCountTotalGroup>();
        for (StockChangeItem item: getResultList()){
            Customer customer = item.getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
            StoreResCountTotalGroup mapValue = result.get(customer);
            if (mapValue == null){
                mapValue = new StoreResCountTotalGroup();
                result.put(customer,mapValue);
            }
            mapValue.put(item);
        }

        return result;
    }

    public String showReport(){
        return "/report/SaleCustomerShip.xhtml";
    }
}
