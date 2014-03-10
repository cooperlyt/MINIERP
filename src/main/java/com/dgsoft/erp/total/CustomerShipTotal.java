package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

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


    public Map<Customer,StoreResCountGroup> getCustomerTotalResultMap() {

        Map<Customer,StoreResCountGroup> result =new HashMap<Customer,StoreResCountGroup>();
        for (StockChangeItem item: getResultList()){
            Customer customer = item.getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
            StoreResCountGroup mapValue = result.get(customer);
            if (mapValue == null){
                mapValue = new StoreResCountGroup();
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
