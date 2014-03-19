package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/03/14
 * Time: 14:54
 */
@Entity
@Table(name = "STORE_RES_REBATE", catalog = "MINI_ERP", uniqueConstraints = @UniqueConstraint(columnNames = {"STORE_RES", "ORDER_ITEM_REBATE"}))
public class StoreResRebate implements Comparable<StoreResRebate>, java.io.Serializable {

    private String id;

    private OrderItemRebate.ItemRebateModel mode;

    private StoreRes storeRes;

    private ResUnit calcUnit;

    private OrderItemRebate orderItemRebate;


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

    @Enumerated(EnumType.STRING)
    @Column(name="MODE", nullable = false,length = 20)
    @NotNull
    public OrderItemRebate.ItemRebateModel getMode() {
        return mode;
    }

    public void setMode(OrderItemRebate.ItemRebateModel model) {
        this.mode = model;
    }

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JoinColumn(name = "STORE_RES",nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "CALC_UNIT",nullable = true)
    public ResUnit getCalcUnit() {
        return calcUnit;
    }

    public void setCalcUnit(ResUnit calcUnit) {
        this.calcUnit = calcUnit;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ITEM_REBATE", nullable = false)
    @NotNull
    public OrderItemRebate getOrderItemRebate() {
        return orderItemRebate;
    }

    public void setOrderItemRebate(OrderItemRebate orderItemRebate) {
        this.orderItemRebate = orderItemRebate;
    }

    @Override
    @Transient
    public int compareTo(StoreResRebate o) {
        int result = getMode().compareTo(o.getMode());
        if (result == 0){
            return getStoreRes().compareTo(o.getStoreRes());
        }else{
            return result;
        }
    }
}
