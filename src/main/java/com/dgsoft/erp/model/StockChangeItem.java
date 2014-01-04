package com.dgsoft.erp.model;
// Generated Oct 1, 2013 5:41:32 PM by Hibernate Tools 4.0.0

import com.dgsoft.erp.model.api.ResCount;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * StockChangeItem generated by hbm2java
 */
@Entity
@Table(name = "STOCK_CHANGE_ITEM", catalog = "MINI_ERP")
public class StockChangeItem implements java.io.Serializable {

    private String id;
    private StoreRes storeRes;
    private Stock stock;
    private StockChange stockChange;
    private boolean storeOut;
    private BigDecimal count;
    private BigDecimal befortCount;
    private BigDecimal afterCount;

    private Batch batch;
    private Set<NoConvertCount> noConvertCounts = new HashSet<NoConvertCount>(0);

    public StockChangeItem() {
    }


    public StockChangeItem(StockChange stockChange, StoreRes storeRes, BigDecimal count, boolean storeOut) {
        this.stockChange = stockChange;
        this.storeRes = storeRes;
        this.storeOut = storeOut;
        this.count = count;
    }

    public StockChangeItem(StockChange stockChange, Stock stock, BigDecimal count, boolean storeOut) {
        this.stockChange = stockChange;
        this.storeRes = stock.getStoreRes();
        this.stock = stock;
        this.storeOut = storeOut;
        this.count = count;
        this.befortCount = stock.getCount();
        if (storeOut){
            this.afterCount = befortCount.subtract(count);
        } else{
            this.afterCount = befortCount.add(count);
        }
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

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "STORE_RES", nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return this.storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BATCH", nullable = true)
    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "STOCK", nullable = false)
    @NotNull
    public Stock getStock() {
        return this.stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STORE_CHANGE", nullable = false)
    @NotNull
    public StockChange getStockChange() {
        return this.stockChange;
    }

    public void setStockChange(StockChange stockChange) {
        this.stockChange = stockChange;
    }

    @Column(name = "STORE_OUT", nullable = false)
    public boolean isStoreOut() {
        return this.storeOut;
    }

    public void setStoreOut(boolean storeOut) {
        this.storeOut = storeOut;
    }

    @Column(name = "COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return this.count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Column(name = "BEFORT_COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getBefortCount() {
        return this.befortCount;
    }

    public void setBefortCount(BigDecimal befortCount) {
        this.befortCount = befortCount;
    }

    @Column(name = "AFTER_COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getAfterCount() {
        return this.afterCount;
    }

    public void setAfterCount(BigDecimal afterCount) {
        this.afterCount = afterCount;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockChangeItem", cascade = {CascadeType.ALL})
    public Set<NoConvertCount> getNoConvertCounts() {
        return this.noConvertCounts;
    }

    public void setNoConvertCounts(Set<NoConvertCount> noConvertCounts) {
        this.noConvertCounts = noConvertCounts;
    }


    @Transient
    public ResCount getResCount(){
       return getStoreRes().getResCount(getCount());
    }
}
