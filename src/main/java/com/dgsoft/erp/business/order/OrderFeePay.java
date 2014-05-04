package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.MiddleMoneyPay;
import com.dgsoft.erp.model.OrderFee;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/18/13
 * Time: 11:48 AM
 */
@Name("orderFeePay")
public class OrderFeePay extends OrderTaskHandle {

    private List<OrderFee> noPayOrderFeeList;

    private List<OrderFee> payCompleteOrderFeeList;

    public enum ItemMiddleMoneyCalcType {
        NOT_CALC, ITEM_FIX, ITEM_RATE, CROSS_CALC;
    }

    @Factory(value = "allItemMiddleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public ItemMiddleMoneyCalcType[] getAllItemMiddleMoneyCalcTypes() {
        return ItemMiddleMoneyCalcType.values();
    }

    public List<OrderFee> getNoPayOrderFeeList() {
        return noPayOrderFeeList;
    }

    public void setNoPayOrderFeeList(List<OrderFee> noPayOrderFeeList) {
        this.noPayOrderFeeList = noPayOrderFeeList;
    }

    public List<OrderFee> getPayCompleteOrderFeeList() {
        return payCompleteOrderFeeList;
    }

    public void setPayCompleteOrderFeeList(List<OrderFee> payCompleteOrderFeeList) {
        this.payCompleteOrderFeeList = payCompleteOrderFeeList;
    }

    @Observer(value = "org.jboss.seam.afterTransactionSuccess.OrderFee", create = false)
    public void splitOrderFee() {
        orderHome.refresh();
        noPayOrderFeeList = new ArrayList<OrderFee>();
        payCompleteOrderFeeList = new ArrayList<OrderFee>();
        for (OrderFee orderFee : orderHome.getInstance().getOrderFeeList()) {
            if (orderFee.isPay()) {
                payCompleteOrderFeeList.add(orderFee);
            } else {
                noPayOrderFeeList.add(orderFee);
            }
        }
    }

    public BigDecimal getTotalPayedMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderFee fee : payCompleteOrderFeeList) {
            result = result.add(fee.getMoney());
        }
        return result;
    }

    public BigDecimal getTotalNoPayMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderFee fee : noPayOrderFeeList) {
            result = result.add(fee.getMoney());
        }
        return result;
    }


    @Override
    protected String completeOrderTask() {
        if (!noPayOrderFeeList.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "canotCompleteTaskHaveOrderrFee");
            return "fail";
        }

//
//        for (OrderFee orderFee: orderHome.getInstance().getOrderFees()){
//            if (orderFee.isMiddleMoney()){
//                orderHome.getInstance().setMiddleMoneyPay(new MiddleMoneyPay(orderHome.getInstance(),
//                        orderHome.getInstance().getCustomer().getMiddleMan(),
//                        orderFee.getMoney(),orderFee.getPayDate(),orderFee.getPayEmp(),
//                        orderFee.getDescription(),orderFee.getPayType(),orderFee.getCheckNumber()));
//
//                break;
//            }
//        }


        orderHome.update();

        //----------------------

        return "taskComplete";
    }

    @Override
    protected void initOrderTask() {
        splitOrderFee();
    }

}
