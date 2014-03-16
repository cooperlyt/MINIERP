package com.dgsoft.erp.total;

import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;

/**
 * Created by cooper on 2/18/14.
 */
@Name("customerShipTotal")
@Scope(ScopeType.CONVERSATION)
public class CustomerShipTotal extends CustomerStockChangeTotal {

    //private static final String EJBQL = "select dispatchItem from DispatchItem dispatchItem  where dispatchItem.dispatch.storeOut = true";


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{customerShipTotal.changeType}",
            "stockChangeItem.stockChange.operDate >= #{customerShipTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{customerShipTotal.searchDateArea.searchDateTo}",
            "lower(stockChangeItem.stockChange.orderDispatch.needRes.customerOrder.customer.name) like lower(concat(#{customerShipTotal.sellCustomerName},'%'))",
            "lower(stockChangeItem.stockChange.productBackStoreIn.orderBack.customer.name) like lower(concat(#{customerShipTotal.backCustomerName},'%'))",
            "stockChangeItem.stockChange.orderDispatch.needRes.customerOrder.customer.customerArea.id = #{customerShipTotal.sellSellAreaId}",
            "stockChangeItem.stockChange.productBackStoreIn.orderBack.customer.customerArea.id = #{customerShipTotal.backSellAreaId}",
            "stockChangeItem.storeRes.res.id = #{storeResList.resultSearchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResList.resultSearchFloatConvertRate}",
            "stockChangeItem.storeRes in (#{storeResList.filterResultList})"};

    public CustomerShipTotal() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    public StockChange.StoreChangeType getChangeType(){
       if (sellOutTotal){
           return StockChange.StoreChangeType.SELL_OUT;
       }else{
           return StockChange.StoreChangeType.SELL_BACK;
       }
    }

    public String getSellSellAreaId(){
        if (sellOutTotal){
            return sellAreaId;
        }else
            return null;
    }

    public String getBackSellAreaId(){
        if (!sellOutTotal){
            return sellAreaId;
        }else
            return null;
    }

    public String getSellCustomerName(){
        if (sellOutTotal){
            return customerName;
        }else
            return null;
    }

    public String getBackCustomerName(){
        if (!sellOutTotal){
            return customerName;
        }else
            return null;
    }

    private boolean sellOutTotal = true;

    private String customerName;

    private String sellAreaId;

    public String getSellAreaId() {
        return sellAreaId;
    }

    public void setSellAreaId(String sellAreaId) {
        this.sellAreaId = sellAreaId;
    }

    public boolean isSellOutTotal() {
        return sellOutTotal;
    }

    public void setSellOutTotal(boolean sellOutTotal) {
        this.sellOutTotal = sellOutTotal;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

//private Map<Customer,List<StoreResCount>> resultMap;


    public String showReport(){
        return "/report/SaleCustomerShip.xhtml";
    }
}
