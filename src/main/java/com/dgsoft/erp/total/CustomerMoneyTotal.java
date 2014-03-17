package com.dgsoft.erp.total;

import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by cooper on 3/16/14.
 */
@Name("customerMoneyTotal")
public class CustomerMoneyTotal extends CustomerMoneyTotalBase {


    protected static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyTotal.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerMoneyTotal.searchDateArea.searchDateTo}",
            "accountOper.operType = #{customerMoneyTotal.accountOperType}",
            "accountOper.operType in (#{customerMoneyTotal.showTypes})",
            "accountOper.customer.customerArea.id = #{customerMoneyTotal.sellAreaId} ",
            "lower(accountOper.customer.name) like lower(concat(#{customerMoneyTotal.customerName},'%'))" };


    public CustomerMoneyTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    private String customerName;

    private String sellAreaId;


    private AccountOper.AccountOperType accountOperType;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSellAreaId() {
        return sellAreaId;
    }

    public void setSellAreaId(String sellAreaId) {
        this.sellAreaId = sellAreaId;
    }

    public AccountOper.AccountOperType getAccountOperType() {
        return accountOperType;
    }

    public void setAccountOperType(AccountOper.AccountOperType accountOperType) {
        this.accountOperType = accountOperType;
    }


    public List<AccountOper.AccountOperType> getShowTypes() {
        return new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.ORDER_SAVINGS,
                AccountOper.AccountOperType.PRE_DEPOSIT,
                AccountOper.AccountOperType.DEPOSIT_BACK,
                AccountOper.AccountOperType.ORDER_FREE,
                AccountOper.AccountOperType.ORDER_BACK));
    }
}
