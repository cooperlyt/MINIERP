package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by cooper on 2/18/14.
 */
@Name("customerShipTotal")
public class CustomerShipTotal extends ErpEntityQuery<DispatchItem> {

    private static final String EJBQL = "select dispatchItem from DispatchItem dispatchItem  where dispatchItem.dispatch.storeOut = true";

    private static final String[] RESTRICTIONS = {
            "dispatchItem.dispatch.sendTime >= #{customerShipTotal.shipDateFrom}",
            "dispatchItem.dispatch.sendTime <= #{customerShipTotal.searchShipDateTo}",
            "dispatchItem.storeRes.res.id = #{customerShipTotal.searchResId}",
            "dispatchItem.storeRes.floatConversionRate = #{customerShipTotal.searchFloatConvertRate}",
            "dispatchItem.storeRes in (#{customerShipTotal.filterStoreReses})"};


    public CustomerShipTotal() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }


    @org.jboss.seam.annotations.Logger
    private Log log;

    @In(create = true)
    private StoreResList storeResList;

    private Date shipDateFrom;

    private Date shipDateTo;

    private Map<Customer,Map<StoreRes,ResCount>> resultMap;

    public Date getShipDateFrom() {
        return shipDateFrom;
    }

    public void setShipDateFrom(Date shipDateFrom) {
        this.shipDateFrom = shipDateFrom;
    }

    public Date getShipDateTo() {
        return shipDateTo;
    }

    public void setShipDateTo(Date shipDateTo) {
        this.shipDateTo = shipDateTo;
    }

    public Date getSearchShipDateTo() {
        if (shipDateTo == null) {
            return null;
        }
        return new Date(shipDateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public String getSearchResId() {
        if (storeResList.isResSearch()) {
            return storeResList.getSearchResId();
        } else {

            return null;
        }
    }

    public BigDecimal getSearchFloatConvertRate() {
        if (storeResList.isResSearch()) {
            return storeResList.getSearchFloatConvertRate();
        } else {
            return null;
        }
    }

    public List<StoreRes> getFilterStoreReses(){
        log.debug("getFilterStoreReses:" + storeResList.isAllStoreRes());

        if (!storeResList.isAllStoreRes() && !storeResList.isResSearch()) {

            return storeResList.getResultList();
        }else{
            return null;
        }
    }

    public Map<Customer,Map<StoreRes,ResCount>> getResultMap() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initResultMap();

        log.debug("getResultMap:" + resultMap.entrySet().size());
        return resultMap;
    }

    public Map<StoreRes,ResCount> getTotalResultMap(){
        Map<StoreRes,ResCount> result = new HashMap<StoreRes,ResCount>();
        for (Map.Entry<Customer,Map<StoreRes,ResCount>> entry: getResultMap().entrySet()){
            for (Map.Entry<StoreRes,ResCount> resEntry: entry.getValue().entrySet()){
                ResCount resCount = result.get(resEntry.getKey());
                if (resCount == null){
                    result.put(resEntry.getKey(),resEntry.getValue());
                }else{
                    resCount.add(resEntry.getValue());
                }
            }
        }
        return result;
    }



    private void initResultMap(){
        if (resultMap == null){
            Map<Customer,Map<StoreRes,ResCount>> result =new HashMap<Customer,Map<StoreRes,ResCount>>();
            for (DispatchItem item: getResultList()){
                Map<StoreRes,ResCount> mapValue = result.get(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer());
                if (mapValue == null){
                    mapValue = new HashMap<StoreRes,ResCount>();
                    result.put(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer(),mapValue);
                }

                ResCount count =  mapValue.get(item.getStoreRes());
                if (count == null){
                    mapValue.put(item.getStoreRes(),item.getResCount());
                }else{
                    count.add(item.getResCount());
                }
            }
            resultMap = result;
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        resultMap = null;
    }

    public String showReport(){
        return "/report/SaleCustomerShip.xhtml";
    }
}
