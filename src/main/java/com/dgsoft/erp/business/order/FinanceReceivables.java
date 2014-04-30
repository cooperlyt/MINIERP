package com.dgsoft.erp.business.order;


import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;
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
import java.util.EnumSet;
import java.util.List;
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

    protected List<PayType> allowPayTypes;

    public BigDecimal getShortageMoney() {
        return orderHome.getInstance().getShortageMoney();
    }

    protected abstract AccountOper.AccountOperType getAccountOperType();

    private Customer getCustomer() {
        return orderHome.getInstance().getCustomer();
    }

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

    public List<PayType> getAllowPayTypes() {
        return allowPayTypes;
    }

    public boolean isFreeMoney() {
        return freeMoney;
    }

    public void setFreeMoney(boolean freeMoney) {
        this.freeMoney = freeMoney;
    }

    private boolean isFreeForPay() {
        return freeMoney && debitAccountOper.getPayType().equals(PayType.ARREARS);
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


        boolean splitPay = false;

        if (debitAccountOper.getPayType().equals(PayType.ARREARS)) {
            if (!isFreeForPay() && (orderHome.getInstance().getCustomer().getBalance().compareTo(BigDecimal.ZERO) > 0)) {
                if (orderHome.getInstance().getCustomer().getBalance().compareTo(getShortageMoney()) >= 0) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "orderFreeConvertToPreparePay");
                    debitAccountOper.setPayType(PayType.FROM_PRE_DEPOSIT);
                    debitAccountOper.setOperMoney(getShortageMoney());
                } else {
                    splitPay = true;
                }
            }
            if (!splitPay) {
                debitAccountOper.setOperMoney(getShortageMoney());
            }


        }

        if ((debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT) || debitAccountOper.getPayType().equals(PayType.ARREARS)) &&
                (debitAccountOper.getOperMoney().compareTo(getShortageMoney()) > 0)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderFreeMoneyLessMoney");
            return;
        }

        if (debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT) && orderHome.getInstance().getCustomer().getBalance().compareTo(debitAccountOper.getOperMoney()) < 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "customerBalanceNotEn");
            return;
        }


        if (orderHome.getInstance().getShortageMoney().compareTo(BigDecimal.ZERO) <= 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderMoneyIsComplete");
            return;
        }

        if (splitPay) {
            debitAccountOper.setOperMoney(getShortageMoney().subtract(orderHome.getInstance().getCustomer().getBalance()));
            AccountOper splitAccountOper = new AccountOper(getCustomer(),
                    credentials.getUsername(), getCustomer().getBalance(),
                    AccountOper.AccountOperType.ORDER_PAY,
                    new Date(debitAccountOper.getOperDate().getTime()),
                    debitAccountOper.getDescription(), PayType.FROM_PRE_DEPOSIT, orderHome.getInstance(),
                    debitAccountOper.getCheckNumber(), BigDecimal.ZERO);
            debitAccountOper.setOperDate(new Date(debitAccountOper.getOperDate().getTime() + 1001));

            orderHome.getInstance().getAccountOpers().add(splitAccountOper);
        }

        boolean saving = !debitAccountOper.getPayType().equals(PayType.FROM_PRE_DEPOSIT) && !debitAccountOper.getPayType().equals(PayType.ARREARS);
        //if (debitAccountOper.getPayType().equals(PayType.BANK_TRANSFER)) {
        //    debitAccountOper.setOperMoney(debitAccountOper.getOperMoney().add(debitAccountOper.getRemitFee()));
        //}


        if (saving || isFreeForPay()) {
            AccountOper savingAccountOper = new AccountOper(getCustomer(),
                    credentials.getUsername(), debitAccountOper.getOperMoney(),
                    isFreeForPay() ? AccountOper.AccountOperType.ORDER_FREE : AccountOper.AccountOperType.ORDER_SAVINGS,
                    isFreeForPay() ? new Date(debitAccountOper.getOperDate().getTime() + 1001) :
                            new Date(debitAccountOper.getOperDate().getTime()),
                    debitAccountOper.getDescription(), debitAccountOper.getPayType(), orderHome.getInstance(),
                    debitAccountOper.getCheckNumber(), debitAccountOper.getRemitFee());
            if (debitAccountOper.getPayType().equals(PayType.BANK_TRANSFER) || orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                savingAccountOper.setBankAccount(debitAccountOper.getBankAccount());
                debitAccountOper.setBankAccount(null);
            }

            orderHome.getInstance().getAccountOpers().add(savingAccountOper);
        }


        debitAccountOper.setOperType(getAccountOperType());
        debitAccountOper.setCustomer(getCustomer());
        debitAccountOper.setCustomerOrder(orderHome.getInstance());


        if (saving) {

            if (debitAccountOper.getOperMoney().compareTo(orderHome.getInstance().getShortageMoney()) > 0) {
                BigDecimal saveMoney = debitAccountOper.getOperMoney().subtract(orderHome.getInstance().getShortageMoney());
                debitAccountOper.setOperMoney(orderHome.getInstance().getShortageMoney());
                getCustomer().setBalance(getCustomer().getBalance().add(saveMoney));

                orderHome.getInstance().getAccountOpers().add(new AccountOper(getCustomer(), credentials.getUsername(), saveMoney,
                        AccountOper.AccountOperType.PRE_DEPOSIT_BY_ORDER, new Date(debitAccountOper.getOperDate().getTime() + 1002),
                        "", debitAccountOper.getPayType(), orderHome.getInstance(), debitAccountOper.getCheckNumber(), BigDecimal.ZERO));
            }

        } else {
            if (!isFreeForPay()) {

                if (splitPay){
                    getCustomer().setBalance(debitAccountOper.getOperMoney());
                }else{
                    getCustomer().setBalance(getCustomer().getBalance().subtract(debitAccountOper.getOperMoney()));
                }
            }
        }

        //debitAccountOper.setPayType(PayType.FROM_PRE_DEPOSIT);
        debitAccountOper.setRemitFee(BigDecimal.ZERO);
        //debitAccountOper.setCheckNumber(null);
        if (!isFreeForPay())
            debitAccountOper.setOperDate(new Date(debitAccountOper.getOperDate().getTime() + 1001));


        orderHome.getInstance().getAccountOpers().add(debitAccountOper);

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
