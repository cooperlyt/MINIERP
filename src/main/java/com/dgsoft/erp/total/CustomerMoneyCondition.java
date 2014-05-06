package com.dgsoft.erp.total;

import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 4/16/14.
 */
@Name("customerMoneyCondition")
public class CustomerMoneyCondition {

    private boolean containOrderSavings = true;

    private boolean containPreDeposit = true;

    private boolean containBackPre = true;

    private boolean containBack = true;

    private boolean containCancel = true;

    private boolean containOrderFree = false;

    private boolean containOrderPay = false;

    private boolean containMoneyBackToCustomer= false;

    private boolean containMoneyBackToPrepare = false;



    @BypassInterceptors
    public List<AccountOper.AccountOperType> getSearchAccountOperTypes() {
        List<AccountOper.AccountOperType> result = new ArrayList<AccountOper.AccountOperType>();
        if (containOrderSavings) {
            result.add(AccountOper.AccountOperType.ORDER_SAVINGS);
        }
        if (containPreDeposit) {
            result.add(AccountOper.AccountOperType.PRE_DEPOSIT);
        }
        if (containBack) {
            result.add(AccountOper.AccountOperType.ORDER_BACK);
        }
        if (containBackPre) {
            result.add(AccountOper.AccountOperType.DEPOSIT_BACK);
        }
        if (containCancel) {
            result.add(AccountOper.AccountOperType.ORDER_CANCELED);
        }
        if (containOrderFree) {
            result.add(AccountOper.AccountOperType.ORDER_FREE);
        }
        if (containOrderPay){
            result.add(AccountOper.AccountOperType.ORDER_PAY);
        }
        if (containMoneyBackToCustomer){
            result.add(AccountOper.AccountOperType.MONEY_BACK_TO_CUSTOMER);
        }
        if (containMoneyBackToPrepare){
            result.add(AccountOper.AccountOperType.MONEY_BACK_TO_PREPARE);
        }


        return result;
    }

    @BypassInterceptors
    public boolean isContainOrderPay() {
        return containOrderPay;
    }

    public void setContainOrderPay(boolean containOrderPay) {
        this.containOrderPay = containOrderPay;
    }


    @BypassInterceptors
    public boolean isContainBackPre() {
        return containBackPre;
    }

    public void setContainBackPre(boolean containBackPre) {
        this.containBackPre = containBackPre;
    }

    @BypassInterceptors
    public boolean isContainOrderSavings() {
        return containOrderSavings;
    }

    public void setContainOrderSavings(boolean containOrderSavings) {
        this.containOrderSavings = containOrderSavings;
    }

    @BypassInterceptors
    public boolean isContainPreDeposit() {
        return containPreDeposit;
    }

    public void setContainPreDeposit(boolean containPreDeposit) {
        this.containPreDeposit = containPreDeposit;
    }

    @BypassInterceptors
    public boolean isContainBack() {
        return containBack;
    }

    public void setContainBack(boolean containBack) {
        this.containBack = containBack;
    }

    @BypassInterceptors
    public boolean isContainCancel() {
        return containCancel;
    }

    public void setContainCancel(boolean containCancel) {
        this.containCancel = containCancel;
    }

    @BypassInterceptors
    public boolean isContainOrderFree() {
        return containOrderFree;
    }

    public void setContainOrderFree(boolean containOrderFree) {
        this.containOrderFree = containOrderFree;
    }

    @BypassInterceptors
    public boolean isContainMoneyBackToCustomer() {
        return containMoneyBackToCustomer;
    }

    public void setContainMoneyBackToCustomer(boolean containMoneyBackToCustomer) {
        this.containMoneyBackToCustomer = containMoneyBackToCustomer;
    }

    @BypassInterceptors
    public boolean isContainMoneyBackToPrepare() {
        return containMoneyBackToPrepare;
    }

    public void setContainMoneyBackToPrepare(boolean containMoneyBackToPrepare) {
        this.containMoneyBackToPrepare = containMoneyBackToPrepare;
    }
}
