package com.dgsoft.common.utils.finance;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-19
 * Time: 下午4:26
 */
public class AccountDetailsItem {

    private CertificateItem certificateItem;

    private AccountDetailsItem parent;

    private BigDecimal afterMoney = null;
    private BigDecimal balance = null;

    private String description;

    private BigDecimal debit = null;

    private BigDecimal credit = null;

    private Account.Direction balanceDir;

    private Date itemTime;

    public AccountDetailsItem(CertificateItem certificateItem,
                              AccountDetailsItem parent, Account.Direction accountDirection) {
        this.certificateItem = certificateItem;
        this.parent = parent;
        afterMoney = parent.getAfterMoney();
        if (accountDirection.equals(Account.Direction.DBEDIT)) {
            afterMoney = afterMoney.add(certificateItem.getDebit()).subtract(certificateItem.getCredit());
        } else {
            afterMoney = afterMoney.add(certificateItem.getCredit()).subtract(certificateItem.getDebit());
        }
        initBalance(accountDirection);
    }

    public AccountDetailsItem(String description, BigDecimal afterMoney, Account.Direction accountDirection, Date itemTime) {
        this.description = description;
        this.afterMoney = afterMoney;
        this.itemTime = itemTime;
        initBalance(accountDirection);
    }

    public AccountDetailsItem(String description, BigDecimal debit, BigDecimal credit,
                              BigDecimal afterMoney, Account.Direction accountDirection, Date itemTime) {
        this.description = description;
        this.afterMoney = afterMoney;
        this.debit = debit;
        this.credit = credit;
        this.itemTime = itemTime;
        initBalance(accountDirection);
    }

    private void initBalance(Account.Direction accountDirection) {
        if (afterMoney.compareTo(BigDecimal.ZERO) < 0) {
            balanceDir = accountDirection.reverse();
            balance = afterMoney.abs();
        } else {
            balance = afterMoney;
            balanceDir = accountDirection;
        }
    }

    public BigDecimal getDebit() {
        if (certificateItem != null) {
            return certificateItem.getDebit();
        } else {
            return debit;
        }
    }

    public BigDecimal getCredit() {
        if (certificateItem != null) {
            return certificateItem.getCredit();
        } else return credit;
    }

    public String getDescription() {
        if (certificateItem != null) {
            return certificateItem.getDescription();
        } else return description;
    }

    public Date getItemTime() {
        if (certificateItem != null) {
            return certificateItem.getCertificate().getDate();
        } else
            return itemTime;
    }

    public int getMonth(){
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(getItemTime());
        return c.get(Calendar.MONTH);
    }

    public int getDay(){
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(getItemTime());
        return c.get(Calendar.DATE);
    }

    public String getCertificateWord(){
        if (certificateItem != null) {
            return certificateItem.getCertificate().getWord();
        }
        return null;
    }

    public Integer getCertificateCode(){
        if (certificateItem != null) {
            return certificateItem.getCertificate().getCode();
        }
        return null;
    }

    public String getCertificateId(){
        if (certificateItem != null) {
            return certificateItem.getCertificate().getId();
        }
        return null;
    }

    public AccountDetailsItem getParent() {
        return parent;
    }

    private BigDecimal getAfterMoney() {
        return afterMoney;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Account.Direction getBalanceDir() {
        return balanceDir;
    }
}
