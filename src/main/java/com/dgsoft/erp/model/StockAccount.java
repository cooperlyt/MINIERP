package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by cooper on 6/2/14.
 */
@Entity
@Table(name = "STOCK_ACCOUNT", catalog = "MINI_ERP")
public class StockAccount implements Serializable{

    private String id;
    private BigDecimal beginCount;
    private BigDecimal closeCount;
    private Checkout checkout;
    private Stock stock;

    public StockAccount() {
    }

    public StockAccount(BigDecimal beginCount, Checkout checkout, Stock stock) {
        this.beginCount = beginCount;
        this.checkout = checkout;
        this.stock = stock;
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

    @Column(name="BEGIN_COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getBeginCount() {
        return beginCount;
    }

    public void setBeginCount(BigDecimal beginCount) {
        this.beginCount = beginCount;
    }

    @Column(name="CLOSE_COUNT",nullable = false,scale = 4)
    @NotNull
    public BigDecimal getCloseCount() {
        return closeCount;
    }

    public void setCloseCount(BigDecimal closeCount) {
        this.closeCount = closeCount;
    }

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECKOUT",nullable = false)
    @NotNull
    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="STOCK",nullable = false)
    @NotNull
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
