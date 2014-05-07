package com.dgsoft.erp.business.order;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cooper on 4/10/14.
 */
@Name("orderMoneyBack")
@Scope(ScopeType.CONVERSATION)
public class OrderMoneyBack extends OrderTaskHandle {

    private AccountOper customerOper;

    @In
    private Credentials credentials;

    @In(create = true)
    private ProcessInstanceHome processInstanceHome;


    private Date operDate;

    public boolean backToAdvance = true;

    private AccountOper.AccountOperType operType;

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public AccountOper.AccountOperType getOperType() {
        return operType;
    }

    public void setOperType(AccountOper.AccountOperType operType) {
        this.operType = operType;
    }

    public AccountOper getCustomerOper() {
        return customerOper;
    }

    public boolean isBackToAdvance() {
        return backToAdvance;
    }

    public void setBackToAdvance(boolean backToAdvance) {
        this.backToAdvance = backToAdvance;
    }

    protected void initOrderTask(){
        customerOper = new AccountOper(orderHome.getInstance(),
                credentials.getUsername(), AccountOper.AccountOperType.MONEY_BACK_TO_CUSTOMER,operDate,
                BigDecimal.ZERO,BigDecimal.ZERO,orderHome.getInstance().getReceiveMoney(),BigDecimal.ZERO);
    }



    protected String completeOrderTask(){

//
//        List<AccountOper> accountOpers = new ArrayList<AccountOper>(2);
//
//        accountOpers.add(new AccountOper(orderHome.getInstance(),
//                credentials.getUsername(), AccountOper.AccountOperType.ORDER_CANCELED, operDate,
//                BigDecimal.ZERO, BigDecimal.ZERO, orderHome.getInstance().getReceiveMoney(), BigDecimal.ZERO));
//
//
//        if (backToAdvance) {
//            accountOpers.add(
//                    new AccountOper(orderHome.getInstance(),
//                            credentials.getUsername(), AccountOper.AccountOperType.MONEY_BACK_TO_PREPARE, new Date(operDate.getTime() + 1001),
//                            BigDecimal.ZERO, orderHome.getInstance().getReceiveMoney(), orderHome.getInstance().getReceiveMoney(),BigDecimal.ZERO));
//        } else {
//
//            customerOper.setOperDate(new Date(operDate.getTime() + 1001));
//            accountOpers.add(customerOper);
//
//        }
//
//        for(AccountOper ap: accountOpers){
//            ap.calcCustomerMoney();
//        }
//        orderHome.getInstance().getAccountOpers().addAll(accountOpers);
//
//
//
//        orderHome.getInstance().setCanceled(true);
//        orderHome.getInstance().setMoneyComplete(true);
//        // orderBackHome.getInstance().getCustomerOrder().getCustomer().setBalance(accountOper.getAfterMoney());
//        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
//        //businessProcess.stopProcess("order",orderHome.getInstance().getId());
//        processInstanceHome.setProcessDefineName("order");
//        processInstanceHome.setProcessKey(orderHome.getInstance().getId());
//
//        if ("updated".equals(orderHome.update())) {
//
//
//            processInstanceHome.stop();
//            return super.completeOrderTask();
//        } else
            return null;
    }



}
