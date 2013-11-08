package com.dgsoft.erp.business.order;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.AccountOperHome;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Accounting;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import javax.faces.event.ValueChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:55 AM
 */

public abstract class FinanceReceivables extends OrderTaskHandle {

    @In(create = true)
    protected AccountOperHome accountOperHome;

    @In(create = true)
    protected CustomerHome customerHome;

    protected abstract AccountOper.AccountOperType getAccountOperType();

    public abstract BigDecimal getShortageMoney();

    public void allMoney() {
        accountOperHome.getInstance().setOperMoney(getShortageMoney());
        checkCustomerAccountBlance();
    }

    public void payTypeChangeListener(){
        checkCustomerAccountBlance();
        accountOperHome.payTypeChangeListener();
    }

    public void checkCustomerAccountBlance(){
        if (accountOperHome.getInstance().getOperMoney() == null){
            return;
        }
        if (accountOperHome.getInstance().getPayType() != null &&
                accountOperHome.getInstance().getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            if (accountOperHome.getInstance().getOperMoney().compareTo(orderHome.getInstance().getCustomer().getBalance()) > 0) {
                facesMessages.addFromResourceBundle(
                        StatusMessage.Severity.WARN,"customer_balance_not_enough",
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getCustomer().getBalance()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(accountOperHome.getInstance().getOperMoney()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(accountOperHome.getInstance().getOperMoney().subtract(orderHome.getInstance().getCustomer().getBalance())));
            }
        }
    }

    public void operMoneyValueChangeListener(ValueChangeEvent e) {
        if (accountOperHome.getInstance().getPayType() != null &&
                accountOperHome.getInstance().getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            if (((BigDecimal)e.getNewValue()).compareTo(orderHome.getInstance().getCustomer().getBalance()) > 0) {
                facesMessages.addToControlFromResourceBundle(e.getComponent().getId(),
                        StatusMessage.Severity.WARN,"customer_balance_not_enough",
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getCustomer().getBalance()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(e.getNewValue()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(((BigDecimal)e.getNewValue()).subtract(orderHome.getInstance().getCustomer().getBalance())));
            }
        }
    }

    public BigDecimal getTotalReveiveMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper oper : orderHome.getInstance().getAccountOpers()) {
            result = result.add(oper.getOperMoney());
        }
        return result;
    }

    public void receiveMoney() {


        accountOperHome.getInstance().setOperType(getAccountOperType());
        accountOperHome.getInstance().setCustomer(orderHome.getInstance().getCustomer());
        accountOperHome.getInstance().setCustomerOrder(orderHome.getInstance());

        if (accountOperHome.getInstance().getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            accountOperHome.getInstance().setBeforMoney(accountOperHome.getInstance().getCustomer().getBalance());
            accountOperHome.getInstance().getCustomer().setBalance(accountOperHome.getInstance().getCustomer().getBalance().subtract(accountOperHome.getInstance().getOperMoney()));
            accountOperHome.getInstance().setAfterMoney(accountOperHome.getInstance().getCustomer().getBalance());
        } else {
            accountOperHome.getInstance().setBeforMoney(accountOperHome.getInstance().getCustomer().getBalance());
            accountOperHome.getInstance().setAfterMoney(accountOperHome.getInstance().getCustomer().getBalance());
        }


        accountOperHome.persist();
        accountOperHome.clearInstance();
        orderHome.refresh();

    }

    @Override
    protected String initOrderTask() {
        accountOperHome.clearInstance();
        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        return super.initOrderTask();
    }

}
