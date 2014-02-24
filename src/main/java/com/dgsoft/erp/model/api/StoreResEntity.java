package com.dgsoft.erp.model.api;

import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:24 PM
 */
public abstract class StoreResEntity {

    public abstract StoreRes getStoreRes();

    public abstract void setStoreRes(StoreRes storeRes);

    private BigDecimal floatConvertRate;

    private Res res;

    private List<Format> formats;

    private Map<FormatDefine, List<Format>> formatHistory;

    private List<BigDecimal> floatConvertRateHistory;

    public StoreResEntity() {

    }

    public StoreResEntity(StoreRes storeRes) {
        setStoreRes(storeRes);
    }

    public StoreResEntity(Res res, Map<FormatDefine, List<Format>> formatHistory) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("FLOAT_CONVERT res must be call floatConvert history constructor");
        }
        this.res = res;
        this.formatHistory = formatHistory;
    }

    public StoreResEntity(Res res, Map<FormatDefine,
            List<Format>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only FLOAT_CONVERT  call ");
        }
        this.res = res;
        this.formatHistory = formatHistory;
        this.floatConvertRateHistory = floatConvertRateHistory;
    }

    public Res getRes() {
        if (getStoreRes() == null) {
            return res;
        } else {
            return getStoreRes().getRes();
        }
    }


    public BigDecimal getFloatConvertRate() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("res is not float convert");
        }
        if (getStoreRes() == null) {
            return floatConvertRate;
        } else {
            return getStoreRes().getFloatConversionRate();
        }
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public boolean isSameItem(StoreResEntity other) {
        if ((getStoreRes() != null) && other.getStoreRes() != null) {
            return getStoreRes().equals(other.getStoreRes());
        } else {
            return ResHelper.sameFormat(other.getFormats(), getFormats())
                    && (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                    || getFloatConvertRate().equals(other.getFloatConvertRate()));
        }
    }
}
