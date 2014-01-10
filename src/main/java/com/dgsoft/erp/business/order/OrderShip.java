package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 10:33 AM
 */
@Name("orderShip")
public class OrderShip extends OrderTaskHandle {

    @Logger
    private Log log;

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    @In(create = true)
    private ResHelper resHelper;

    @In
    private Credentials credentials;

    private boolean inputDetails;

    private List<OverlyOut> noConfirmOverlys;

    @DataModel("overlyOrderItems")
    private List<OrderItem> overlyOrderItems;

    @DataModelSelection
    private OrderItem selectOverlyOrderItem;

    @In(create = true)
    private ActionExecuteState actionExecuteState;

    @In(create = true)
    private TransCorpHome transCorpHome;

    @In(create = true)
    private CarsHome carsHome;

    private OverlyOut selectOverly;

    private OrderItem editingOrderItem;

    private BigDecimal editingOrderItemTotalMoney;

    private BigDecimal orderRebate;

    private BigDecimal orderTotalMoney;

    public List<OverlyOut> getNoConfirmOverlys() {
        return noConfirmOverlys;
    }

    public BigDecimal getEditingOrderItemTotalMoney() {
        return editingOrderItemTotalMoney;
    }

    public void setEditingOrderItemTotalMoney(BigDecimal editingOrderItemTotalMoney) {
        this.editingOrderItemTotalMoney = editingOrderItemTotalMoney;
    }

    public String getOverlyOutId() {

        if (selectOverly == null) {
            return null;
        } else {
            return selectOverly.getId();
        }
    }

    public void setOverlyOutId(String overlyOutId) {
        if (StringUtil.isEmpty(overlyOutId)) {
            selectOverly = null;
            return;
        }

        for (OverlyOut overly : noConfirmOverlys) {
            if (overly.getId().equals(overlyOutId)) {
                selectOverly = overly;
                return;
            }
        }
        selectOverly = null;
    }

    public OverlyOut getSelectOverly() {
        return selectOverly;
    }

    public OrderItem getEditingOrderItem() {
        return editingOrderItem;
    }

    public void setEditingOrderItem(OrderItem editingOrderItem) {
        this.editingOrderItem = editingOrderItem;
    }

