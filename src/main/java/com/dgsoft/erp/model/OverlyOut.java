package com.dgsoft.erp.model;

import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created by cooper on 1/5/14.
 */
@Entity
@Table(name = "OVERLY_OUT", catalog = "MINI_ERP")
public class OverlyOut extends StoreResCountEntity implements java.io.Serializable{

    private String id;
    private StoreRes storeRes;
    private BigDecimal count;
    private String description;
    private Dispatch dispatch;
    private boolean add;

    public OverlyOut() {
    }

    public OverlyOut(Dispatch dispatch, StoreRes storeRes, BigDecimal count, boolean add) {
        this.storeRes = storeRes;
        this.count = count;
        this.dispatch = dispatch;
        this.add = add;
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

    @Column(name = "COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Column(name = "DESCRIPTION", length = 200)
    @Size(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_RES", nullable = false)
    public StoreRes getStoreRes() {
        return storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPATCH", nullable = false)
    public Dispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    @Column(name = "IS_ADD",nullable = false)
    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}
