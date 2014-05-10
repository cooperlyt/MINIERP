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


    @Override
    public BigDecimal getNeedMoney() {
        if (orderHome.getInstance().isEarnestFirst()){
            return orderHome.getInstance().getEarnest();
        }else{
            return BigDecimal.ZERO;
        }
    }


}
