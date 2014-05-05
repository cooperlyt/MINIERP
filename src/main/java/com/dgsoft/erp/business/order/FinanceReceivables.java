package com.dgsoft.erp.business.order;


import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:55 AM
 */

public abstract class FinanceReceivables extends OrderTaskHandle {

    @In(create = true)
    protected CustomerHome customerHome;

    protected AccountOper accountOper;

    @In
    protected Credentials credentials;

    private BigDecimal operMoney;

    private boolean freeMoney;

    public boolean isFreeMoney() {
        return freeMoney;
    }

    public void setFreeMoney(boolean freeMoney) {
        this.freeMoney = freeMoney;
    }

    public BigDecimal getOrderShortageMoney() {
        return orderHome.getInstance().getShortageMoney();
    }

    public BigDecimal getShortageMoney() {
        return getOrderShortageMoney();
    }

//    private Customer getCustomer() {
//        return orderHome.getInstance().getCustomer();
//    }

    public AccountOper getAccountOper() {
        return accountOper;
    }

    public void setAccountOper(AccountOper accountOper) {
        this.accountOper = accountOper;
    }

    public BigDecimal getOperMoney() {
        return operMoney;
    }

    public void setOperMoney(BigDecimal operMoney) {
        this.operMoney = operMoney;
    }

    public void allMoney() {
        setOperMoney(getShortageMoney());
    }

    protected void receiveAdvance() {
        accountOper.setAdvanceReceivable(getOperMoney());
        accountOper.setAccountsReceivable(BigDecimal.ZERO);
        accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
    }

    protected abstract void receiveAccountOper();

    @Transactional
    public void receiveMoney() {

        if (getOrderShortageMoney().compareTo(BigDecimal.ZERO) <= 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderMoneyIsComplete");
            return;
        }

        if (isFreeMoney() && orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
            throw new IllegalArgumentException("EXPRESS_PROXY cant free Money");
        }

        if (!isFreeMoney() && (accountOper.getRemitFee().compareTo(getOperMoney()) >= 0)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "remitFeeGtOperMoney");
            return;
        }

        receiveAccountOper();
        accountOper.calcCustomerMoney();
        orderHome.getInstance().getAccountOpers().add(accountOper);



        if (!orderHome.getInstance().isAllStoreOut()) {
            orderHome.getInstance().setAdvanceMoney(
                    orderHome.getInstance().getAdvanceMoney().
                            add((getOperMoney().compareTo(orderHome.getInstance().getMoney()) > 0) ? orderHome.getInstance().getMoney() : getOperMoney() ));
        }

        orderHome.calcMoneys();
        if ("updated".equals(orderHome.update())) {
            reset();

            Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        }

    }

    public void reset() {
        accountOper = new AccountOper(orderHome.getInstance(), credentials.getUsername(),
                AccountOper.AccountOperType.ORDER_SAVINGS);
        setOperMoney(null);
    }

    @Override
    protected void initOrderTask() {
        reset();
        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        super.initOrderTask();
    }

}
