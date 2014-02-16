package com.dgsoft.erp.model;

import com.dgsoft.erp.model.api.AccountDetailsCheckout;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/16/14
 * Time: 9:09 PM
 */

@Entity
@Table(name = "CUSTOMER_ACCOUNT_CHECK_OUT", catalog = "MINI_ERP", uniqueConstraints = @UniqueConstraint(columnNames = {
        "CHECKOUT", "CUSTOMER"}))
public class CustomerDetailsCheckout implements AccountDetailsCheckout, java.io.Serializable {

    private String id;

    private AccountCheckout accountCheckout;

    private BigDecimal beginningBalance;

    private BigDecimal closingBalance;

    private BigDecimal debitMoney;

    private BigDecimal creditMoney;

    private Customer customer;

    public CustomerDetailsCheckout() {
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

    @Override
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "CHECKOUT", nullable = false)
    @NotNull
    public AccountCheckout getAccountCheckout() {
        return accountCheckout;
    }

    public void setAccountCheckout(AccountCheckout accountCheckout) {
        this.accountCheckout = accountCheckout;
    }

    @Override
    @Column(name = "BEGINNING_BALANCE", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(BigDecimal beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    @Override
    @Column(name = "CLOSING_BALANCE", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Override
    @Column(name = "DEBIT_MONEY", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getDebitMoney() {
        return debitMoney;
    }

    public void setDebitMoney(BigDecimal debitMoney) {
        this.debitMoney = debitMoney;
    }

    @Override
    @Column(name = "CREDIT_MONEY", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCreditMoney() {
        return creditMoney;
    }

    public void setCreditMoney(BigDecimal creditMoney) {
        this.creditMoney = creditMoney;
    }


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER", nullable = false)
    @NotNull
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
