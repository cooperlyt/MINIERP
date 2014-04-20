package com.dgsoft.erp.model;

import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/21/14
 * Time: 9:43 AM
 */
@Entity
@Table(name = "BACK_ITEM", catalog = "MINI_ERP")
public class BackItem extends StoreResPriceEntity implements java.io.Serializable {

    public enum BackItemStatus {
        CREATE, DISPATCH, STORE_IN;
    }

    private String id;
    private BigDecimal count;
    private BigDecimal money;


    private StoreRes storeRes;
    private ResUnit resUnit;
    private OrderBack orderBack;

    private BackDispatch dispatch;
    private BackItemStatus backItemStatus;
    private BigDecimal totalMoney;
    private BigDecimal rebate;
    private String memo;

    public BackItem() {
    }


    public BackItem(Res res, ResUnit defaultUnit) {
        super(res, defaultUnit);
    }

    public BackItem(StoreRes storeRes, ResUnit defaultUnit) {
        super(storeRes, defaultUnit);
    }

    @Override
    @Transient
    public String getType() {
        return "back";
    }

    //split Item
    public BackItem(OrderBack orderBack,StoreRes storeRes,BigDecimal count,
                    BigDecimal money,ResUnit resUnit,BackItemStatus backItemStatus,BigDecimal rebate,String memo){
        this.count = count;
        this.money = money;
        this.storeRes = storeRes;
        this.resUnit = resUnit;
        this.orderBack = orderBack;
        this.backItemStatus = backItemStatus;
        this.rebate = rebate;
        this.memo = memo;
        calcMoney();
    }

    public BackItem(OrderBack orderBack,OrderItem orderItem){
        this.count = orderItem.getCount();
        this.money = orderItem.getRebateUnitPrice();
        this.storeRes = orderItem.getStoreRes();
        this.resUnit = orderItem.getResUnit();
        this.orderBack = orderBack;
        this.backItemStatus = BackItemStatus.CREATE;
        this.rebate = new BigDecimal("100");
        this.memo = orderItem.getMemo();
        calcMoney();
    }

//
//    public BackItem(OrderBack orderBack, StoreRes storeRes, ResUnit resUnit, BigDecimal count,
//                    BigDecimal money) {
//        this.count = count;
//        this.money = money;
//        this.storeRes = storeRes;
//        this.resUnit = resUnit;
//        this.orderBack = orderBack;
//    }
//
//    public BackItem(BigDecimal count, BigDecimal money, String memo, StoreRes storeRes, ResUnit resUnit, OrderBack orderBack) {
//        this.count = count;
//        this.money = money;
//        this.memo = memo;
//        this.storeRes = storeRes;
//        this.resUnit = resUnit;
//        this.orderBack = orderBack;
//    }
//
//    public BackItem(OrderBack orderBack, StoreRes storeRes, BigDecimal masterCount) {
//        this.orderBack = orderBack;
//        this.storeRes = storeRes;
//        this.count = masterCount;
//    }

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


    @Override
    @Column(name = "COUNT", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getCount() {
        return count;
    }

    @Override
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

    @Override
    @Column(name = "TOTAL_MONEY", nullable = true, scale = 3)
    public BigDecimal getTotalMoney() {
        return this.totalMoney;
    }

    @Override
    public void setTotalMoney(BigDecimal money) {
        this.totalMoney = money;
    }

    @Override
    @Transient
    public boolean isPresentation() {
        return false;
    }

    @Override
    @Transient
    public void setPresentation(boolean presentation) {
    }

    @Override
    @Column(name = "REBATE", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getRebate() {
        return rebate;
    }

    @Override
    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPATCH", nullable = true)
    public BackDispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(BackDispatch dispatch) {
        this.dispatch = dispatch;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 10)
    @NotNull
    public BackItemStatus getBackItemStatus() {
        return backItemStatus;
    }

    public void setBackItemStatus(BackItemStatus backItemStatus) {
        this.backItemStatus = backItemStatus;
    }
}
