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
@BypassInterceptors
public class CustomerMoneyCondition {

    private boolean containDepositBack =true;// DEPOSIT_BACK(false),

    private boolean containProxySavings = true;// PROXY_SAVINGS(true),
    private boolean containCustomerSavings = true;// CUSTOMER_SAVINGS(true),
    private boolean containDepositPay = true;// DEPOSIT_PAY(null),
    private boolean containMoneyFree = true;// containMoneyFree(null),
    private boolean containOrderPay= true;// ORDER_PAY(null),

    private boolean containOrderBack = true;// ORDER_BACK(null);






    @BypassInterceptors
    public List<AccountOper.AccountOperType> getSearchAccountOperTypes() {
        List<AccountOper.AccountOperType> result = new ArrayList<AccountOper.AccountOperType>();
        if (containDepositBack) {
            result.add(AccountOper.AccountOperType.DEPOSIT_BACK);
        }
        if (containProxySavings) {
            result.add(AccountOper.AccountOperType.PROXY_SAVINGS);
        }
        if (containCustomerSavings) {
            result.add(AccountOper.AccountOperType.CUSTOMER_SAVINGS);
        }
        if (containDepositPay) {
            result.add(AccountOper.AccountOperType.DEPOSIT_BACK);
        }
        if (containMoneyFree) {
            result.add(AccountOper.AccountOperType.MONEY_FREE);
        }
        if (containOrderPay){
            result.add(AccountOper.AccountOperType.ORDER_PAY);
        }
        if (containOrderBack){
            result.add(AccountOper.AccountOperType.ORDER_BACK);
        }



        return result;
    }

    public boolean isContainOrderPay() {
        return containOrderPay;
    }

    public void setContainOrderPay(boolean containOrderPay) {
        this.containOrderPay = containOrderPay;
    }


    public boolean isContainCustomerSavings() {
        return containCustomerSavings;
    }

    public void setContainCustomerSavings(boolean containCustomerSavings) {
        this.containCustomerSavings = containCustomerSavings;
    }

    public boolean isContainDepositBack() {
        return containDepositBack;
    }

    public void setContainDepositBack(boolean containDepositBack) {
        this.containDepositBack = containDepositBack;
    }

    public boolean isContainProxySavings() {
        return containProxySavings;
    }

    public void setContainProxySavings(boolean containProxySavings) {
        this.containProxySavings = containProxySavings;
    }

    public boolean isContainDepositPay() {
        return containDepositPay;
    }

    public void setContainDepositPay(boolean containDepositPay) {
        this.containDepositPay = containDepositPay;
    }

    public boolean isContainMoneyFree() {
        return containMoneyFree;
    }

    public void setContainMoneyFree(boolean containMoneyFree) {
        this.containMoneyFree = containMoneyFree;
    }

    public boolean isContainOrderBack() {
        return containOrderBack;
    }

    public void setContainOrderBack(boolean containOrderBack) {
        this.containOrderBack = containOrderBack;
    }
}
