package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/6/13
 * Time: 12:31 PM
 */
@Name("orderMoneyReceive")
public class OrderMoneyReceive extends FinanceReceivables {

    @In(create = true)
    private NeedResHome needResHome;

    @Override
    protected void receiveAccountOper() {

        BigDecimal result = null;
        if (!orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            if (isFreeMoney()) {
                accountOper = new AccountOper(orderHome.getInstance(),
                        credentials.getUsername(), AccountOper.AccountOperType.ORDER_FREE,
                        accountOper.getOperDate(), getOperMoney(), BigDecimal.ZERO, getOperMoney(), BigDecimal.ZERO);
            } else {


                BigDecimal orderMoney = getOperMoney();
                BigDecimal advanceMoney = BigDecimal.ZERO;

                if (orderMoney.compareTo(getOrderShortageMoney()) > 0) {
                    orderMoney = getOrderShortageMoney();
                    advanceMoney = getOperMoney().subtract(orderMoney);
                }

                accountOper.setAdvanceReceivable(advanceMoney);
                if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                    accountOper.setAccountsReceivable(BigDecimal.ZERO);
                    accountOper.setProxcAccountsReceiveable(orderMoney);
                } else {
                    accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
                    accountOper.setAccountsReceivable(orderMoney);
                }
            }
        } else {
            receiveAdvance();
        }

    }

    private void addCantCompleteMsg(){
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                "order_money_not_enough",
                DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getMoney()),
                DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getReceiveMoney()),
                DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getShortageMoney()));
    }

    @Override
    protected String completeOrderTask() {
        //orderPayType change from payFirst



        //更改收费方式 先发货  允许完成
        if (!(!orderHome.isAnyOneStoreOut() && !orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST))) {
            addCantCompleteMsg();
            return null;
        }

        if (!orderHome.isAnyOneStoreOut() &&
                (orderHome.getInstance().getShortageMoney().compareTo(BigDecimal.ZERO) != 0) &&
                (orderHome.getInstance().getShortageMoney().compareTo(customerHome.getCanUseAdvanceMoney()) <= 0)){
            orderHome.getInstance().setReceiveMoney(orderHome.getInstance().getReceiveMoney().add(orderHome.getInstance().getShortageMoney()));
            //除欠款发货 外 其它都不允许完成
        }else if (!orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.OVERDRAFT) &&
                (orderHome.getInstance().getShortageMoney().compareTo(BigDecimal.ZERO) != 0)){
            addCantCompleteMsg();
            return null;
        }




        orderHome.getInstance().setMoneyComplete(true);

        if (orderHome.update().equals("updated")) {
            if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
                needResHome.setId(orderHome.getMasterNeedRes().getId());
            }
            return "taskComplete";
        } else
            return null;

    }
}
