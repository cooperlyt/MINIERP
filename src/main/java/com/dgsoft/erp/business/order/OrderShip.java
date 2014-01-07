package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.DispatchHome;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.OverlyOut;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
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

    @In
    private Credentials credentials;

    private List<OverlyOut> noConfirmOverlys;

    @DataModel("overlyOrderItems")
    private List<OrderItem> overlyOrderItems;

    @DataModelSelection
    private OrderItem selectOverlyOrderItem;

    @In(create=true)
    private ActionExecuteState actionExecuteState;

    private OverlyOut selectOverly;

    private OrderItem editingOrderItem;

    private BigDecimal editingOrderItemTotalMoney;

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

    public void autoConfirmAll() {
        //TODO ff
    }

    public void autoConfirmItem() {
        //TODO ff
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

    public void deleteOrderItem(){
        //TODO ff
    }

    public void saveOverlyToOrderItem() {
         //TODO ff

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
        if (BigDecimalFormat.isTyped(editingOrderItem.getMoney())){
            calcPriceByUnitMoney();
        }else if (BigDecimalFormat.isTyped(editingOrderItemTotalMoney)){
            editingOrderItem.setMoney(calcUnitPrice());
        }
    }

    private BigDecimal calcUnitPrice(){
        BigDecimal result = editingOrderItemTotalMoney.divide(getEditingCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(editingOrderItem.getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP));

        result = BigDecimalFormat.halfUpCurrency(result);
        return result;

    }

    public void calcPriceByTotalMoney() {
        if (!BigDecimalFormat.isTyped(editingOrderItem.getMoney())){
            editingOrderItem.setMoney(calcUnitPrice());
        }else{
            editingOrderItem.setRebate(editingOrderItem.getMoney().divide(calcUnitPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
        }
    }

    public void calcPriceByRebate() {
        calcPriceByUnitMoney();
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

                        overlyOrderItems = new ArrayList<OrderItem>();
                        noConfirmOverlys = new ArrayList<OverlyOut>();
                        for (OverlyOut overly : dispatchHome.getInstance().getOverlyOuts()) {
                            if (!overly.isAddTo()) {
                                noConfirmOverlys.add(overly);
                            }
                        }


                        switch (dispatchHome.getInstance().getDeliveryType()) {
                            case FULL_CAR_SEND:
                                //dispatchHome.getInstance().getExpressCar().
                                //        setCarCode(dispatchHome.getInstance().getExpressCar().getExpressDriver().getCarCode());
                                break;

                            case SEND_TO_DOOR:
                                dispatchHome.getInstance().getProductToDoor().
                                        setToDoorDriver(dispatchHome.getInstance().getProductToDoor().getCars().getDefaultDriver());
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

        dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
        dispatchHome.getInstance().setSendEmp(credentials.getUsername());
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "fail";
        }


    }


}
