package com.dgsoft.erp.business.order;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
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

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private ProcessInstanceHome processInstanceHome;

    @In
    private OrderHome orderHome;

    @CreateProcess(definition = "cancelOrderMoney", processKey = "#{orderHome.instance.id}")
    @Transactional
    public String cancelOrderMoney() {
        orderHome.getInstance().setCanceled(true);
        if ("updated".equals(orderHome.update())) {
            initProcessInstanceHome();
            processInstanceHome.suspend();
            return "updated";
        } else {
            return null;
        }
    }

    private void initProcessInstanceHome() {
        processInstanceHome.setProcessDefineName("order");
        processInstanceHome.setProcessKey(orderHome.getInstance().getId());
    }


    @Transactional
    public void removeOrder() {
        if (orderHome.isInAccount()){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "isInAccount");
            return;
        }
        for (AccountOper ao : orderHome.getInstance().getAccountOpers()) {
            ao.revertCustomerMoney();
            orderHome.getEntityManager().remove(ao);
        }

        orderHome.getInstance().getAccountOpers().clear();
        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.getStockChange() != null) {
                    for (StockChangeItem item : dispatch.getStockChange().getStockChangeItems()) {
                        item.getStock().setCount(item.getStock().getCount().add(item.getCount()));
                    }
                    orderHome.getEntityManager().remove(dispatch.getStockChange());
                }
                dispatch.getOrderItems().clear();
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
