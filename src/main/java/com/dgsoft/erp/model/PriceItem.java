package com.dgsoft.erp.model;

import com.dgsoft.erp.model.api.StoreResPriceEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 26/03/14
 * Time: 14:39
 */
@Entity
@Table(name = "PRICE_ITEM", catalog = "MINI_ERP", uniqueConstraints = @UniqueConstraint(columnNames = {"STORE_RES", "QUOTED"}))
public class PriceItem extends StoreResPriceEntity implements Serializable {

    private String id;
    private String memo;
    private BigDecimal count;
    private BigDecimal money;
    private ResUnit resUnit;
    private StoreRes storeRes;
    private QuotedPrice quotedPrice;

    public PriceItem() {
    }

    public PriceItem(Res res, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit, QuotedPrice quotedPrice) {
        super(res, formatHistory, floatConvertRateHistory, defaultUnit);
        this.quotedPrice = quotedPrice;
    }

    public PriceItem(StoreRes storeRes, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit, QuotedPrice quotedPrice) {
        super(storeRes, formatHistory, floatConvertRateHistory, defaultUnit);
        this.quotedPrice = quotedPrice;
    }

    public PriceItem(QuotedPrice quotedPrice, StoreRes storeRes, BigDecimal count, BigDecimal money, ResUnit resUnit, String memo) {
        this.memo = memo;
        this.count = count;
        this.money = money;
        this.resUnit = resUnit;
        this.storeRes = storeRes;
        this.quotedPrice = quotedPrice;
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

    @Column(name = "MEMO", length = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    @Override
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "STORE_RES", nullable = false)
    @NotNull
    public StoreRes getStoreRes() {
        return storeRes;
    }

    @Override
    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    @Override
    @Column(name = "MONEY", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getMoney() {
        return money;
    }

    @Override
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "MONEY_UNIT", nullable = false)
    @NotNull
    public ResUnit getResUnit() {
        return resUnit;
    }

    @Override
    public void setResUnit(ResUnit resUnit) {
        this.resUnit = resUnit;
    }

    @Override
    public void setTotalMoney(BigDecimal money) {

    }

    @Override
    public BigDecimal getTotalMoney() {
        return null;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTED", nullable = false)
    @NotNull
    public QuotedPrice getQuotedPrice() {
        return quotedPrice;
    }

    public void setQuotedPrice(QuotedPrice quotedPrice) {
        this.quotedPrice = quotedPrice;
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
}
