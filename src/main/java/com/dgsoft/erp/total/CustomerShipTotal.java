package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StoreResCount;
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
        shipDateFrom = new Date();
        shipDateTo = new Date();
    }


    @org.jboss.seam.annotations.Logger
    private Log log;

    @In(create = true)
    private StoreResList storeResList;

    private Date shipDateFrom;

    private Date shipDateTo;

    private Map<Customer,List<StoreResCount>> resultMap;

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

        if (!storeResList.isAllStoreRes() && !storeResList.isResSearch()) {

            return storeResList.getResultList();
        }else{
            return null;
        }
    }

    public Map<Customer,List<StoreResCount>> getCustomerTotalResultMap() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initResultMap();

        return resultMap;
    }

    public List<Map.Entry<Customer,List<StoreResCount>>> getCustomerTotalResultList(){
        List<Map.Entry<Customer,List<StoreResCount>>> result = new ArrayList<Map.Entry<Customer, List<StoreResCount>>>(getCustomerTotalResultMap().entrySet());
        Collections.sort(result,new Comparator<Map.Entry<Customer, List<StoreResCount>>>() {
            @Override
            public int compare(Map.Entry<Customer, List<StoreResCount>> o1, Map.Entry<Customer, List<StoreResCount>> o2) {
                return o1.getKey().getId().compareTo(o2.getKey().getId());
            }
        });
        return result;
    }

    public Map<StoreRes,StoreResCount> getResTotalResultMap(){
        Map<StoreRes,StoreResCount> result = new HashMap<StoreRes, StoreResCount>();
        for (List<StoreResCount> storeResCounts: getCustomerTotalResultMap().values()){
            for (StoreResCount storeResCount: storeResCounts){
                StoreResCount c = result.get(storeResCount.getStoreRes());

                if (c == null){
                    result.put(storeResCount.getStoreRes(),storeResCount);
                }else{
                    c.add(storeResCount);
                }
            }
        }
        return result;
    }

    public List<StoreResCount> getResTotalResultList(){
        List<StoreResCount> result = new ArrayList<StoreResCount>(getResTotalResultMap().values());
        Collections.sort(result,new Comparator<StoreResCount>() {
            @Override
            public int compare(StoreResCount o1, StoreResCount o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });
        return result;
    }



    private void initResultMap(){
        if (resultMap == null){
            Map<Customer,List<StoreResCount>> result =new HashMap<Customer,List<StoreResCount>>();
            for (DispatchItem item: getResultList()){
                List<StoreResCount> mapValue = result.get(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer());
                if (mapValue == null){
                    mapValue = new ArrayList<StoreResCount>();
                    result.put(item.getDispatch().getNeedRes().getCustomerOrder().getCustomer(),mapValue);
                }

                StoreResCount count = null;
                for (StoreResCount c: mapValue){
                    if (c.getStoreRes().equals(item.getStoreRes())){
                        count = c;
                        break;
                    }
                }
                if (count == null){
                    mapValue.add(new StoreResCount(item.getStoreRes(),item.getResCount().getMasterCount()) );
                }else{
                    count.setMasterCount(count.getMasterCount().add(item.getResCount().getMasterCount()));
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
