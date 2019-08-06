package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "OUT_NUMBER", catalog = "MINI_ERP")
public class OutNumber {

    private String id;
    private String prefx;
    private String after;
    private int begin;
    private int end;
    private StockChange stockChange;

    public OutNumber() {
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

    @Column(name = "PREFX")
    public String getPrefx() {
        return prefx;
    }

    public void setPrefx(String prefx) {
        this.prefx = prefx;
    }

    @Column(name = "AFTER")
    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    @Column(name = "BEGIN")
    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    @Column(name = "END")
    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANGE_ID")
    public StockChange getStockChange() {
        return stockChange;
    }

    public void setStockChange(StockChange stockChange) {
        this.stockChange = stockChange;
    }


}
