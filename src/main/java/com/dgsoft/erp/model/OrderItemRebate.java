package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/03/14
 * Time: 14:21
 */
@Entity
@Table(name = "ORDER_ITEM_REBATE", catalog = "MINI_ERP" ,uniqueConstraints = @UniqueConstraint(columnNames = {"RES", "PROGRAM"}))
public class OrderItemRebate implements Comparable<OrderItemRebate>, java.io.Serializable {

    @Override
    @Transient
    public int compareTo(OrderItemRebate o) {
        int result = getMode().compareTo(o.getMode());
        if (result == 0){
            return getRes().compareTo(o.getRes());
        }else{
            return result;
        }
    }

    public enum ItemRebateModel{
        NO_CALC, BY_COUNT, BY_MONEY;
    }

    private String id;
    private ItemRebateModel mode;
    private RebateProgram rebateProgram;
    private Res res;
    private ResUnit calcUnit;
    private BigDecimal rebate;
    private Set<StoreResRebate> storeResRebates = new HashSet<StoreResRebate>(0);

    public OrderItemRebate() {
    }

    public OrderItemRebate(RebateProgram rebateProgram,ItemRebateModel mode) {
        this.mode = mode;
        this.rebateProgram = rebateProgram;
    }

    public OrderItemRebate(RebateProgram rebateProgram,ItemRebateModel mode, Res res) {
        this.mode = mode;
        this.res = res;
        this.rebateProgram = rebateProgram;
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

    @Enumerated(EnumType.STRING)
    @Column(name="MODE",nullable = false,length = 20)
    @NotNull
    public ItemRebateModel getMode() {
        return mode;
    }

    public void setMode(ItemRebateModel mode) {
        this.mode = mode;
    }


    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "PROGRAM",nullable = false)
    @NotNull
    public RebateProgram getRebateProgram() {
        return rebateProgram;
    }

    public void setRebateProgram(RebateProgram rebateProgram) {
        this.rebateProgram = rebateProgram;
    }


    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "RES", nullable = false)
    @NotNull
    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    @ManyToOne(optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name="CALC_UNIT", nullable = true)
    public ResUnit getCalcUnit() {
        return calcUnit;
    }

    public void setCalcUnit(ResUnit calcUnit) {
        this.calcUnit = calcUnit;
    }

    @Column(name = "REBATE", nullable = true ,scale = 6)
    public BigDecimal getRebate() {
        return rebate;
    }

    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "orderItemRebate")
    public Set<StoreResRebate> getStoreResRebates() {
        return storeResRebates;
    }

    public void setStoreResRebates(Set<StoreResRebate> storeResRebates) {
        this.storeResRebates = storeResRebates;
    }

    @Transient
    public List<StoreResRebate> getStoreResRebateList(){
        List<StoreResRebate> result = new ArrayList<StoreResRebate>(getStoreResRebates());
        Collections.sort(result);
        return result;
    }
}
