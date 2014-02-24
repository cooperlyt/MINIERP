package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:20 PM
 */
public abstract class StoreResCountEntity extends StoreResEntity {

    protected static final int FLOAT_CONVERT_SCALE = 10;

    public abstract BigDecimal getMasterCount();

    public abstract void setMasterCount(BigDecimal count);

    protected StoreResCountEntity() {
    }

    protected StoreResCountEntity(StoreRes storeRes) {
        super(storeRes);
    }

    protected StoreResCountEntity(Res res, Map<FormatDefine, List<Format>> formatHistory) {
        super(res, formatHistory);
    }

    protected StoreResCountEntity(Res res, Map<FormatDefine, List<Format>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        super(res, formatHistory, floatConvertRateHistory);
    }

    public BigDecimal getAuxCount() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float convert res can call this function");
        }
        return BigDecimalFormat.format(getMasterCount().multiply(getFloatConvertRate()),
                getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
    }

    public void setAuxCount(BigDecimal count) {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float convert res can call this function");
        }
        setMasterCount(BigDecimalFormat.
                format(count.divide(getFloatConvertRate(),
                        FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP),
                        getRes().getUnitGroup().getMasterUnit().getCountFormate()));
    }

    public BigDecimal getCountByResUnit(ResUnit resUnit) {

        switch (getRes().getUnitGroup().getType()) {
            case FLOAT_CONVERT:
                if (resUnit.isMasterUnit()) {
                    return getMasterCount();
                } else {
                    return getAuxCount();
                }
            case FIX_CONVERT:
                return BigDecimalFormat.format(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), resUnit.getCountFormate());

            default:
                return null;
        }
    }

    public String getDisplayMasterCount(){
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.applyPattern(getRes().getUnitGroup().getMasterUnit().getCountFormate());
        return df.format(getMasterCount()) + " " + getRes().getUnitGroup().getMasterUnit().getName();
    }

    public String getDisplayAuxCount() {

        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);

        if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            df.applyPattern(getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
            return df.format(getAuxCount()) + " " + getRes().getUnitGroup().getFloatAuxiliaryUnit().getName();
        }else if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)){

            String result = "";
            for (ResUnit resUnit : getRes().getUnitGroup().getResUnitList()) {
                df.applyPattern(resUnit.getCountFormate());
                if (resUnit.getConversionRate().compareTo(BigDecimal.ONE) != 0) {
                    result += df.format(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP))
                            + " " + resUnit.getName() + ",";
                }
            }

            if (!"".equals(result))
                result = result.substring(0, result.length() - 1);

            return result;
        }

        return "";
    }


    public void subtract(StoreResCountEntity otherCount){
        if (!isSameItem(otherCount)){
            throw new IllegalArgumentException("not seam unit cant subtract");
        }
        setMasterCount(getMasterCount().subtract(otherCount.getMasterCount()));
    }

    public void add(StoreResCountEntity otherCount) {
        if (!isSameItem(otherCount)){
            throw new IllegalArgumentException("not seam unit cant subtract");
        }
        setMasterCount(getMasterCount().add(otherCount.getMasterCount()));

    }


}
