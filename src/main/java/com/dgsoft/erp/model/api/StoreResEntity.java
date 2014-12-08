package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

import java.io.Serializable;
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
public class StoreResEntity implements Serializable{

    public StoreResEntity() {
    }


    public StoreResEntity(Res res) {
        initByRes(res);
    }

    public StoreResEntity(StoreRes storeRes) {
        this.res = storeRes.getRes();

        //this.formatHistory = ResHelper.instance().getFormatHistory(res);
        formats = new ArrayList<Format>();
        for (Format format : ResFormatCache.instance().getFormats(storeRes)) {
            formats.add(new Format(format.getFormatDefine(), format.getFormatValue()));
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {


            floatConvertRate = storeRes.getFloatConversionRate();
        }
    }

    protected void initByRes(Res res){
        this.res = res;
        //this.formatHistory = ResHelper.instance().getFormatHistory(res);
        formats = new ArrayList<Format>(res.getFormatDefines().size());
        for (FormatDefine formatDefine : res.getFormatDefineList()) {
            formats.add(new Format(formatDefine));
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {

            floatConvertRate = res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate();
        }
    }


    private BigDecimal floatConvertRate;

    private Res res;

    private String code;

    private List<Format> formats;

    private Map<String, Set<Object>> formatHistory;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private List<BigDecimal> floatConvertRateHistory;

    public Res getRes() {
        return res;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public List<Object> getFormatHistorys(String defineId) {
        if (formatHistory == null){
            this.formatHistory = ResHelper.instance().getFormatHistory(getRes());
        }
        return new ArrayList<Object>(formatHistory.get(defineId));
    }

    public List<BigDecimal> getFloatConvertRateHistory() {
        if ((floatConvertRateHistory == null) && getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            this.floatConvertRateHistory = ResHelper.instance().getFloatConvertRateHistory(getRes());
        }
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

        return ResHelper.instance().sameFormat(other.getFormats(), getFormats())
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
