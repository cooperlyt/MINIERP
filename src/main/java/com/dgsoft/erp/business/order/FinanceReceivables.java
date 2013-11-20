package com.dgsoft.erp.business.order;


import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import javax.faces.event.ValueChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:55 AM
 */

public abstract class FinanceReceivables extends OrderTaskHandle {

    @In(create = true)
    protected CustomerHome customerHome;

    @In
    protected Credentials credentials;

    private AccountOper debitAccountOper;


    protected abstract AccountOper.AccountOperType getAccountOperType();

    public AccountOper getDebitAccountOper() {
        return debitAccountOper;
    }

    public void setDebitAccountOper(AccountOper debitAccountOper) {
        this.debitAccountOper = debitAccountOper;
    }

    public void allMoney() {
        debitAccountOper.setOperMoney(getShortageMoney());
        checkCustomerAccountBlance();
    }

    public void payTypeChangeListener() {
        checkCustomerAccountBlance();
        debitAccountOper.setAccountingByDebitAccount(null);

    }

    public void checkCustomerAccountBlance() {
        if (debitAccountOper.getOperMoney() == null) {
            return;
        }
        if (debitAccountOper.getPayType() != null &&
                debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            if (debitAccountOper.getOperMoney().compareTo(orderHome.getInstance().getCustomer().getBalance()) > 0) {
                facesMessages.addFromResourceBundle(
                        StatusMessage.Severity.WARN, "customer_balance_not_enough",
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getCustomer().getBalance()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(debitAccountOper.getOperMoney()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(debitAccountOper.getOperMoney().subtract(orderHome.getInstance().getCustomer().getBalance())));
            }
        }
    }

    public void operMoneyValueChangeListener(ValueChangeEvent e) {
        if (debitAccountOper.getPayType() != null &&
                debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT)) {
            if (((BigDecimal) e.getNewValue()).compareTo(orderHome.getInstance().getCustomer().getBalance()) > 0) {
                facesMessages.addToControlFromResourceBundle(e.getComponent().getId(),
                        StatusMessage.Severity.WARN, "customer_balance_not_enough",
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(orderHome.getInstance().getCustomer().getBalance()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(e.getNewValue()),
                        DecimalFormat.getCurrencyInstance(Locale.CHINA).format(((BigDecimal) e.getNewValue()).subtract(orderHome.getInstance().getCustomer().getBalance())));
            }
        }
    }


    @Transactional
    public void receiveMoney() {

        boolean saving =  !debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT);

        if (saving) {
          AccountOper savingAccountOper = new AccountOper(customerHome.getInstance(),
                  credentials.getUsername(),debitAccountOper.getOperMoney(),
                  AccountOper.AccountOperType.ORDER_SAVINGS,
                  new Date(debitAccountOper.getOperDate().getTime()),
                  customerHome.getInstance().getBalance(),
                  customerHome.getInstance().getBalance().add(debitAccountOper.getOperMoney()),
                  debitAccountOper.getDescription(),debitAccountOper.getPayType(),orderHome.getInstance(),
                  debitAccountOper.getCheckNumber());

            customerHome.getInstance().getAccountOpers().add(savingAccountOper);
        }


        debitAccountOper.setOperType(getAccountOperType());
        debitAccountOper.setCustomer(customerHome.getInstance());
        debitAccountOper.setCustomerOrder(orderHome.getInstance());


        if (saving){
            debitAccountOper.setBeforMoney(customerHome.getInstance().getBalance().add(debitAccountOper.getOperMoney()));
            debitAccountOper.setAfterMoney(customerHome.getInstance().getBalance());
        }else{
            debitAccountOper.setBeforMoney(customerHome.getInstance().getBalance());
            debitAccountOper.setAfterMoney(customerHome.getInstance().getBalance().subtract(debitAccountOper.getOperMoney()));
            customerHome.getInstance().setBalance(debitAccountOper.getAfterMoney());
        }

        debitAccountOper.setPayType(PayType.FROM_PRE_DEPOSIT);
        debitAccountOper.setCheckNumber(null);
        debitAccountOper.setOperDate(new Date(debitAccountOper.getOperDate().getTime() + 1));

        customerHome.getInstance().getAccountOpers().add(debitAccountOper);

        customerHome.update();


        //orderHome.refresh();

        reset();
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
    }

    public void reset(){
        debitAccountOper = new AccountOper(PayType.BANK_TRANSFER,credentials.getUsername());
    }

    @Override
    protected String initOrderTask() {
        reset();
        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        return super.initOrderTask();
    }

}
