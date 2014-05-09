package com.dgsoft.erp.model;

import com.dgsoft.erp.model.api.PayType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-9
 * Time: 下午4:09
 */
@Entity
@Table(name = "MONEY_SAVE", catalog = "MINI_ERP")
public class MoneySave implements Serializable{

    private String id;

    private PayType payType;
    private String checkNumber;

    private BigDecimal remitFee;
    private boolean useCheck;

    private BankAccount bankAccount;
    private TransCorp transCorp;

    private Set<AccountOper> accountOpers = new HashSet<AccountOper>(0);

    private BigDecimal money;

    public MoneySave() {
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "MONEY", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Column(name = "CHECK_NUMBER", length = 50)
    @Size(max = 50)
    public String getCheckNumber() {
        return this.checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    @Column(name="USE_CHECK",nullable = false)
    public boolean isUseCheck() {
        return useCheck;
    }

    public void setUseCheck(boolean useCheck) {
        this.useCheck = useCheck;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "NUMBER", nullable = true)
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }


    @Column(name = "REMIT_FEE", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getRemitFee() {
        return remitFee;
    }

    public void setRemitFee(BigDecimal remitFee) {
        this.remitFee = remitFee;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_TYPE", nullable = true, length = 32)
    public PayType getPayType() {
        return this.payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }


    @ManyToOne(optional = true,fetch = FetchType.LAZY)
    @JoinColumn(name = "PROXY",nullable = true)
    public TransCorp getTransCorp() {
        return transCorp;
    }

    public void setTransCorp(TransCorp transCorp) {
        this.transCorp = transCorp;
    }


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "moneySave",cascade = {CascadeType.ALL})
    public Set<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public void setAccountOpers(Set<AccountOper> accountOpers) {
        this.accountOpers = accountOpers;
    }
}
