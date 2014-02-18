package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.DispatchItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 2/18/14.
 */
@Name("customerShipTotal")
public class CustomerShipTotal extends ErpEntityQuery<DispatchItem> {

    private static final String EJBQL = "select dispatchItem from DispatchItem dispatchItem left join fetch dispatchItem.dispatch.needRes.customerOrder.customer where dispatchItem.dispatch.storeOut = true";

    private static final String[] RESTRICTIONS = {
            "dispatchItem.sendTime >= #{customerShipTotal.shipDateFrom}",
            "dispatchItem.sendTime <= #{customerShipTotal.searchShipDateTo}",
            "dispatchItem.storeRes.res.id = #{customerShipTotal.searchResId}}",
            "dispatchItem.storeRes.floatConversionRate = #{customerShipTotal.searchFloatConvertRate}}",
            "dispatchItem.storeRes in (#{customerShipTotal.filterStoreReses}})"};


    public CustomerShipTotal() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }


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
        if (!storeResList.isResSearch()) {
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

        return resultMap;
    }

    private void initResultMap(){
        if (resultMap == null){
            resultMap =new HashMap<Customer,Map<StoreRes,ResCount>>();
            for (DispatchItem item: getResultList()){
                Map<StoreRes,ResCount> mapValue = resultMap.get(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer());
                if (mapValue == null){
                    mapValue = new HashMap<StoreRes,ResCount>();
                    resultMap.put(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer(),mapValue);
                }

                ResCount count =  mapValue.get(item.getStoreRes());
                if (count == null){
                    mapValue.put(item.getStoreRes(),item.getResCount());
                }else{
                    count.add(item.getResCount());
                }
            }
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        resultMap = null;
    }
}
