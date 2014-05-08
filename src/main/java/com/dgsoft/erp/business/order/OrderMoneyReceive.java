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
    public BigDecimal getShortageMoney() {
        if (orderHome.getInstance().getCustomer().getAdvanceMoney().compareTo(orderHome.getInstance().getMoney()) >= 0) {
            return BigDecimal.ZERO;
        } else {
            return orderHome.getInstance().getMoney().subtract(orderHome.getInstance().getAdvanceMoney());
        }
    }

    @Override
    protected String completeOrderTask() {

        if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            if (getShortageMoney().compareTo(BigDecimal.ZERO) > 0) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                        "order_money_not_enough",
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getMoney()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getCustomer().getAdvanceMoney()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getMoney().subtract(orderHome.getInstance().getCustomer().getAdvanceMoney())));
                return null;
            }
            needResHome.setId(orderHome.getMasterNeedRes().getId());
        }
        return "taskComplete";


    }
}
