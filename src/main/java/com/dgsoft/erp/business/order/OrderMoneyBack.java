package com.dgsoft.erp.business.order;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 4/10/14.
 */
@Name("orderMoneyBack")
@Scope(ScopeType.CONVERSATION)
public class OrderMoneyBack extends OrderTaskHandle {

    private AccountOper accountOper;

    @In
    private Credentials credentials;

    @In(create = true)
    private ProcessInstanceHome processInstanceHome;

    public AccountOper getAccountOper() {
        return accountOper;
    }

    public void setAccountOper(AccountOper accountOper) {
        this.accountOper = accountOper;
    }

    protected void initOrderTask(){
        accountOper = new AccountOper(orderHome.getInstance(),
                credentials.getUsername(), BigDecimal.ZERO);
    }



    protected String completeOrderTask(){

        if (!getAccountOper().getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            AccountOper backAccountOper = new AccountOper(orderHome.getInstance(), credentials.getUsername(),getAccountOper().getRemitFee());

            backAccountOper.setPayType(getAccountOper().getPayType());
            backAccountOper.setOperMoney(getAccountOper().getOperMoney());
            backAccountOper.setBankAccount(getAccountOper().getBankAccount());
            getAccountOper().setBankAccount(null);
            backAccountOper.setCheckNumber(getAccountOper().getCheckNumber());
            getAccountOper().setCheckNumber(null);
            backAccountOper.setRemitFee(getAccountOper().getRemitFee());

            backAccountOper.setOperType(AccountOper.AccountOperType.ORDER_BACK);
            backAccountOper.setOperDate(new Date(getAccountOper().getOperDate().getTime() + 1001));

            orderHome.getInstance().getAccountOpers().add(backAccountOper);
        } else {
            orderHome.getInstance().getCustomer().setBalance(orderHome.getInstance().getCustomer().getBalance().add(getAccountOper().getOperMoney()));
        }

        getAccountOper().setRemitFee(BigDecimal.ZERO);
        // clacAfterBalance();


        orderHome.getInstance().getAccountOpers().add(getAccountOper());
        orderHome.getInstance().setCanceled(true);
        orderHome.getInstance().setMoneyComplete(false);
        // orderBackHome.getInstance().getCustomerOrder().getCustomer().setBalance(accountOper.getAfterMoney());
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
        //businessProcess.stopProcess("order",orderHome.getInstance().getId());
        processInstanceHome.setProcessDefineName("order");
        processInstanceHome.setProcessKey(orderHome.getInstance().getId());

        if ("updated".equals(orderHome.update())) {


            processInstanceHome.stop();
            return super.completeOrderTask();
        } else
            return null;
    }



}
