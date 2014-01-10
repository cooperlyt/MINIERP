package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.model.AccountOper;
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
 * Time: 12:30 PM
 */

@Name("orderEarnestReceive")
public class OrderEarnestReceive extends FinanceReceivables{

    @In(create = true)
    private NeedResHome needResHome;

    @Override
    protected AccountOper.AccountOperType getAccountOperType() {
        return AccountOper.AccountOperType.ORDER_EARNEST;
    }

    @Override
    public BigDecimal getShortageMoney(){
        return  orderHome.getInstance().getEarnest().subtract(getTotalReveiveMoney());
    }

    @Override
    protected String completeOrderTask(){
        if (getTotalReveiveMoney().compareTo(orderHome.getInstance().getEarnest()) < 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "order_earnest_not_enough",
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getEarnest()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getTotalReveiveMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getShortageMoney()));

            return null;
        }

        needResHome.setId(orderHome.getMasterNeedRes().getId());
        return "taskComplete";
    }
}
