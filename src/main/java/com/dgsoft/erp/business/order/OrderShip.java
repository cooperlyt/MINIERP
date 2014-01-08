package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
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
    private ExpressCarHome expressCarHome;

    @In(create = true)
    private ExpressInfoHome expressInfoHome;

    @In(create = true)
    private ProductToDoorHome productToDoorHome;

    @In(create = true)
    private TransCorpHome transCorpHome;

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
        orderTotalMoney = BigDecimalFormat.halfUpCurrency(getOrderResTotalMoney().multiply(
                orderRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void calcByOrderTotalMoney() {
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
        overlyOut.setAddTo(true);
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

    public void deleteOrderItem() {
        OverlyOut overlyOut = selectOverlyOrderItem.getOverlyOut();
        if (overlyOut == null) {
            throw new IllegalArgumentException("overly not confirm");
        }
        overlyOut.setOrderItem(null);
        selectOverlyOrderItem.setOverlyOut(null);
        overlyOut.setAddTo(false);
        noConfirmOverlys.add(overlyOut);
        overlyOrderItems.remove(selectOverlyOrderItem);
        selectOverlyOrderItem = null;
        calcByRate();
    }

    public void saveOverlyToOrderItem() {
        matchOrderItem(selectOverly, editingOrderItem);

        selectOverly = null;

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
                        orderRebate = dispatchHome.getInstance().getNeedRes().getCustomerOrder().getTotalRebate();

                        overlyOrderItems = new ArrayList<OrderItem>();
                        noConfirmOverlys = new ArrayList<OverlyOut>();
                        for (OverlyOut overly : dispatchHome.getInstance().getOverlyOuts()) {
                            if (!overly.isAddTo()) {
                                noConfirmOverlys.add(overly);
                            }
                        }


                        switch (dispatchHome.getInstance().getDeliveryType()) {
                            case FULL_CAR_SEND:
                                if (dispatchHome.getInstance().getExpressCar() != null) {
                                    expressCarHome.setId(dispatchHome.getInstance().getExpressCar().getId());
                                    transCorpHome.setId(expressCarHome.getInstance().getTransCorp().getId());
                                } else {
                                    expressCarHome.clearInstance();
                                    transCorpHome.clearInstance();
                                }

                                break;

                            case SEND_TO_DOOR:
                                if (dispatchHome.getInstance().getProductToDoor() != null) {
                                    productToDoorHome.setId(dispatchHome.getInstance().getProductToDoor().getId());
                                } else {
                                    productToDoorHome.clearInstance();
                                }
                                break;

                            case EXPRESS_SEND:
                                if (dispatchHome.getInstance().getExpressInfo() != null) {
                                    expressInfoHome.setId(dispatchHome.getInstance().getExpressInfo().getId());
                                    transCorpHome.setId(expressInfoHome.getInstance().getTransCorp().getId());
                                } else {
                                    expressInfoHome.clearInstance();
                                    transCorpHome.clearInstance();
                                }
                                break;
                        }

                        dispatchHome.getInstance().setFare(BigDecimal.ZERO);
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

        if (!overlyOrderItems.isEmpty()) {
            for (OrderItem item : overlyOrderItems) {
                dispatchHome.getInstance().getNeedRes().getOrderItems().add(item);
            }
            dispatchHome.getInstance().getNeedRes().getCustomerOrder().setTotalRebate(orderRebate);
            //dispatchHome.getInstance().getNeedRes().getCustomerOrder().setTotalCost();
            dispatchHome.getInstance().getNeedRes().getCustomerOrder().setMoney(orderTotalMoney);
        }


        dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
        dispatchHome.getInstance().setSendEmp(credentials.getUsername());
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "fail";
        }


    }


}
