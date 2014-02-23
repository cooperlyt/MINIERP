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
 * Time: 9:43 AM
 */
@Entity
@Table(name = "BACK_ITEM", catalog = "MINI_ERP")
public class BackItem {

    private String id;
    private BigDecimal count;
    private BigDecimal money;
    private String memo;

    private StoreRes storeRes;
    private ResUnit resUnit;
    private OrderBack orderBack;

    public BackItem() {
    }

    public BackItem(OrderBack orderBack, StoreRes storeRes, ResUnit resUnit, BigDecimal count,
                    BigDecimal money) {
        this.count = count;
        this.money = money;
        this.storeRes = storeRes;
        this.resUnit = resUnit;
        this.orderBack = orderBack;
    }

    public BackItem(OrderBack orderBack,StoreRes storeRes, ResCount resCount) {
        this.orderBack = orderBack;
        this.storeRes = storeRes;
        this.count = resCount.getMasterCount();
        this.resUnit = storeRes.getRes().getUnitGroup().getMasterUnit();
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

    @Column(name = "MONEY", nullable = true, scale = 3)
    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Column(name = "MEMO", length = 200)
    @Size(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "STORE_RES", nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "MONEY_UNIT", nullable = false)
    @NotNull
    public ResUnit getResUnit() {
        return resUnit;
    }

    public void setResUnit(ResUnit resUnit) {
        this.resUnit = resUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BACK_ORDER", nullable = false)
    @NotNull
    public OrderBack getOrderBack() {
        return orderBack;
    }

    public void setOrderBack(OrderBack orderBack) {
        this.orderBack = orderBack;
    }

    @Transient
    public ResCount getResCount() {
        return getStoreRes().getResCount(getCount(), getResUnit());
    }

    @Transient
    public BigDecimal getTotalMoney(){
        if ((getMoney() == null) || (getCount() == null)){
            return BigDecimal.ZERO;
        }
        return getMoney().multiply(getCount());
    }
}
