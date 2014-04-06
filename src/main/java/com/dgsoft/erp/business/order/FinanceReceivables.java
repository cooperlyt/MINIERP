package com.dgsoft.erp.business.order;


import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
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

    private boolean freeMoney;

    public BigDecimal getShortageMoney() {
        return orderHome.getInstance().getShortageMoney();
    }

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

    public boolean isFreeMoney() {
        return freeMoney;
    }

    public void setFreeMoney(boolean freeMoney) {
        this.freeMoney = freeMoney;
    }

    private boolean isFreeForPay() {
        return freeMoney && debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT);
    }

    public void checkCustomerAccountBlance() {
        if (debitAccountOper.getOperMoney() == null) {
            return;
        }
        if (!isFreeForPay() && debitAccountOper.getPayType() != null &&
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

        if (debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT) && (debitAccountOper.getOperMoney().compareTo(getShortageMoney()) > 0)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderFreeMoneyLessMoney");
            return;
        }

        boolean saving = !debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT);
        //if (debitAccountOper.getPayType().equals(PayType.BANK_TRANSFER)) {
        //    debitAccountOper.setOperMoney(debitAccountOper.getOperMoney().add(debitAccountOper.getRemitFee()));
        //}

        if (saving || isFreeForPay()) {
            AccountOper savingAccountOper = new AccountOper(customerHome.getInstance(),
                    credentials.getUsername(), debitAccountOper.getOperMoney(),
                    isFreeForPay() ? AccountOper.AccountOperType.ORDER_FREE : AccountOper.AccountOperType.ORDER_SAVINGS,
                    isFreeForPay() ? new Date(debitAccountOper.getOperDate().getTime() + 1001) :
                            new Date(debitAccountOper.getOperDate().getTime()),
                    debitAccountOper.getDescription(), debitAccountOper.getPayType(), orderHome.getInstance(),
                    debitAccountOper.getCheckNumber());
            if (debitAccountOper.getPayType().equals(PayType.BANK_TRANSFER) || orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                savingAccountOper.setRemitFee(debitAccountOper.getRemitFee());
                savingAccountOper.setBankAccount(debitAccountOper.getBankAccount());
                debitAccountOper.setBankAccount(null);
            } else {
                savingAccountOper.setRemitFee(BigDecimal.ZERO);
            }


            customerHome.getInstance().getAccountOpers().add(savingAccountOper);
        }


        debitAccountOper.setOperType(getAccountOperType());
        debitAccountOper.setCustomer(customerHome.getInstance());
        debitAccountOper.setCustomerOrder(orderHome.getInstance());


        if (saving) {

            if (debitAccountOper.getOperMoney().compareTo(orderHome.getInstance().getShortageMoney()) > 0) {
                BigDecimal saveMoney = debitAccountOper.getOperMoney().subtract(orderHome.getInstance().getShortageMoney());
                debitAccountOper.setOperMoney(orderHome.getInstance().getShortageMoney());
                customerHome.getInstance().setBalance(customerHome.getInstance().getBalance().add(saveMoney));
            }

        } else {
            if (!isFreeForPay())
                customerHome.getInstance().setBalance(customerHome.getInstance().getBalance().subtract(debitAccountOper.getOperMoney()));
        }

        //debitAccountOper.setPayType(PayType.FROM_PRE_DEPOSIT);
        debitAccountOper.setRemitFee(BigDecimal.ZERO);
        //debitAccountOper.setCheckNumber(null);
        if (!isFreeForPay())
            debitAccountOper.setOperDate(new Date(debitAccountOper.getOperDate().getTime() + 1001));


        customerHome.getInstance().getAccountOpers().add(debitAccountOper);

        orderHome.calcMoneys();
        orderHome.update();


        //orderHome.refresh();

        reset();
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
    }

    public void reset() {
        debitAccountOper = new AccountOper(PayType.BANK_TRANSFER, credentials.getUsername());
        debitAccountOper.setRemitFee(BigDecimal.ZERO);
        freeMoney = false;
    }

    @Override
    protected void initOrderTask() {
        reset();
        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        super.initOrderTask();
    }

}
