package com.dgsoft.common.utils.finance;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-19
 * Time: 下午4:26
 */
public class AccountDetailsItem {

    private CertificateItem certificateItem;

    private AccountDetailsItem parent;

    private BigDecimal afterMoney;

    public AccountDetailsItem(CertificateItem certificateItem,
                              AccountDetailsItem parent) {
        this.certificateItem = certificateItem;
        this.parent = parent;
       //TODO afterMoney = parent.getAfterMoney();
    }

    private BigDecimal getDebit(){
        return certificateItem.getDebit();
    }

    private BigDecimal getCredit(){
        return certificateItem.getCredit();
    }

    public String getDescription(){
        return certificateItem.getDescription();
    }

    public AccountDetailsItem getParent() {
        return parent;
    }

    public BigDecimal getAfterMoney() {
        return afterMoney;
    }
}
