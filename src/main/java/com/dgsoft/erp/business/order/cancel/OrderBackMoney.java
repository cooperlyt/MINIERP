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

    private AccountOper accountOper;

    @In
    private Credentials credentials;

    @Override
    protected void initCancelOrderTask() {
        accountOper = new AccountOper(orderBackHome.getInstance(),
                credentials.getUsername());
    }

    public AccountOper getAccountOper() {
        return accountOper;
    }

    public void setAccountOper(AccountOper accountOper) {
        this.accountOper = accountOper;
    }


    @Override
    protected String completeOrderTask() {
        orderBackHome.getInstance().getAccountOpers().clear();


        if (!getAccountOper().getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            AccountOper backAccountOper = new AccountOper(orderBackHome.getInstance(), credentials.getUsername());

            backAccountOper.setPayType(getAccountOper().getPayType());
            backAccountOper.setOperMoney(getAccountOper().getOperMoney());
            backAccountOper.setBankAccount(getAccountOper().getBankAccount());
            getAccountOper().setBankAccount(null);
            backAccountOper.setCheckNumber(getAccountOper().getCheckNumber());
            getAccountOper().setCheckNumber(null);
            backAccountOper.setRemitFee(getAccountOper().getRemitFee());
            getAccountOper().setRemitFee(BigDecimal.ZERO);
            backAccountOper.setOperType(AccountOper.AccountOperType.ORDER_BACK);
            backAccountOper.setOperDate(new Date(getAccountOper().getOperDate().getTime() + 1001));

            orderBackHome.getInstance().getAccountOpers().add(backAccountOper);
        } else {
            orderBackHome.getInstance().getCustomer().setBalance(orderBackHome.getInstance().getCustomer().getBalance().add(getAccountOper().getOperMoney()));
        }


        // clacAfterBalance();


        orderBackHome.getInstance().getAccountOpers().add(getAccountOper());
        // orderBackHome.getInstance().getCustomerOrder().getCustomer().setBalance(accountOper.getAfterMoney());
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        //businessProcess.stopProcess("order",orderHome.getInstance().getId());
        if ("updated".equals(orderBackHome.update())) {
            return super.completeOrderTask();
        } else
            return null;
    }

}
