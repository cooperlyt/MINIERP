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

//    protected AccountOper accountOper;

//    @In
//    protected Credentials credentials;

    private BigDecimal operMoney;

    public BigDecimal getShortageMoney() {
        if (orderHome.getInstance().getCustomer().getAdvanceMoney().compareTo(orderHome.getInstance().getMoney()) >= 0) {
            return BigDecimal.ZERO;
        } else {
            return orderHome.getInstance().getMoney().subtract(orderHome.getInstance().getAdvanceMoney());
        }
    }

//    public AccountOper getAccountOper() {
//        return accountOper;
//    }
//
//    public void setAccountOper(AccountOper accountOper) {
//        this.accountOper = accountOper;
//    }

    public BigDecimal getOperMoney() {
        return operMoney;
    }

    public void setOperMoney(BigDecimal operMoney) {
        this.operMoney = operMoney;
    }

//    public void allMoney() {
//        setOperMoney(getShortageMoney());
//    }

    public boolean isMoneyComplete(){
        return getShortageMoney().compareTo(BigDecimal.ZERO) <= 0;
    }

    @Transactional
    public void receiveMoney() {

        if (getShortageMoney().compareTo(BigDecimal.ZERO) <= 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderMoneyIsComplete");
            return;
        }


//        if ((accountOper.getRemitFee().compareTo(getOperMoney()) >= 0)) {
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "remitFeeGtOperMoney");
//            return;
//        }

        //if (getCustomer().getAccountMoney().compareTo(BigDecimal.ZERO) > 0){
            //TODO  if  getCustomer().getAccountMoney().compareTo(BigDecimal.ZERO) > 0


            accountOper.setAdvanceReceivable(getOperMoney());
            accountOper.setAccountsReceivable(BigDecimal.ZERO);
            accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
       // }


        accountOper.calcCustomerMoney();
        //orderHome.getInstance().getAccountOpers().add(accountOper);


        orderHome.getInstance().setAdvanceMoney(
                orderHome.getInstance().getAdvanceMoney().
                        add((getOperMoney().compareTo(orderHome.getInstance().getMoney()) > 0) ? orderHome.getInstance().getMoney() : getOperMoney())
        );

        //TODO not save orderHome
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
        super.initOrderTask();
    }

}
