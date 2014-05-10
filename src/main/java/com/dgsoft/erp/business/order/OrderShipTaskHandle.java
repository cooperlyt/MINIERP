package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.*;
import com.dgsoft.erp.tools.CustomerMoneyTool;
import org.jboss.seam.annotations.In;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 5/5/14.
 */
public abstract class OrderShipTaskHandle extends OrderTaskHandle {


    protected void calcStoreResCompleted(Date shipDate) {
        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            for (OrderItem item : needRes.getOrderItems()) {
                if (!item.getStatus().equals(OrderItem.OrderItemStatus.COMPLETED)) {
                    orderHome.getInstance().setAllStoreOut(false);
                    return;
                }
            }
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.isHaveNoOutOweItem()) {
                    orderHome.getInstance().setAllStoreOut(false);
                    return;
                }
            }

        }
        shipComplete(shipDate);

    }

    protected void shipComplete(Date shipDate) {
        orderHome.getInstance().setAllStoreOut(true);
        orderHome.getInstance().setResReceived(!orderHome.isHaveShip());
        orderHome.getInstance().setAllShipDate(shipDate);

        if (orderHome.getInstance().getMoney().compareTo(BigDecimal.ZERO) != 0) {
            AccountOper accountOper = new AccountOper(AccountOper.AccountOperType.ORDER_PAY, getCustomer(), orderHome.getInstance().getOrderEmp());
            accountOper.setOperDate(shipDate);
            if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
                accountOper.setAdvanceReceivable(orderHome.getInstance().getMoney());
            } else if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                accountOper.setProxcAccountsReceiveable(orderHome.getInstance().getMoney());
            } else {
                accountOper.setAccountsReceivable(orderHome.getInstance().getMoney());
            }

            accountOper.calcCustomerMoney();
            orderHome.getEntityManager().persist(accountOper);
        }
    }

}
