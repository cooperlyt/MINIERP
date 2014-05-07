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

        AccountOper accountOper;
        if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            accountOper = new AccountOper(orderHome.getInstance(), orderHome.getInstance().getOrderEmp(), AccountOper.AccountOperType.ORDER_PAY,
                    shipDate, BigDecimal.ZERO, orderHome.getInstance().getMoney(), BigDecimal.ZERO, BigDecimal.ZERO);
        } else if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
            accountOper = new AccountOper(orderHome.getInstance(), orderHome.getInstance().getOrderEmp(), AccountOper.AccountOperType.ORDER_PAY,
                    shipDate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, orderHome.getInstance().getMoney());
        } else {
            BigDecimal canUseAvanceMoney = orderHome.getInstance().getCustomer().getAdvanceMoney().
                    subtract(CustomerMoneyTool.instance().getOrderAdvance(orderHome.getInstance().getCustomer().getId()));
            if (canUseAvanceMoney.compareTo(orderHome.getInstance().getMoney()) > 0) {
                canUseAvanceMoney = orderHome.getInstance().getMoney();
            }
            accountOper = new AccountOper(orderHome.getInstance(), orderHome.getInstance().getOrderEmp(), AccountOper.AccountOperType.ORDER_PAY,
                    shipDate, BigDecimal.ZERO, canUseAvanceMoney, orderHome.getInstance().getMoney().subtract(canUseAvanceMoney), BigDecimal.ZERO);
        }

        accountOper.calcCustomerMoney();
        orderHome.getInstance().getAccountOpers().add(accountOper);
    }

}
