package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.action.CarsHome;
import com.dgsoft.erp.action.ExpressDriverHome;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.TransCorpHome;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.DeliveryType;
import com.dgsoft.erp.model.api.FarePayType;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.international.StatusMessage;
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

    @Out(required = false,value = "dispatchStoreIds", scope = ScopeType.CONVERSATION)
    private String[] dispatchStoreIds;

    @In
    private ActionExecuteState actionExecuteState;

    @In(create = true)
    private ExpressDriverHome expressDriverHome;

    @In(create = true)
    private TransCorpHome transCorpHome;

    @In(create = true)
    private CarsHome carsHome;

    @In(create = true)
    private NeedResHome needResHome;

    public static class ResOrderItem {

        private OrderItem orderItem;

        private List<Stock> stockList;

    }

    private List<OrderItem> storeResOrderItems;

    private List<ResOrderItem> resOrderItemList;

    private List<Dispatch> dispatchList;



    //--------------------

    private Store store;

    //private BigDecimal count;
    private StoreResCountInupt storeResCountInupt;

    private ResUnit unit;

    private String orderItemId;

    private String memo;

    private DeliveryType deliveryType;

    private FarePayType farePayType;

    private boolean dispatchStoreExists;

    private OrderItem selectOrderItem;

    //----------------------

    private String storeId;

    private Dispatch selectDispatch;


    //----------------------------

    private String selectOrderItemId;

    //private NeedRes needRes;

    public void clearDispatch() {
        store = null;
        //count = BigDecimal.ZERO;
        orderItemId = null;
        memo = null;
        deliveryType = needResHome.getInstance().getDeliveryType();
        farePayType = needResHome.getInstance().getFarePayType();
        dispatchStoreExists = false;

        expressDriverHome.clearInstance();
        transCorpHome.clearInstance();
        carsHome.clearInstance();
        selectDispatch = null;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Dispatch getSelectDispatch() {
        return selectDispatch;
    }

    public void setSelectDispatch(Dispatch selectDispatch) {
        this.selectDispatch = selectDispatch;
    }

    public OrderItem getSelectOrderItem() {
        return selectOrderItem;
    }

    public void setSelectOrderItem(OrderItem selectOrderItem) {
        this.selectOrderItem = selectOrderItem;
    }

    public StoreResCountInupt getStoreResCountInupt() {
        return storeResCountInupt;
    }

    public void setStoreResCountInupt(StoreResCountInupt storeResCountInupt) {
        this.storeResCountInupt = storeResCountInupt;
    }

    public ResUnit getUnit() {
        return unit;
    }

    public void setUnit(ResUnit unit) {
        this.unit = unit;
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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void beginEditDispatchInfo(){
        for (Dispatch dispatch: dispatchList){
            if (dispatch.getStore().getId().equals(storeId)){
                selectDispatch = dispatch;
                deliveryType = selectDispatch.getDeliveryType();
                farePayType = selectDispatch.getFarePayType();
                memo = selectDispatch.getMemo();

                expressDriverHome.clearInstance();
                carsHome.clearInstance();
                transCorpHome.clearInstance();

                switch (deliveryType){
                    case SEND_TO_DOOR:
                        carsHome.setInstance(selectDispatch.getProductToDoor().getCars());
                        break;
                    case FULL_CAR_SEND:
                         expressDriverHome.setInstance(selectDispatch.getExpressCar().getExpressDriver());
                        break;
                    case EXPRESS_SEND:
                        transCorpHome.setInstance(selectDispatch.getExpressInfo().getTransCorp());
                        break;
                }

                store = dispatch.getStore();
                break;
            }
        }
        selectOrderItem = null;
        actionExecuteState.clearState();
    }

    public void beginDispatchItem() {



        for (OrderItem oi : storeResOrderItems) {
            if (oi.getId().equals(orderItemId)) {
                selectOrderItem = oi;
                break;
            }
        }


        storeResCountInupt = new StoreResCountInupt(selectOrderItem.getStoreRes().getRes(),
                selectOrderItem.getStoreRes().getRes().getResUnitByOutDefault());

        if (selectOrderItem.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            storeResCountInupt.setFloatConvertRate(selectOrderItem.getStoreRes().getFloatConversionRate());
        }

        actionExecuteState.clearState();
        clearDispatch();
    }

    public void beginDispatchAll() {
        clearDispatch();
        selectOrderItem = null;
        actionExecuteState.clearState();
    }

    public void dispatchAllStoreRes() {

        if (selectDispatch != null){
            setSendInfo(selectDispatch);
            selectDispatch.setDeliveryType(deliveryType);
            selectDispatch.setFarePayType(deliveryType.isHaveFare()? farePayType: null);
            selectDispatch = null;
            return;
        }


        if ((selectOrderItem != null) && (storeResCountInupt.getMasterCount().compareTo(BigDecimal.ZERO) <=0) ){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_item_count_less_zero");
            actionExecuteState.setState("fail");
            return;
        }


        Dispatch dispatch = getStoreDispatch();

        if (dispatch == null) {
            dispatch = new Dispatch(needResHome.getInstance(), store, deliveryType,
                    deliveryType.isHaveFare() ? farePayType : null, memo, Dispatch.DispatchState.DISPATCH_COMPLETE);
            setSendInfo(dispatch);

            dispatchList.add(dispatch);
        }


        if (selectOrderItem == null) {
            for (OrderItem oi : storeResOrderItems) {
                dispatchItem(dispatch, oi, oi.getStoreResCount());
            }
            storeResOrderItems.clear();
        } else {

            if (storeResCountInupt.getMasterCount().compareTo(selectOrderItem.getStoreResCount().getMasterCount()) > 0) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "dispatch_item_count_over",
                        storeResCountInupt.getMasterDisplayCount(),
                        selectOrderItem.getStoreResCount().getMasterDisplayCount());
                storeResCountInupt.setCount(selectOrderItem.getStoreResCount().getCountByResUnit(storeResCountInupt.getUseUnit()));
            }

            dispatchItem(dispatch, selectOrderItem, storeResCountInupt);


            selectOrderItem.getStoreResCount().subtract(storeResCountInupt);
            if (selectOrderItem.getStoreResCount().getMasterCount().compareTo(BigDecimal.ZERO) <= 0) {
                storeResOrderItems.remove(selectOrderItem);
            }
            selectOrderItem = null;
        }

        clearDispatch();
        actionExecuteState.actionExecute();

    }

    private void setSendInfo(Dispatch dispatch) {
        switch (deliveryType){
            case FULL_CAR_SEND:
                dispatch.setExpressCar(new ExpressCar(dispatch,expressDriverHome.getReadyInstance()));
                dispatch.setExpressInfo(null);
                dispatch.setProductToDoor(null);
                break;
            case EXPRESS_SEND:
                dispatch.setExpressInfo(new ExpressInfo(dispatch,transCorpHome.getReadyInstance()));
                dispatch.setProductToDoor(null);
                dispatch.setExpressCar(null);
                break;
            case SEND_TO_DOOR:
                dispatch.setProductToDoor(new ProductToDoor(dispatch,carsHome.getReadyInstance()));
                dispatch.setExpressCar(null);
                dispatch.setExpressInfo(null);
                break;
        }
    }

    private void dispatchItem(Dispatch dispatch, OrderItem oi, ResCount resCount) {
        DispatchItem dispatchItem = null;
        for (DispatchItem di : dispatch.getDispatchItems()) {
            if (di.getStoreRes().getId().equals(oi.getStoreRes().getId()) && di.getResCount().canMerger(oi.getStoreResCount())) {
                dispatchItem = di;
                break;
            }
        }


        if (dispatchItem == null) {


            if (oi.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                dispatch.getDispatchItems().add(new DispatchItem(
                        resCount.getSingleNoConverCount().getResUnit(),
                        resCount.getSingleNoConverCount().getCount(),
                        dispatch, oi.getStoreRes()));
            } else {
                dispatch.getDispatchItems().add(new DispatchItem(
                        oi.getStoreRes().getRes().getUnitGroup().getMasterUnit(),
                        resCount.getMasterCount(), dispatch, oi.getStoreRes()));
            }


        } else {
            dispatchItem.addResCount(resCount);
        }
    }

    private Dispatch getStoreDispatch() {
        for (Dispatch dispatch : dispatchList) {
            if (dispatch.getStore().getId().equals(store.getId())) {
                return dispatch;
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
                //needRes = nr;
                needResHome.setId(nr.getId());
                break;
            }
        }

        storeResOrderItems = new ArrayList<OrderItem>();
        for (OrderItem oi : needResHome.getInstance().getOrderItems()) {

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


    @Override
    protected String completeOrderTask(){
        needResHome.getInstance().getDispatches().addAll(dispatchList);
        needResHome.getInstance().setDispatched(true);

        needResHome.update();

        dispatchStoreIds = new String[dispatchList.size()];
        for (int i = 0; i< dispatchList.size(); i++){
            dispatchStoreIds[i] = dispatchList.get(i).getStore().getId();
        }

        return super.completeOrderTask();
    }

}
