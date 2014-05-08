package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.NeedResHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
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
public class OrderEarnestReceive extends FinanceReceivables {

    @In(create = true)
    private NeedResHome needResHome;

    @Override
    public BigDecimal getShortageMoney() {
        return orderHome.getInstance().getEarnest().subtract(orderHome.getInstance().getReceiveMoney());
    }



    @Override
    protected String completeOrderTask() {
        if (orderHome.getInstance().isEarnestFirst() && getShortageMoney().compareTo(BigDecimal.ZERO) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "order_earnest_not_enough",
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getEarnest()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getReceiveMoney()),
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getShortageMoney()));

            return null;
        }

        needResHome.setId(orderHome.getMasterNeedRes().getId());
        return "taskComplete";
    }
}
