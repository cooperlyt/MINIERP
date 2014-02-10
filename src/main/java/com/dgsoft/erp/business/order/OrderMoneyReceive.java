package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    protected AccountOper.AccountOperType getAccountOperType() {
        return AccountOper.AccountOperType.ORDER_PAY;
    }

    @Override
    public BigDecimal getShortageMoney() {
        BigDecimal result = orderHome.getInstance().getMoney().subtract(getTotalReveiveMoney());
        return result;
    }

    @Override
    protected String completeOrderTask() {
        if (getTotalReveiveMoney().compareTo(orderHome.getInstance().getMoney()) < 0) {


            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "order_money_not_enough",
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getTotalReveiveMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getShortageMoney()));

            return null;

        }

        orderHome.getInstance().setMoneyComplete(true);
        BigDecimal receiveMoney = BigDecimal.ZERO;
        BigDecimal payMoney = BigDecimal.ZERO;
        for (AccountOper accountOper: orderHome.getInstance().getAccountOpers()){
            if (accountOper.getOperType().isAdd()){
                receiveMoney = receiveMoney.add(accountOper.getOperMoney());
            }else{
                payMoney = payMoney.add(accountOper.getOperMoney());
            }
        }
        orderHome.getInstance().setArrears(payMoney.compareTo(receiveMoney) > 0);
        if (orderHome.update().equals("updated")){
            if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)){
                needResHome.setId(orderHome.getMasterNeedRes().getId());
            }
            return "taskComplete";
        }else
            return null;

    }
}
