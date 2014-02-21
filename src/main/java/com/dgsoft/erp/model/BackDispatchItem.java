package com.dgsoft.erp.model;

import com.dgsoft.erp.model.api.ResCount;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/21/14
 * Time: 9:44 AM
 */
@Entity
@Table(name = "BACK_DISPATCH_ITEM", catalog = "MINI_ERP")
public class BackDispatchItem {

    private String id;

    private BigDecimal count;
    private StoreRes storeRes;
    private ProductBackStoreIn productBackStoreIn;

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

    @Column(name="COUNT",nullable = false,scale = 4)
    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name="STORE_RES",nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="PRODUCT_STORE_IN",nullable = false)
    @NotNull
    public ProductBackStoreIn getProductBackStoreIn() {
        return productBackStoreIn;
    }

    public void setProductBackStoreIn(ProductBackStoreIn productBackStoreIn) {
        this.productBackStoreIn = productBackStoreIn;
    }

    @Transient
    public ResCount getResCount(){
        return getStoreRes().getResCount(getCount());
    }
}