    public BigDecimal getOverlyItemTotalPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : overlyOrderItems) {
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getOrderTotalMoney() {
        return orderTotalMoney;
    }

    public void setOrderTotalMoney(BigDecimal orderTotalMoney) {
        this.orderTotalMoney = orderTotalMoney;
    }

    public BigDecimal getOrderRebate() {
        return orderRebate;
    }

    public void setOrderRebate(BigDecimal orderRebate) {
        this.orderRebate = orderRebate;
    }

    public BigDecimal getOrderResTotalMoney() {
        BigDecimal result = orderHome.getInstance().getResTotalMoney();
        for (OrderItem item : overlyOrderItems) {
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public void calcByRate() {
        log.debug("calcByReate  getOrderResTotalMoney:" + getOrderResTotalMoney() + "|orderRebate:" + orderRebate);
        orderTotalMoney = BigDecimalFormat.halfUpCurrency(getOrderResTotalMoney().multiply(
                orderRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void calcByOrderTotalMoney() {
        log.debug("calcByOrderTotalMoney getOrderResTotalMoney:" + getOrderResTotalMoney() + "|orderTotalMoney:" + orderTotalMoney);
        orderRebate = orderTotalMoney.divide(getOrderResTotalMoney(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
    }

    public void autoConfirmAll() {
        List<OverlyOut> prepareOverlyOut = new ArrayList<OverlyOut>(noConfirmOverlys);

        for (OverlyOut overlyOut : prepareOverlyOut) {

            autoMatchOverlyItem(overlyOut);

        }

        calcByRate();
    }

    private boolean autoMatchOverlyItem(OverlyOut overlyOut) {
        boolean matched = false;
        for (OrderItem orderItem : orderHome.getMasterNeedRes().getOrderItems()) {
            if (orderItem.isStoreResItem() &&
                    orderItem.getStoreRes().equals(overlyOut.getStoreRes())) {
                matchOrderItem(overlyOut, orderItem);
                matched = true;
                break;
            }
        }

        if (!matched) {
            for (OrderItem orderItem : orderHome.getMasterNeedRes().getOrderItems()) {
                if ((!orderItem.isStoreResItem()) && orderItem.getRes().equals(overlyOut.getStoreRes().getRes())) {
                    matchOrderItem(overlyOut, orderItem);
                    matched = true;
                    break;
                }
            }
        }

        if (!matched) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN,
                    "overly_cant_match", resHelper.generateStoreResTitle(overlyOut.getStoreRes()));
        }
        return matched;
    }

    private void matchOrderItem(OverlyOut overlyOut, OrderItem orderItem) {
        OrderItem newItem = new OrderItem(dispatchHome.getInstance().getNeedRes(),
                overlyOut.getStoreRes(), BigDecimal.ZERO, orderItem.getMoneyUnit(),
                overlyOut.getResCount().getCountByResUnit(orderItem.getMoneyUnit()),
                orderItem.getMoney(), orderItem.getRebate());
        newItem.setOverlyOut(overlyOut);
        overlyOut.setOrderItem(newItem);

        overlyOrderItems.add(newItem);
        noConfirmOverlys.remove(overlyOut);
    }

    public void autoConfirmItem() {
        autoMatchOverlyItem(selectOverly);
        calcByRate();
    }

    public void beginConfirmOverly() {
        editingOrderItem = new OrderItem(dispatchHome.getInstance().getNeedRes(),
                selectOverly.getStoreRes(), BigDecimal.ZERO);
        editingOrderItem.setMoney(BigDecimal.ZERO);
        editingOrderItem.setRebate(new BigDecimal("100"));
        editingOrderItemTotalMoney = BigDecimal.ZERO;
        editingOrderItem.setMoneyUnit(selectOverly.getStoreRes().getRes().getResUnitByOutDefault());
        actionExecuteState.clearState();
    }

    public void deleteAllOrderItem() {
        for (OrderItem orderItem : overlyOrderItems) {
            OverlyOut overlyOut = orderItem.getOverlyOut();
            overlyOut.setOrderItem(null);
            noConfirmOverlys.add(overlyOut);
        }
        overlyOrderItems.clear();
        calcByRate();
    }

    public void deleteOrderItem() {
        OverlyOut overlyOut = selectOverlyOrderItem.getOverlyOut();
        if (overlyOut == null) {
            log.error("overly not confirm");
            throw new IllegalArgumentException("overly not confirm");
        }
        noConfirmOverlys.add(overlyOut);
        overlyOut.setOrderItem(null);
        selectOverlyOrderItem.setOverlyOut(null);


        overlyOrderItems.remove(selectOverlyOrderItem);
        selectOverlyOrderItem = null;
        calcByRate();
    }

    public void saveOverlyToOrderItem() {
        matchOrderItem(selectOverly, editingOrderItem);

        selectOverly = null;

        calcByRate();
        actionExecuteState.actionExecute();
    }

    private BigDecimal getEditingCount() {
        return selectOverly.getResCount().getCountByResUnit(editingOrderItem.getMoneyUnit());
    }

    public void calcPriceByUnitMoney() {

        editingOrderItemTotalMoney = editingOrderItem.getMoney().multiply(editingOrderItem.getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).
                multiply(getEditingCount());

        editingOrderItemTotalMoney = BigDecimalFormat.halfUpCurrency(editingOrderItemTotalMoney);
    }

    public void calcPriceByUnit() {
        if (BigDecimalFormat.isTyped(editingOrderItem.getMoney())) {
            calcPriceByUnitMoney();
        } else if (BigDecimalFormat.isTyped(editingOrderItemTotalMoney)) {
            editingOrderItem.setMoney(calcUnitPrice());
        }
    }

    private BigDecimal calcUnitPrice() {
        BigDecimal result = editingOrderItemTotalMoney.divide(getEditingCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(editingOrderItem.getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP));

        result = BigDecimalFormat.halfUpCurrency(result);
        return result;

    }

    public void calcPriceByTotalMoney() {
        if (!BigDecimalFormat.isTyped(editingOrderItem.getMoney())) {
            editingOrderItem.setMoney(calcUnitPrice());
        } else {
            editingOrderItem.setRebate(editingOrderItem.getMoney().divide(calcUnitPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
        }
    }

    public void calcPriceByRebate() {
        calcPriceByUnitMoney();
    }

    public boolean isInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(boolean inputDetails) {
        this.inputDetails = inputDetails;
    }

    protected String initOrderTask() {
        String storeId = taskDescription.getValue(OrderStoreOut.TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }

        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            if (needRes.isDispatched()) {
                for (Dispatch dispatch : needRes.getDispatches()) {
                    if (dispatch.getStore().getId().equals(storeId) &&
                            dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_STORE_OUT)) {
                        dispatchHome.setId(dispatch.getId());
                        if (needRes.isFareByCustomer() || !dispatchHome.getInstance().getDeliveryType().isHaveFare() || dispatchHome.getInstance().getFare() == null) {
                            dispatchHome.getInstance().setFare(BigDecimal.ZERO);
                        }
                        orderRebate = dispatchHome.getInstance().getNeedRes().getCustomerOrder().getTotalRebate();
                        orderTotalMoney = dispatchHome.getInstance().getNeedRes().getCustomerOrder().getMoney();
                        overlyOrderItems = new ArrayList<OrderItem>();
                        noConfirmOverlys = new ArrayList<OverlyOut>();
                        for (OverlyOut overly : dispatchHome.getInstance().getOverlyOuts()) {

                            noConfirmOverlys.add(overly);

                        }


                        switch (dispatchHome.getInstance().getDeliveryType()) {
                            case FULL_CAR_SEND:
                            case EXPRESS_SEND:
                                if (dispatchHome.getInstance().getTransCorp() != null) {
                                    transCorpHome.setId(dispatchHome.getInstance().getTransCorp().getId());
                                } else {
                                    transCorpHome.clearInstance();
                                }
                                break;

                            case SEND_TO_DOOR:
                                if (dispatchHome.getInstance().getCar() != null) {
                                    carsHome.setId(dispatchHome.getInstance().getCar().getId());
                                } else {
                                    carsHome.clearInstance();
                                }
                                break;
                        }

                        return "success";
                    }
                }
            }
        }


        return "fail";
    }

    protected String completeOrderTask() {

        if (!noConfirmOverlys.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "overly_item_not_price", noConfirmOverlys.size());
            return "fail";
        }

        if (inputDetails) {
            switch (dispatchHome.getInstance().getDeliveryType()) {
                case FULL_CAR_SEND:
                case EXPRESS_SEND:
                    if (transCorpHome.isIdDefined()) {
                        dispatchHome.getInstance().setTransCorp(transCorpHome.getInstance());
                    } else {
                        dispatchHome.getInstance().setTransCorp(transCorpHome.getReadyInstance());
                    }
                    break;

                case SEND_TO_DOOR:
                    dispatchHome.getInstance().setCar(carsHome.getInstance());
                    break;
            }
        }

        if (!overlyOrderItems.isEmpty()) {
            for (OrderItem item : overlyOrderItems) {
                dispatchHome.getInstance().getNeedRes().getOrderItems().add(item);
            }
            dispatchHome.getInstance().getNeedRes().getCustomerOrder().setTotalRebate(orderRebate);
            //dispatchHome.getInstance().getNeedRes().getCustomerOrder().setTotalCost();
            dispatchHome.getInstance().getNeedRes().getCustomerOrder().setMoney(orderTotalMoney);
        }


        dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "fail";
        }


    }


}
