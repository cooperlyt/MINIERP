package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.AccountCheckout;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/16/14
 * Time: 9:11 PM
 */
public interface AccountDetailsCheckout{

    public AccountCheckout getAccountCheckout();

    public BigDecimal getBeginningBalance();

    public BigDecimal getClosingBalance();

    public BigDecimal getDebitMoney();

    public BigDecimal getCreditMoney();

}
