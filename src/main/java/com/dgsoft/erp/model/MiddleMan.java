package com.dgsoft.erp.model;
// Generated Oct 17, 2013 5:33:51 PM by Hibernate Tools 4.0.0

import com.dgsoft.common.NamedEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * MiddleMan generated by hbm2java
 */
@Entity
@Table(name = "MIDDLE_MAN", catalog = "MINI_ERP")
public class MiddleMan implements java.io.Serializable, NamedEntity {

    private String id;
    private String name;
    private String contact;
    private String type;
    private String memo;
    private String bankNumber;
    private String bank;
    private String tel;
    private String bankInfo;
    private boolean enable;
    private RebateProgram rebateProgram;

    private Set<Customer> customers = new HashSet<Customer>(0);
    private Set<MiddleMoneyPay> middleMoneys = new HashSet<MiddleMoneyPay>(0);

    public MiddleMan() {
    }

    public MiddleMan(boolean enable) {
        this.enable = enable;
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

    @Column(name = "NAME", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CONTACT", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Column(name = "TYPE", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "MEMO", length = 200)
    @Size(max = 200)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "BANK_NUMBER", length = 50)
    @Size(max = 50)
    public String getBankNumber() {
        return this.bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    @Column(name = "TEL", length = 50)
    @Size(max = 50)
    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "middleMan")
    public Set<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "middleMan")
    public Set<MiddleMoneyPay> getMiddleMoneys() {
        return this.middleMoneys;
    }

    public void setMiddleMoneys(Set<MiddleMoneyPay> middleMoneys) {
        this.middleMoneys = middleMoneys;
    }

    @Column(name = "BANK", length = 32)
    @Size(max = 32)
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Column(name = "BANK_INFO", length = 100)
    @Size(max = 100)
    public String getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "REBATE_PROGRAM", nullable = true)
    public RebateProgram getRebateProgram() {
        return rebateProgram;
    }

    public void setRebateProgram(RebateProgram rebateProgram) {
        this.rebateProgram = rebateProgram;
    }
}
