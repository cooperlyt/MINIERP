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


    @Override
    public BigDecimal getNeedMoney() {
        if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)){
            return orderHome.getInstance().getMoney();
        }else{
            return BigDecimal.ZERO;
        }
    }


}
