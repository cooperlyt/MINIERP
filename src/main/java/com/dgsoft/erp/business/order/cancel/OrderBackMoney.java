package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
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

    private AccountOper accountOper;

    @In(create = true)
    private BussinessProcessUtils businessProcess;

    @In
    private Credentials credentials;

    @In(create = true)
    protected CustomerHome customerHome;

    @Override
    protected void initCancelOrderTask() {


        customerHome.setId(orderHome.getInstance().getCustomer().getId());

        accountOper = new AccountOper(orderBackHome.getInstance(),
                credentials.getUsername());

    }

    public AccountOper getAccountOper() {
        return accountOper;
    }

    public void setAccountOper(AccountOper accountOper) {
        this.accountOper = accountOper;
    }

    public void clacAfterBalance(){
        accountOper.setAfterMoney(accountOper.getBeforMoney().add(accountOper.getOperMoney()));
    }

    @Override
    protected String completeOrderTask() {
        if (accountOper.getOperMoney().compareTo(BigDecimal.ZERO) <= 0){
            return super.completeOrderTask();
        }
        clacAfterBalance();
        accountOper.setOperDate(new Date());

        orderBackHome.getInstance().setAccountOper(accountOper);
        orderBackHome.getInstance().getCustomerOrder().getCustomer().setBalance(accountOper.getAfterMoney());
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        businessProcess.stopProcess("order",orderHome.getInstance().getId());
        if ("updated".equals(orderBackHome.update())) {
            return super.completeOrderTask();
        } else
            return "fail";
    }

}
