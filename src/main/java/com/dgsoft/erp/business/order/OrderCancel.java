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
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.Date;
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

    private Date moneyOperDate;

    public Date getMoneyOperDate() {
        return moneyOperDate;
    }

    public void setMoneyOperDate(Date moneyOperDate) {
        this.moneyOperDate = moneyOperDate;
    }

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

    private CustomerOrder.OrderPayType changeToPayType;

    public CustomerOrder.OrderPayType getChangeToPayType() {
        return changeToPayType;
    }

    public void setChangeToPayType(CustomerOrder.OrderPayType changeToPayType) {
        this.changeToPayType = changeToPayType;
    }

    public void changeShipDate() {
        if (orderHome.isMoneyInAccount()) {
            throw new IllegalArgumentException("order money is in Account!");
        }

        if (!orderHome.getInstance().getAccountOpers().isEmpty()) {
            for (AccountOper oper : orderHome.getInstance().getAccountOpers()) {
                oper.setOperDate(orderHome.getInstance().getCreateDate());
            }
        }
        orderHome.update();

    }

    public void subMoneyStoreOut(){
        if (orderHome.getInstance().isAccountChange() && orderHome.getInstance().getAccountOpers().isEmpty()){
            orderHome.signalMoney(moneyOperDate);
        }
    }

    public void changePayType() {
        if (orderHome.isMoneyInAccount()) {
            throw new IllegalArgumentException("order money is in Account!");
        }
        if (!orderHome.getInstance().getPayType().equals(changeToPayType)) {
            if ((orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)
                    || changeToPayType.equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) &&
                    (!orderHome.getInstance().getAccountOpers().isEmpty())) {
                for (AccountOper accountOper : orderHome.getInstance().getAccountOpers()) {


                    // accountOper.setAdvanceReceivable();
                    if (changeToPayType.equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                        if (orderHome.getInstance().getCustomer().getProxyAccountMoney().
                                subtract(accountOper.getProxcAccountsReceiveable()).
                                add(orderHome.getInstance().getMoney()).compareTo(BigDecimal.ZERO) < 0) {
                            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "OrderCantChangeMoneyByProxyMoney");
                            return;
                        }
                        accountOper.revertCustomerMoney();
                        accountOper.setProxcAccountsReceiveable(orderHome.getInstance().getMoney());
                        accountOper.setAccountsReceivable(BigDecimal.ZERO);
                    } else {
                        accountOper.revertCustomerMoney();
                        accountOper.setAccountsReceivable(orderHome.getInstance().getMoney());
                        accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
                    }

                    accountOper.calcCustomerMoney();
                }

            }
            orderHome.getInstance().setPayType(changeToPayType);

            orderHome.update();
        }

    }

    public String toEditMoneyRebate() {
        orderHome.refreshSaleRebate();
        return "/func/erp/sale/OrderEditMoney.xhtml";
    }

    public String changeOrderPrice() {
        if (orderHome.isMoneyInAccount()) {
            throw new IllegalArgumentException("order money is in Account!");
        }


        if (orderHome.changeMoney(orderHome.getInstance().getCreateDate())) {
            return orderHome.update();
        } else {
            return null;
        }
    }

    @Transactional
    public void removeOrder() {
        if (orderHome.isInAccount()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "isInAccount");
            return;
        }
        for (AccountOper ao : orderHome.getInstance().getAccountOpers()) {
            ao.revertCustomerMoney();
            orderHome.getEntityManager().remove(ao);
        }

        Logging.getLog(getClass()).debug("begin remove order:" + orderHome.getInstance().getId());
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
        orderHome.getResSaleRebates().clear();

        orderHome.getInstance().setResReceived(false);
        orderHome.getInstance().setAllStoreOut(false);
        orderHome.getInstance().setCanceled(true);

        initProcessInstanceHome();

        if ("updated".equals(orderHome.update())) {
            processInstanceHome.stop();
        }

    }


}
