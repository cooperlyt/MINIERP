package com.dgsoft.erp.model;

import com.dgsoft.common.utils.finance.Certificate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-21
 * Time: 下午5:10
 */
@Entity
@Table(name = "CERTIFICATE_ITEM", catalog = "MINI_ERP")
public class SaleCertificateItem implements Serializable, com.dgsoft.common.utils.finance.CertificateItem {

    private String id;

    private String description;

    private String accountCode;

    private BigDecimal debit;

    private BigDecimal credit;

    private SaleCertificate saleCertificate;

    public SaleCertificateItem() {
    }

    public SaleCertificateItem(SaleCertificate saleCertificate, String description, String accountCode, BigDecimal debit, BigDecimal credit) {
        this.description = description;
        this.accountCode = accountCode;
        this.debit = debit;
        this.credit = credit;
        this.saleCertificate = saleCertificate;
    }

    @Override
    @Column(name = "DESCRIPTION", nullable = true, length = 200)
    @Size(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @Column(name = "ACCOUNT_CODE", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    @Column(name = "DEBIT_MONEY", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    @Override
    @Column(name = "CREDIT_MONEY", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CERTIFICATE", nullable = false)
    @NotNull
    public SaleCertificate getSaleCertificate() {
        return saleCertificate;
    }

    public void setSaleCertificate(SaleCertificate saleCertificate) {
        this.saleCertificate = saleCertificate;
    }

    @Override
    @Transient
    public Certificate getCertificate() {
        return getSaleCertificate();
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @NotNull
    @Size(max = 32)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
