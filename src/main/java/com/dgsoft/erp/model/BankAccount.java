package com.dgsoft.erp.model;

import com.dgsoft.common.NamedEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cooper on 3/1/14.
 */
@Entity
@Table(name = "BANK_ACCOUNT", catalog = "MINI_ERP")
public class BankAccount implements java.io.Serializable, NamedEntity {

    private String number;
    private String bank;
    private String memo;
    private String openBank;
    private String accountOwner;

    private boolean enable;

    private Set<AccountOper> accountOpers = new HashSet<AccountOper>(0);

    public BankAccount() {
    }

    public BankAccount(boolean enable) {
        this.enable = enable;
    }

    @Id
    @Column(name = "NUMBER", unique = true, nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "BANK", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Column(name = "MEMO", nullable = true, length = 200)
    @Size(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @OneToMany(orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "bankAccount")
    public Set<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public void setAccountOpers(Set<AccountOper> accountOpers) {
        this.accountOpers = accountOpers;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(name="OPEN_BANK", nullable = true, length = 200)
    @Size(max = 200)
    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    @Column(name="ACCOUNT_OWNER",nullable = true,length = 50)
    @Size(max = 50)
    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    @Override
    @Transient
    public String getName() {
        return getNumber();
    }

    @Override
    @Transient
    public String getId(){
        return getNumber();
    }
}
