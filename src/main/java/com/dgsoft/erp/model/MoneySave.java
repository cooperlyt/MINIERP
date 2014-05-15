package com.dgsoft.erp.model;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.common.utils.finance.SampleCertificateItem;
import com.dgsoft.erp.model.api.PayType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-9
 * Time: 下午4:09
 */
@Entity
@Table(name = "MONEY_SAVE", catalog = "MINI_ERP")
public class MoneySave implements Serializable {

    private String id;

    private PayType payType;
    private String checkNumber;

    private BigDecimal remitFee;
    private boolean useCheck;

    private BankAccount bankAccount;
    private TransCorp transCorp;
    private SaleCertificate saleCertificate;

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

    @Column(name = "USE_CHECK", nullable = false)
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


    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROXY", nullable = true)
    public TransCorp getTransCorp() {
        return transCorp;
    }

    public void setTransCorp(TransCorp transCorp) {
        this.transCorp = transCorp;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moneySave", cascade = {CascadeType.ALL})
    public Set<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public void setAccountOpers(Set<AccountOper> accountOpers) {
        this.accountOpers = accountOpers;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinColumn(nullable = true, name = "CERTIFICATE")
    public SaleCertificate getSaleCertificate() {
        return saleCertificate;
    }

    public void setSaleCertificate(SaleCertificate saleCertificate) {
        this.saleCertificate = saleCertificate;
    }

    @Transient
    public List<CertificateItem> getCertificateItems() {
        List<CertificateItem> result = new ArrayList<CertificateItem>();

        if (!getAccountOpers().isEmpty()) {
            if (getRemitFee().compareTo(BigDecimal.ZERO) > 0){
                result.add(new SampleCertificateItem(" ",
                        RunParam.instance().getStringParamValue("erp.finance.mgrFee"), getRemitFee(), BigDecimal.ZERO));
            }
            if (getMoney().compareTo(BigDecimal.ZERO) > 0) {
                AccountOper oper = getAccountOpers().iterator().next();
                String accountCode = RunParam.instance().getStringParamValue(getPayType().equals(PayType.BANK_TRANSFER) ? "erp.finance.bankAccount" : "erp.finance.cashAccount");
                if (getPayType().equals(PayType.BANK_TRANSFER)) {
                    accountCode += getBankAccount().getId();
                }
                switch (oper.getOperType()) {

                    case DEPOSIT_BACK:
                        result.add(new SampleCertificateItem(
                                String.format(RunParam.instance().getStringParamValue("erp.ADF.s.DEPOSIT_BACK"), oper.getCustomer().getName()),
                                accountCode, BigDecimal.ZERO, getMoney()));
                        break;
                    case PROXY_SAVINGS:
                        String transCorpName = " ";
                        if (getTransCorp() != null) {
                            transCorpName = getTransCorp().getName();
                        }
                        result.add(new SampleCertificateItem(
                                String.format(RunParam.instance().getStringParamValue("erp.ADF.s.PROXY_SAVINGS"), transCorpName),
                                accountCode, getMoney(), BigDecimal.ZERO));
                        break;
                    case CUSTOMER_SAVINGS:
                        result.add(new SampleCertificateItem(
                                String.format(RunParam.instance().getStringParamValue("erp.ADF.o.CUSTOMER_SAVINGS"), oper.getCustomer().getName()),
                                accountCode, getMoney(), BigDecimal.ZERO));
                        break;
                    case DEPOSIT_PAY:
                        throw new IllegalArgumentException("DEPOSIT_PAY nukown type");
                    case MONEY_FREE:
                        throw new IllegalArgumentException("DEPOSIT_PAY nukown type");
                    case ORDER_PAY:
                        throw new IllegalArgumentException("DEPOSIT_PAY nukown type");
                    case ORDER_BACK:
                        result.add(new SampleCertificateItem(
                                String.format(RunParam.instance().getStringParamValue("erp.ADF.o.ORDER_BACK"), oper.getCustomer().getName()),
                                accountCode, BigDecimal.ZERO, getMoney()));
                        break;
                }
            }
        }
        return result;

    }
}
