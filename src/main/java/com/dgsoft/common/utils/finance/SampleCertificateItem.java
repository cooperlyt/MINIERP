package com.dgsoft.common.utils.finance;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-14
 * Time: 上午9:34
 */
public class SampleCertificateItem implements CertificateItem {

    private String description;

    private String accountCode;

    private BigDecimal debit;

    private BigDecimal credit;

    public SampleCertificateItem() {
    }

    public SampleCertificateItem(String description, String accountCode, BigDecimal debit, BigDecimal credit) {
        this.description = description;
        this.accountCode = accountCode;
        this.debit = debit;
        this.credit = credit;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    @Override
    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }
}
