package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.action.CarsHome;
import com.dgsoft.erp.action.TransCorpHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
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
import java.util.Collection;
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
    private CarsHome carsHome;

    private boolean shipDetails;

    @In
    protected FacesMessages facesMessages;

    @DataModel("dispatchOrderItems")
    private List<OrderItem> noDispatchItems;

    @DataModelSelection
    private OrderItem selectedOrderItem;

    private List<Dispatch> dispatchList;


    //--------------------

    private Store store;

    //private BigDecimal count;

    private StoreResCount operCount;

    //private ResUnit unit;

    private String orderItemId;

    // private String memo;

    // private Dispatch.DeliveryType deliveryType;

    //private boolean dispatchStoreExists;

    private OrderItem operOrderItem;

    //----------------------

    private String storeId;

    private Dispatch selectDispatch;

    private boolean editInfo;


    //----------------------------

    //private NeedRes needRes;

    public void clearDispatch() {
        store = null;
        //count = BigDecimal.ZERO;
        orderItemId = null;
        //memo = null;

        //dispatchStoreExists = false;

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

    public OrderItem getOperOrderItem() {
        return operOrderItem;
    }

    public void setOperOrderItem(OrderItem operOrderItem) {
        this.operOrderItem = operOrderItem;
    }

    public StoreResCount getOperCount() {
        return operCount;
    }

    public void setOperCount(StoreResCount operCount) {
        this.operCount = operCount;
    }

//    public ResUnit getUnit() {
//        return unit;
//    }
//
//    public void setUnit(ResUnit unit) {
//        this.unit = unit;
//    }

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

    public List<Dispatch> getDispatchList(){
        return dispatchList;
    }

    public void wire() {

        for (Dispatch d: dispatchList){
            d.setNeedRes(needRes);
            for(OrderItem item: d.getOrderItems()){
                item.setStatus(OrderItem.OrderItemStatus.DISPATCHED);
            }
        }
        needRes.getDispatches().clear();
        needRes.getDispatches().addAll(dispatchList);
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

    public boolean isEditInfo() {
        return editInfo;
    }

    public void setEditInfo(boolean editInfo) {
        this.editInfo = editInfo;
    }

    public void beginEditDispatchInfo() {
        for (Dispatch dispatch : dispatchList) {
            if (dispatch.getStore().getId().equals(storeId)) {
                selectDispatch = dispatch;
                //deliveryType = selectDispatch.getDeliveryType();
                //memo = selectDispatch.getMemo();


                carsHome.clearInstance();
                transCorpHome.clearInstance();

                switch (selectDispatch.getDeliveryType()) {
                    case SEND_TO_DOOR:
                        if (selectDispatch.getCar() != null)
                            carsHome.setId(selectDispatch.getCar().getId());
                        break;
                    case FULL_CAR_SEND:
                    case EXPRESS_SEND:
                        if (selectDispatch.getTransCorp() != null)
                            transCorpHome.setId(selectDispatch.getTransCorp().getId());
                        break;
                }

                store = selectDispatch.getStore();
                break;
            }
        }
        operOrderItem = null;
        editInfo = true;
        actionExecuteState.clearState();
    }

    public void beginDispatchItem() {
        editInfo = false;


//        for (OrderItem oi : storeResOrderItems) {
//            if (oi.getId().equals(orderItemId)) {
//                operOrderItem = oi;
//                break;
//            }
//        }
        operOrderItem = selectedOrderItem;

        operCount = new StoreResCount(operOrderItem.getStoreRes(), operOrderItem.getCount());


        actionExecuteState.clearState();
        clearDispatch();
        shipDetails = false;
    }

    public void beginDispatchAll() {
        editInfo = false;
        clearDispatch();
        shipDetails = false;
        operOrderItem = null;
        actionExecuteState.clearState();
    }


    public void dispatchAction() {
        setSendInfo();
        if (!editInfo) {


            if ((operOrderItem != null) && (operCount.getMasterCount().compareTo(BigDecimal.ZERO) <= 0)) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_item_count_less_zero");
                actionExecuteState.setState("fail");
                return;
            }


            if (!dispatchList.contains(selectDispatch)) {
                selectDispatch.setStoreOut(false);
                dispatchList.add(selectDispatch);
            }


            if (operOrderItem == null) {
                for (OrderItem oi : noDispatchItems) {
                    dispatchItem(oi, oi);
                }
            } else {

                if (operCount.getMasterCount().compareTo(operOrderItem.getMasterCount()) > 0) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "dispatch_item_count_over",
                            operCount.getDisplayMasterCount(),
                            operOrderItem.getDisplayMasterCount());
                    operCount.setMasterCount(operOrderItem.getMasterCount());
                }

                dispatchItem(operOrderItem, operCount);

                operOrderItem = null;
            }

            clearDispatch();

        }
        actionExecuteState.actionExecute();

    }

    private void setSendInfo() {

        if (shipDetails) {


            switch (selectDispatch.getDeliveryType()) {
                case FULL_CAR_SEND:
                case EXPRESS_SEND:
                    if (transCorpHome.isIdDefined()) {
                        selectDispatch.setTransCorp(transCorpHome.getInstance());
                    } else {
                        selectDispatch.setTransCorp(transCorpHome.getReadyInstance());
                    }
                    selectDispatch.setCar(null);
                    break;
                case SEND_TO_DOOR:

                    selectDispatch.setCar(carsHome.getInstance());
                    selectDispatch.setTransCorp(null);
                    break;
            }
        } else {
            selectDispatch.setTransCorp(null);
            selectDispatch.setCar(null);
        }
    }

    private void dispatchItem(OrderItem dispathcItem, StoreResCountEntity resCount) {

        if (dispathcItem.getCount().compareTo(resCount.getCount()) == 0){
            dispathcItem.setDispatch(selectDispatch);
            selectDispatch.getOrderItems().add(dispathcItem);

            noDispatchItems.remove(dispathcItem);
        }else{
            dispathcItem.setMasterCount(dispathcItem.getMasterCount().subtract(resCount.getMasterCount()));


            OrderItem splitItem = new OrderItem(selectDispatch,dispathcItem.getStoreRes(),resCount.getCount(),dispathcItem.getMoney(),
                    dispathcItem.getMoneyRebate(),dispathcItem.getResUnit(),
                    dispathcItem.isPresentation(),dispathcItem.getMemo(),dispathcItem.getNeedConvertRate());

            needRes.getOrderItems().add(splitItem);

            selectDispatch.getOrderItems().add(splitItem);

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
        selectDispatch = getStoreDispatch();
        if (selectDispatch == null) {
            selectDispatch = new Dispatch(store, needRes);
        } else {
            switch (selectDispatch.getDeliveryType()) {
                case EXPRESS_SEND:
                case FULL_CAR_SEND:
                    if (selectDispatch.getTransCorp() != null) {
                        shipDetails = true;
                        transCorpHome.clearInstance();
                        transCorpHome.setInstance(selectDispatch.getTransCorp());
                    } else {
                        shipDetails = false;
                        transCorpHome.clearInstance();
                    }
                    break;
                case SEND_TO_DOOR:
                    if (selectDispatch.getCar() != null) {
                        shipDetails = true;
                        carsHome.setId(selectDispatch.getCar().getId());
                    } else {
                        shipDetails = false;
                        carsHome.clearInstance();
                    }

                default:
                    shipDetails = false;
            }
        }

    }


//    public void dispatchStoreRes() {
//
//        Dispatch dispatch = null;
//        for (Dispatch d : dispatchList) {
//            if (d.getStore().getId().equals(store.getId())) {
//                dispatch = d;
//                break;
//            }
//        }
//
//        if (dispatch == null) {
//            dispatch = new Dispatch();
//        }
//
//        actionExecuteState.actionExecute();
//    }


    private NeedRes needRes;
    //private Collection<OrderItem> oldOrderItems;

    public void init(NeedRes needRes) {
        this.needRes = needRes;
        noDispatchItems =  new ArrayList<OrderItem>(needRes.getOrderItemList());
        for (OrderItem item: noDispatchItems){
            item.setDispatch(null);
            item.setStatus(OrderItem.OrderItemStatus.CREATED);
        }
        dispatchList = new ArrayList<Dispatch>();
        clearDispatch();
    }


    public boolean isDispatchComplete() {
        return noDispatchItems.isEmpty();
    }

}
