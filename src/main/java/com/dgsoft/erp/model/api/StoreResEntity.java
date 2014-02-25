package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:24 PM
 */
public abstract class StoreResEntity {

    public StoreResEntity() {
    }

    public StoreResEntity(Res res, Map<String, Set<Object>> formatHistory) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("FLOAT_CONVERT res must be call floatConvert history constructor");
        }
        this.res = res;
        this.formatHistory = formatHistory;
        initResFormat();
    }

    public StoreResEntity(Res res, Map<String, Set<Object>> formatHistory,
                          List<BigDecimal> floatConvertRateHistory) {
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only FLOAT_CONVERT  call ");
        }
        this.res = res;
        this.formatHistory = formatHistory;
        this.floatConvertRateHistory = floatConvertRateHistory;
        initResFormat();
        floatConvertRate = res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate();
    }

    public StoreResEntity(Res res, Map<String, Set<Object>> formatHistory,
                          List<BigDecimal> floatConvertRateHistory, BigDecimal defaultFloatConvertRate) {
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only FLOAT_CONVERT  call ");
        }
        this.res = res;
        this.formatHistory = formatHistory;
        this.floatConvertRateHistory = floatConvertRateHistory;
        initResFormat();
        floatConvertRate = defaultFloatConvertRate;
    }

    private void initResFormat(){
        formats = new ArrayList<Format>(res.getFormatDefines().size());
        for (FormatDefine formatDefine : res.getFormatDefineList()) {
            formats.add(new Format(formatDefine));
        }
    }

    private BigDecimal floatConvertRate;

    private Res res;

    private List<Format> formats;

    private Map<String, Set<Object>> formatHistory;

    private List<BigDecimal> floatConvertRateHistory;

    public Res getRes() {
        return res;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public List<Object> getFormatHistorys(String defineId) {
        return new ArrayList<Object>(formatHistory.get(defineId));
    }

    public List<BigDecimal> getFloatConvertRateHistory() {
        return floatConvertRateHistory;
    }

    public BigDecimal getFloatConvertRate() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("res is not float convert");
        }
        return BigDecimalFormat.format(floatConvertRate,getRes().getUnitGroup().getFloatConvertRateFormat());

    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = BigDecimalFormat.format(floatConvertRate,getRes().getUnitGroup().getFloatConvertRateFormat());
    }


    public boolean isSameItem(StoreResEntity other) {

        return ResHelper.sameFormat(other.getFormats(), getFormats())
                && (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                || getFloatConvertRate().equals(other.getFloatConvertRate()));

    }
}
