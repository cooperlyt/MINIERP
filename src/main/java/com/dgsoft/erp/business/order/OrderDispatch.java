package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.DeliveryType;
import com.dgsoft.erp.model.api.FarePayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/9/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("orderDispatch")
public class OrderDispatch extends OrderTaskHandle {

    @In
    private ActionExecuteState actionExecuteState;

    public static class ResOrderItem {

        private OrderItem orderItem;

        private List<Stock> stockList;

    }

    private List<OrderItem> storeResOrderItems;

    private List<ResOrderItem> resOrderItemList;

    private List<Dispatch> dispatchList;

    //--------------------

    private Store store;

    private BigDecimal count;

    private String orderItemId;

    private String memo;

    private DeliveryType deliveryType;

    private FarePayType farePayType;

    private boolean dispatchStoreExists;

    //----------------------

    private String selectOrderItemId;

    private NeedRes needRes;

    public void clearDispatch() {
        store = null;
        count = BigDecimal.ZERO;
        orderItemId = null;
        memo = null;
        deliveryType = needRes.getDeliveryType();
        farePayType = needRes.getFarePayType();
        dispatchStoreExists = false;
    }


    public boolean isDispatchStoreExists() {
        return dispatchStoreExists;
    }

    public void setDispatchStoreExists(boolean dispatchStoreExists) {
        this.dispatchStoreExists = dispatchStoreExists;
    }

    public FarePayType getFarePayType() {
        return farePayType;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getMemo() {
        return memo;
    }

    public List<ResOrderItem> getResOrderItemList() {
        return resOrderItemList;
    }

    public void setResOrderItemList(List<ResOrderItem> resOrderItemList) {
        this.resOrderItemList = resOrderItemList;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public void setFarePayType(FarePayType farePayType) {
        this.farePayType = farePayType;
    }

    public List<OrderItem> getStoreResOrderItems() {
        return storeResOrderItems;
    }

    public void setStoreResOrderItems(List<OrderItem> storeResOrderItems) {
        this.storeResOrderItems = storeResOrderItems;
    }

    public List<Dispatch> getDispatchList() {
        return dispatchList;
    }

    public void setDispatchList(List<Dispatch> dispatchList) {
        this.dispatchList = dispatchList;
    }

    public String getSelectOrderItemId() {
        return selectOrderItemId;
    }

    public void setSelectOrderItemId(String selectOrderItemId) {
        this.selectOrderItemId = selectOrderItemId;
    }

    public NeedRes getNeedRes() {
        return needRes;
    }

    public void setNeedRes(NeedRes needRes) {
        this.needRes = needRes;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void dispatchAllStoreRes() {
        if (!dispatchList.isEmpty())
            throw new IllegalStateException("dispatch must be empty");

        Dispatch dispatch = getStoreDispatch();

        if (dispatch == null){
            dispatch = new Dispatch(needRes,store,deliveryType,farePayType,memo, Dispatch.DispatchState.DISPATCH_COMPLETE);
            dispatchList.add(dispatch);
        }

        for (OrderItem oi : storeResOrderItems) {
            DispatchItem dispatchItem = null;
            for (DispatchItem di: dispatch.getDispatchItems()){
                if (di.getStoreRes().getId().equals(oi.getStoreRes().getId()) && di.getResCount().canMerger(oi.getStoreResCount())){
                    dispatchItem = di;
                    break;
                }
            }
            if (dispatchItem == null){
                if (oi.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                    dispatch.getDispatchItems().add(new DispatchItem(
                            oi.getStoreResCount().getSingleNoConverCount().getResUnit(),
                            oi.getStoreResCount().getSingleNoConverCount().getCount(),
                            dispatch, oi.getStoreRes()));
                } else {
                    dispatch.getDispatchItems().add(new DispatchItem(
                            oi.getStoreRes().getRes().getUnitGroup().getMasterUnit(),
                            oi.getStoreResCount().getMasterCount(), dispatch, oi.getStoreRes()));
                }
            }else{
                dispatchItem.addResCount(oi.getStoreResCount());
            }


        }


        storeResOrderItems.clear();
        actionExecuteState.actionExecute();
    }

    private Dispatch getStoreDispatch(){
        for(Dispatch dispatch: dispatchList){
            if (dispatch.getStore().getId().equals(store.getId())){
                return  dispatch;
            }
        }
        return null;
    }

    public void storeSelectListener() {
        dispatchStoreExists = (getStoreDispatch() != null);
    }



    public void dispatchStoreRes() {

        Dispatch dispatch = null;
        for (Dispatch d : dispatchList) {
            if (d.getStore().getId().equals(store.getId())) {
                dispatch = d;
                break;
            }
        }

        if (dispatch == null) {
            dispatch = new Dispatch();
        }

        actionExecuteState.actionExecute();
    }

    @Override
    protected String initOrderTask() {

        dispatchList = new ArrayList<Dispatch>();
        for (NeedRes nr : orderHome.getInstance().getNeedReses()) {
            if (nr.getDispatches().isEmpty()) {
                needRes = nr;
                break;
            }
        }

        storeResOrderItems = new ArrayList<OrderItem>();
        for (OrderItem oi : needRes.getOrderItems()) {

            if (oi.isStoreResItem()) {
                oi.generateResCount();
                boolean added = false;
                for (OrderItem doi : storeResOrderItems) {
                    if (doi.getStoreRes().getId().equals(oi.getStoreRes().getId())) {
                        doi.addCount(oi);
                        added = true;
                        break;
                    }
                }
                if (!added)
                    storeResOrderItems.add(oi);
            }
        }


        dispatchList = new ArrayList<Dispatch>();

        clearDispatch();

        return "success";
    }


}
