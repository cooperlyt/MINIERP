package com.dgsoft.erp.model;
// Generated Oct 17, 2013 5:33:51 PM by Hibernate Tools 4.0.0

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ProductBackStoreIn generated by hbm2java
 */
@Entity
@Table(name = "PRODUCT_BACK_STORE_IN", catalog = "MINI_ERP")
public class ProductBackStoreIn implements java.io.Serializable {

    private String id;
    private OrderBack orderBack;
    private StockChange stockChange;
    private Store store;

    public ProductBackStoreIn() {
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ORDER", nullable = false)
    @NotNull
    public OrderBack getOrderBack() {
        return this.orderBack;
    }

    public void setOrderBack(OrderBack orderBack) {
        this.orderBack = orderBack;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "STOCK_CNAHGE", nullable = true)
    public StockChange getStockChange() {
        return this.stockChange;
    }

    public void setStockChange(StockChange stockChange) {
        this.stockChange = stockChange;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE", nullable = false)
    @NotNull
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
