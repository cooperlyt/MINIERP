package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/6/13
 * Time: 12:30 PM
 */

@Name("orderEarnestReceive")
public class OrderEarnestReceive extends FinanceReceivables{


    @Override
    protected AccountOper.AccountOperType getAccountOperType() {
        return AccountOper.AccountOperType.ORDER_EARNEST;
    }
}
