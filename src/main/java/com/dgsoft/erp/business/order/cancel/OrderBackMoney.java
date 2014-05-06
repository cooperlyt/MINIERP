package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/20/13
 * Time: 4:50 PM
 */
@Name("orderBackMoney")
public class OrderBackMoney extends CancelOrderTaskHandle {

    private AccountOper customerOper;

    @In
    private Credentials credentials;

    private Date operDate;

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public boolean backToAdvance = false;

    public boolean isBackToAdvance() {
        return backToAdvance;
    }

    public void setBackToAdvance(boolean backToAdvance) {
        this.backToAdvance = backToAdvance;
    }

    @Override
    protected void initCancelOrderTask() {
        customerOper = new AccountOper(orderBackHome.getInstance(),
                credentials.getUsername(), AccountOper.AccountOperType.MONEY_BACK_TO_CUSTOMER, operDate,
                BigDecimal.ZERO, BigDecimal.ZERO, orderBackHome.getInstance().getMoney());
    }

    public AccountOper getCustomerOper() {
        return customerOper;
    }

    @Override
    protected String completeOrderTask() {
        orderBackHome.getInstance().getAccountOpers().clear();

        orderBackHome.getInstance().getAccountOpers().add(new AccountOper(orderBackHome.getInstance(),
                credentials.getUsername(), AccountOper.AccountOperType.ORDER_BACK, operDate,
                BigDecimal.ZERO, BigDecimal.ZERO, orderBackHome.getInstance().getMoney()));


        if (backToAdvance) {
            orderBackHome.getInstance().getAccountOpers().add(
                    new AccountOper(orderBackHome.getInstance(),
                            credentials.getUsername(), AccountOper.AccountOperType.MONEY_BACK_TO_PREPARE, new Date(operDate.getTime() + 1001),
                            BigDecimal.ZERO, orderBackHome.getInstance().getMoney(), orderBackHome.getInstance().getMoney()));
        } else {

            customerOper.setOperDate(new Date(operDate.getTime() + 1001));
            orderBackHome.getInstance().getAccountOpers().add(customerOper);

        }

        for (AccountOper ap : orderBackHome.getInstance().getAccountOpers()) {
            ap.calcCustomerMoney();
        }


        orderBackHome.getInstance().setMoneyComplete(true);
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        if ("updated".equals(orderBackHome.update())) {
            return super.completeOrderTask();
        } else
            return null;
    }

}
