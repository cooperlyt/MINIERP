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

    @Override
    protected void initCancelOrderTask() {
        customerOper = new AccountOper(AccountOper.AccountOperType.ORDER_BACK,credentials.getUsername());
        customerOper.setAdvanceReceivable(orderBackHome.getInstance().getMoney());
        customerOper.setAccountsReceivable(orderBackHome.getInstance().getMoney());
        customerOper.setCustomer(orderBackHome.getInstance().getCustomer());
    }

    public AccountOper getCustomerOper() {
        return customerOper;
    }

    @Override
    protected String completeOrderTask() {


        orderBackHome.getEntityManager().persist(customerOper);
        orderBackHome.getInstance().setMoneyComplete(true);
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        if ("updated".equals(orderBackHome.update())) {
            return super.completeOrderTask();
        } else
            return null;
    }

}
