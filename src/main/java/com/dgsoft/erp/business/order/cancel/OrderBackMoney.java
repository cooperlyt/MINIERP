package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.MoneySave;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.text.DateFormat;

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

    private boolean backToPreMoney = false;

    private MoneySave moneySave;

    public boolean isBackToPreMoney() {
        return backToPreMoney;
    }

    public void setBackToPreMoney(boolean backToPreMoney) {
        this.backToPreMoney = backToPreMoney;
    }

    public MoneySave getMoneySave() {
        return moneySave;
    }


    @Override
    protected void initCancelOrderTask() {
        moneySave = new MoneySave();
        customerOper = new AccountOper(AccountOper.AccountOperType.ORDER_BACK,credentials.getUsername());
        customerOper.setCustomer(orderBackHome.getInstance().getCustomer());
        customerOper.setOrderBack(orderBackHome.getInstance());
    }

    public AccountOper getCustomerOper() {
        return customerOper;
    }

    public boolean isCanBackMoneyToProxyAccount(){
        return orderBackHome.getInstance().getCustomer().getProxyAccountMoney().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getMaxProxyMoney(){
        return (orderBackHome.getInstance().getCustomer().getProxyAccountMoney().compareTo(orderBackHome.getInstance().getMoney()) > 0) ? orderBackHome.getInstance().getMoney() : orderBackHome.getInstance().getCustomer().getProxyAccountMoney();
    }

    @Override
    protected String completeOrderTask() {

        if (customerOper.getOperDate().compareTo(DataFormat.getTodayLastTime()) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "DateIsFuture", DateFormat.getDateInstance(DateFormat.MEDIUM).format(customerOper.getOperDate()));
            return null;
        }

        if (customerOper.getOperDate().compareTo(DataFormat.halfTime(orderBackHome.getInstance().getCreateDate())) < 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "DateIsFuture",
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(customerOper.getOperDate()),
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(orderBackHome.getInstance().getCreateDate()));
            return null;
        }
        customerOper.setAdvanceReceivable(BigDecimal.ZERO);
        if (backToPreMoney){

            if (isCanBackMoneyToProxyAccount()){

                if (customerOper.getAccountsReceivable().add(customerOper.getProxcAccountsReceiveable()).compareTo(orderBackHome.getInstance().getMoney()) != 0){
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "backMoneyAccountMoneyError");

                    return null;
                }

                if (customerOper.getProxcAccountsReceiveable().compareTo(orderBackHome.getInstance().getCustomer().getProxyAccountMoney()) > 0){
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "backMoneyProxyAccountMoneyError");

                    return null;
                }




            }else{
                customerOper.setAccountsReceivable(orderBackHome.getInstance().getMoney());
                customerOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
            }




            customerOper.setMoneySave(null);
        }else{
            customerOper.setAccountsReceivable(orderBackHome.getInstance().getMoney());

            customerOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
            moneySave.setMoney(orderBackHome.getInstance().getMoney());
            moneySave.getAccountOpers().clear();
            moneySave.getAccountOpers().add(customerOper);
            customerOper.setMoneySave(moneySave);
        }
        customerOper.calcCustomerMoney();
        orderBackHome.getInstance().getAccountOpers().add(customerOper);
        orderBackHome.getInstance().setMoneyComplete(true);
        if ("updated".equals(orderBackHome.update())) {
            Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess.AccountOper");
            return super.completeOrderTask();
        } else
            return null;
    }

}
