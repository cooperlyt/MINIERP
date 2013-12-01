package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/1/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "PREPARE_STOCK_CHANGE", catalog = "MINI_ERP")
public class PrepareStockChange implements java.io.Serializable {

    private String id;
    private BigDecimal count;

    private StockChange stockChange;
    private StoreRes storeRes;
    private Set<NoConvertCount> noConvertCounts = new HashSet<NoConvertCount>(0);

    public PrepareStockChange() {
        super();
    }

    public PrepareStockChange(StockChange stockChange, StoreRes storeRes, BigDecimal count) {
        this.count = count;
        this.stockChange = stockChange;
        this.storeRes = storeRes;
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

    @Column(name = "COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return this.count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STOCK_CHANGE", nullable = false)
    @NotNull
    public StockChange getStockChange() {
        return this.stockChange;
    }

    public void setStockChange(StockChange stockChange) {
        this.stockChange = stockChange;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "prepareStockChange", cascade = {CascadeType.ALL})
    public Set<NoConvertCount> getNoConvertCounts() {
        return this.noConvertCounts;
    }

    public void setNoConvertCounts(Set<NoConvertCount> noConvertCounts) {
        this.noConvertCounts = noConvertCounts;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "STORE_RES", nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return this.storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

}
