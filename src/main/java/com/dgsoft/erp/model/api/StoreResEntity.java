package com.dgsoft.erp.model.api;

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

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
public class StoreResEntity {

    public StoreResEntity() {
    }


    public StoreResEntity(Res res, Map<String, Set<Object>> formatHistory,
                          List<BigDecimal> floatConvertRateHistory) {
        this.res = res;
        this.formatHistory = formatHistory;
        formats = new ArrayList<Format>(res.getFormatDefines().size());
        for (FormatDefine formatDefine : res.getFormatDefineList()) {
            formats.add(new Format(formatDefine));
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            if (floatConvertRateHistory == null) {
                throw new IllegalArgumentException("float convert res must set floatConvertRateHistory");
            }
            this.floatConvertRateHistory = floatConvertRateHistory;
            floatConvertRate = res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate();
        }
    }


    public StoreResEntity(StoreRes storeRes, Map<String, Set<Object>> formatHistory,
                          List<BigDecimal> floatConvertRateHistory) {
        this.res = storeRes.getRes();
        this.formatHistory = formatHistory;
        formats = new ArrayList<Format>(res.getFormatDefines().size());
        for (Format format : storeRes.getFormatList()) {
            formats.add(new Format(format.getFormatDefine(), format.getFormatValue()));
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            if (floatConvertRateHistory == null) {
                throw new IllegalArgumentException("float convert res must set floatConvertRateHistory");
            }
            this.floatConvertRateHistory = floatConvertRateHistory;
            floatConvertRate = storeRes.getFloatConversionRate();
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
        if (floatConvertRate == null){
            return null;
        }
        return DataFormat.format(floatConvertRate, getRes().getUnitGroup().getFloatConvertRateFormat());

    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        if (floatConvertRate == null) {
            this.floatConvertRate = null;
        } else
            this.floatConvertRate = DataFormat.format(floatConvertRate, getRes().getUnitGroup().getFloatConvertRateFormat());
    }


    public boolean isSameItem(StoreResEntity other) {

        return ResHelper.sameFormat(other.getFormats(), getFormats())
                && (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                || getFloatConvertRate().equals(other.getFloatConvertRate()));

    }

    public boolean isNoFormatTyped() {
        for (Format format : formats) {
            if ((format.getFormatValue() != null) && !format.getFormatValue().trim().equals("")) {
                return false;
            }
        }
        return true;
    }
}
