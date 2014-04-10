package com.dgsoft.erp.business.order;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;

import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/04/14
 * Time: 16:00
 */
@Name("orderCancel")
public class OrderCancel {

    @Logger
    private Log log;

    @In(create = true)
    private BusinessCreate businessCreate;

    @In(create = true)
    private ProcessInstanceHome processInstanceHome;

    @In
    private OrderHome orderHome;


    @Transactional
    public void cancelOrderMoney() {
        log.debug("cancelOrderMoney OrderId:" + orderHome);
        orderHome.getInstance().setCanceled(true);
        if ("updated".equals(orderHome.update())) {
            initProcessInstanceHome();
            processInstanceHome.suspend();
            businessCreate.create();
        }
    }

    private void initProcessInstanceHome() {
        processInstanceHome.setProcessDefineName("order");
        processInstanceHome.setProcessKey(orderHome.getInstance().getId());
    }

    @Transactional
    public void removeOrder() {
        BigDecimal operMoney = BigDecimal.ZERO;
        for (AccountOper ao : orderHome.getInstance().getAccountOpers()) {
            if (ao.getOperType().isAdd()) {
                operMoney = operMoney.subtract(ao.getOperMoney());
            } else {
                operMoney = operMoney.add(ao.getOperMoney());
            }
        }
        log.debug("backMoney:" + operMoney);
        if (operMoney.compareTo(BigDecimal.ZERO) != 0) {
            orderHome.getInstance().getCustomer().setBalance(
                    orderHome.getInstance().getCustomer().getBalance().add(operMoney));
        }
        orderHome.getInstance().getCustomer().getAccountOpers().removeAll(orderHome.getInstance().getAccountOpers());
        orderHome.getInstance().getAccountOpers().clear();
        orderHome.getInstance().setMoneyComplete(false);
        orderHome.getInstance().setReceiveMoney(BigDecimal.ZERO);


        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.getStockChange() != null) {
                    for (StockChangeItem item : dispatch.getStockChange().getStockChangeItems()) {
                        item.getStock().setCount(item.getStock().getCount().add(item.getCount()));
                    }
                    orderHome.getEntityManager().remove(dispatch.getStockChange());
                }
            }
            needRes.getDispatches().clear();
            needRes.setStatus(NeedRes.NeedResStatus.REMOVED);
        }

        for (OrderItem orderItem : orderHome.getOrderItemByStatus(EnumSet.allOf(OrderItem.OrderItemStatus.class))) {
            orderItem.setStatus(OrderItem.OrderItemStatus.REMOVED);
            orderItem.setDispatch(null);
        }

        orderHome.getInstance().setResReceived(false);
        orderHome.getInstance().setAllStoreOut(false);
        orderHome.getInstance().setCanceled(true);

        initProcessInstanceHome();

        if ("updated".equals(orderHome.update())) {
            processInstanceHome.stop();
        }

    }


}
