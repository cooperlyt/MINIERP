package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.action.CarsHome;
import com.dgsoft.erp.action.ExpressCarHome;
import com.dgsoft.erp.action.TransCorpHome;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/2/14
 * Time: 2:52 PM
 */
@Name("orderDispatch")
@Scope(ScopeType.CONVERSATION)
public class OrderDispatch {


    @In
    private ActionExecuteState actionExecuteState;

    @In(create = true)
    private TransCorpHome transCorpHome;

    @In(create = true)
    private ExpressCarHome expressCarHome;

    @In(create = true)
    private CarsHome carsHome;

    private boolean shipDetails;

    @In
    protected FacesMessages facesMessages;

    public static class ResOrderItem {

        private OrderItem orderItem;

        private List<Stock> stockList;

    }

    @DataModel("dispatchOrderItems")
    private List<OrderItem> storeResOrderItems;

    @DataModelSelection
    private OrderItem selectedOrderItem;

    private List<ResOrderItem> resOrderItemList;

    private List<Dispatch> dispatchList;


    //--------------------

    private Store store;

    //private BigDecimal count;
    private StoreResCountInupt storeResCountInupt;

    private ResUnit unit;

    private String orderItemId;

    private String memo;

    private Dispatch.DeliveryType deliveryType;

    private boolean dispatchStoreExists;

    private OrderItem selectOrderItem;

    //----------------------

    private String storeId;

    private Dispatch selectDispatch;


    //----------------------------

    //private NeedRes needRes;

    public void clearDispatch() {
        store = null;
        //count = BigDecimal.ZERO;
        orderItemId = null;
        memo = null;

        dispatchStoreExists = false;

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

    public Dispatch.DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Dispatch.DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public boolean isShipDetails() {
        return shipDetails;
    }

    public void setShipDetails(boolean shipDetails) {
        this.shipDetails = shipDetails;
    }

//    public List<OrderItem> getStoreResOrderItems() {
//        return storeResOrderItems;
//    }
//
//    public void setStoreResOrderItems(List<OrderItem> storeResOrderItems) {
//        this.storeResOrderItems = storeResOrderItems;
//    }

    public List<Dispatch> getDispatchList() {
        return dispatchList;
    }

    public void setDispatchList(List<Dispatch> dispatchList) {
        this.dispatchList = dispatchList;
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

    public void beginEditDispatchInfo() {
        for (Dispatch dispatch : dispatchList) {
            if (dispatch.getStore().getId().equals(storeId)) {
                selectDispatch = dispatch;
                deliveryType = selectDispatch.getDeliveryType();
                memo = selectDispatch.getMemo();


                carsHome.clearInstance();
                transCorpHome.clearInstance();

                switch (deliveryType) {
                    case SEND_TO_DOOR:
                        if (selectDispatch.getProductToDoor() != null)
                            carsHome.setId(selectDispatch.getProductToDoor().getCars().getId());
                        break;
                    case FULL_CAR_SEND:
                        if (selectDispatch.getExpressCar() != null) {
                            expressCarHome.setInstance(selectDispatch.getExpressCar());
                            transCorpHome.setInstance(selectDispatch.getExpressCar().getTransCorp());
                        }
                        break;
                    case EXPRESS_SEND:
                        if (selectDispatch.getExpressInfo() != null)
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


//        for (OrderItem oi : storeResOrderItems) {
//            if (oi.getId().equals(orderItemId)) {
//                selectOrderItem = oi;
//                break;
//            }
//        }
        selectOrderItem = selectedOrderItem;

        storeResCountInupt = new StoreResCountInupt(selectOrderItem.getStoreRes().getRes(),
                selectOrderItem.getStoreRes().getRes().getResUnitByOutDefault());

        if (selectOrderItem.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            storeResCountInupt.setFloatConvertRate(selectOrderItem.getStoreRes().getFloatConversionRate());
        }

        actionExecuteState.clearState();
        clearDispatch();
        shipDetails = false;
    }

    public void beginDispatchAll() {
        clearDispatch();
        shipDetails = false;
        selectOrderItem = null;
        actionExecuteState.clearState();
    }

    public void dispatchAllStoreRes() {

        if (selectDispatch != null) {

            setSendInfo(selectDispatch);

            selectDispatch.setDeliveryType(deliveryType);
            selectDispatch = null;
            actionExecuteState.actionExecute();
            return;
        }


        if ((selectOrderItem != null) && (storeResCountInupt.getMasterCount().compareTo(BigDecimal.ZERO) <= 0)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_item_count_less_zero");
            actionExecuteState.setState("fail");
            return;
        }


        Dispatch dispatch = getStoreDispatch();

        if (dispatch == null) {
            dispatch = new Dispatch(needRes, store, deliveryType,
                    memo, Dispatch.DispatchState.DISPATCH_COMPLETE);

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

        if (shipDetails) {


            switch (deliveryType) {
                case FULL_CAR_SEND:
                    dispatch.setExpressCar(expressCarHome.getReadyInstance());
                    //dispatch.setExpressCar(new ExpressCar(dispatch, expressDriverHome.getReadyInstance()));
                    dispatch.setExpressInfo(null);
                    dispatch.setProductToDoor(null);
                    break;
                case EXPRESS_SEND:
                    dispatch.setExpressInfo(new ExpressInfo(dispatch, transCorpHome.getReadyInstance()));
                    dispatch.setProductToDoor(null);
                    dispatch.setExpressCar(null);
                    break;
                case SEND_TO_DOOR:

                    dispatch.setProductToDoor(new ProductToDoor(dispatch, carsHome.getInstance()));
                    dispatch.setExpressCar(null);
                    dispatch.setExpressInfo(null);
                    break;
            }
        } else {
            dispatch.setExpressCar(null);
            dispatch.setExpressInfo(null);
            dispatch.setProductToDoor(null);
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


    private NeedRes needRes;

    public void init(NeedRes needRes) {

        this.needRes = needRes;

        storeResOrderItems = new ArrayList<OrderItem>();
        for (OrderItem oi : needRes.getOrderItems()) {


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

        dispatchList = new ArrayList<Dispatch>();
        clearDispatch();
    }

    public void reset() {
        init(needRes);
    }


    public boolean dispatchComplete(){
        return storeResOrderItems.isEmpty();
    }

}
