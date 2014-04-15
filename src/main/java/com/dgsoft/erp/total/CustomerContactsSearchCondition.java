package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 15/04/14
 * Time: 09:08
 */
@Name("customerContactsSearchCondition")
@BypassInterceptors
public class CustomerContactsSearchCondition {

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    private Boolean showMoney = null;

    //  money type
    private boolean containOrderSavings = true;

    private boolean containPreDeposit = true;

    private boolean containBackPre = true;

    private boolean containBack = true;

    private boolean containCancelBack = true;

    private boolean containOrderFee = false;

    // res type
    private boolean containStoreOut = true;

    private boolean containResBack = true;

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
    public Boolean getShowMoney() {
        return showMoney;
    }

    public void setShowMoney(Boolean showMoney) {
        this.showMoney = showMoney;
    }

    @BypassInterceptors
    public boolean isContainStoreOut() {
        return containStoreOut;
    }

    public void setContainStoreOut(boolean containStoreOut) {
        this.containStoreOut = containStoreOut;
    }

    @BypassInterceptors
    public boolean isContainResBack() {
        return containResBack;
    }

    public void setContainResBack(boolean containResBack) {
        this.containResBack = containResBack;
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

    @BypassInterceptors
    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

}
