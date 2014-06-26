package com.dgsoft.erp.business.order;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 5/5/14.
 */
public abstract class OrderShipTaskHandle extends OrderTaskHandle {




    protected void shipComplete(Date shipDate) {
        orderHome.getInstance().setAllStoreOut(true);
        orderHome.getInstance().setResReceived(!orderHome.isHaveShip());
        orderHome.getInstance().setAllShipDate(shipDate);

        if (orderHome.getInstance().getMoney().compareTo(BigDecimal.ZERO) != 0) {
            AccountOper accountOper = new AccountOper(AccountOper.AccountOperType.ORDER_PAY, orderHome.getInstance().getOrderEmp());
            accountOper.setCustomerOrder(orderHome.getInstance());
            accountOper.setOperDate(shipDate);
            accountOper.setCustomer(getCustomer());
            if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST) &&
                    RunParam.instance().getBooleanParamValue("erp.finance.useAdvance")) {
                accountOper.setAdvanceReceivable(orderHome.getInstance().getMoney());
            } else if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                accountOper.setProxcAccountsReceiveable(orderHome.getInstance().getMoney());
            } else {
                accountOper.setAccountsReceivable(orderHome.getInstance().getMoney());
            }

            accountOper.calcCustomerMoney();
            orderHome.getInstance().getAccountOpers().add(accountOper);
        }
    }

}
