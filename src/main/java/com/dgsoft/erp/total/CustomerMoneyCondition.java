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

    private boolean containCancelBack = true;

    private boolean containOrderFee = false;


    @BypassInterceptors
    public List<AccountOper.AccountOperType> getSearchAccountOperTypes(){
        List<AccountOper.AccountOperType> result = new ArrayList<AccountOper.AccountOperType>();
        if (containOrderSavings){
            result.add(AccountOper.AccountOperType.ORDER_SAVINGS);
        }
        if (containPreDeposit){
            result.add(AccountOper.AccountOperType.PRE_DEPOSIT);
        }
        if (containBack){
            result.add(AccountOper.AccountOperType.ORDER_BACK);
        }
        if (containBackPre){
            result.add(AccountOper.AccountOperType.DEPOSIT_BACK);
        }
        if (containCancelBack){
            result.add(AccountOper.AccountOperType.ORDER_CANCEL_BACK);
        }
        if (containOrderFee){
            result.add(AccountOper.AccountOperType.ORDER_FREE);
        }
        if (result.isEmpty())
            result.addAll(AccountOper.AccountOperType.allCustomerOper());
        return result;
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
    public boolean isContainCancelBack() {
        return containCancelBack;
    }

    public void setContainCancelBack(boolean containCancelBack) {
        this.containCancelBack = containCancelBack;
    }

    @BypassInterceptors
    public boolean isContainOrderFee() {
        return containOrderFee;
    }

    public void setContainOrderFee(boolean containOrderFee) {
        this.containOrderFee = containOrderFee;
    }


}
