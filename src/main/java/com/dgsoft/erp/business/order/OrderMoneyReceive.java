package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.AccountOper;
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


    @Override
    protected AccountOper.AccountOperType getAccountOperType() {
        return AccountOper.AccountOperType.ORDER_PAY;
    }

    @Override
    public BigDecimal getShortageMoney() {
        BigDecimal result = orderHome.getInstance().getMoney().subtract(getTotalReveiveMoney());
        if (orderHome.getMasterNeedRes().isReceiveMoney()){
            result = result.subtract(orderHome.getMasterNeedRes().getTotalFare());
        }
        return result;
    }

    @Override
    protected String completeOrderTask() {
        if (getTotalReveiveMoney().compareTo(orderHome.getInstance().getEarnest()) < 0) {

            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "order_money_not_enough",
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getTotalReveiveMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getShortageMoney()));

            return null;

        }

        return "taskComplete";
    }
}
