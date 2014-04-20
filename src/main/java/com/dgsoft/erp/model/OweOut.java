package com.dgsoft.erp.model;

import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cooper on 1/5/14.
 */
@Entity
@Table(name = "OWE_OUT", catalog = "MINI_ERP")
public class OweOut extends StoreResCountEntity implements java.io.Serializable {

    private String id;
    private StoreRes storeRes;
    private BigDecimal count;
    private String description;
    private Dispatch dispatch;
    private BigDecimal needConvertRate;
    private boolean add;

    public OweOut() {
    }

    public OweOut(Res res, ResUnit defaultUnit, Dispatch dispatch) {
        super(res, defaultUnit);
        this.dispatch = dispatch;
        this.add = true;
    }

    public OweOut(StoreRes storeRes, ResUnit defaultUnit, Dispatch dispatch) {
        super(storeRes, defaultUnit);
        this.dispatch = dispatch;
        this.add = true;
    }

    public OweOut(Dispatch dispatch, StoreRes storeRes, BigDecimal count, String description,BigDecimal needConvertRate) {
        this.dispatch = dispatch;
        this.storeRes = storeRes;
        this.count = count;
        this.description = description;
        this.needConvertRate = needConvertRate;
        this.add = false;
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

    @Column(name = "IS_ADD", nullable = false)
    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    @Column(name = "NEED_CONVERSION_RATE", nullable = true, scale = 3)
    public BigDecimal getNeedConvertRate() {
        return needConvertRate;
    }

    public void setNeedConvertRate(BigDecimal needConvertRate) {
        this.needConvertRate = needConvertRate;
    }
}
