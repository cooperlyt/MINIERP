package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:20 PM
 */
public abstract class StoreResCountEntity extends StoreResEntity {

    protected static final int FLOAT_CONVERT_SCALE = 10;

    public StoreResCountEntity() {
    }

    public StoreResCountEntity(StoreRes storeRes) {
        setStoreRes(storeRes);
    }

    public StoreResCountEntity(Res res, Map<String, Set<Object>> formatHistory, ResUnit defaultUnit) {
        super(res, formatHistory);
        setUseUnit(defaultUnit);
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            setFloatConvertRate(res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate());
        }
    }

    public StoreResCountEntity(Res res, Map<String, Set<Object>> formatHistory) {
        super(res, formatHistory);
        setUseUnit(res.getResUnitByMasterUnit());
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            setFloatConvertRate(res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate());
        }
    }

    public StoreResCountEntity(Res res, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        super(res, formatHistory, floatConvertRateHistory);
        setUseUnit(res.getResUnitByMasterUnit());
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            setFloatConvertRate(res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate());
        }
    }

    public StoreResCountEntity(Res res, Map<String, Set<Object>> formatHistory,
                               List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(res, formatHistory, floatConvertRateHistory);
        setUseUnit(defaultUnit);
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            setFloatConvertRate(res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate());
        }
    }

    public abstract BigDecimal getMasterCount();

    public abstract void setMasterCount(BigDecimal count);

    public abstract StoreRes getStoreRes();

    public abstract void setStoreRes(StoreRes storeRes);

    @Transient
    private ResUnit useUnit;

    @Transient
    public ResUnit getUseUnit() {
        return useUnit;
    }

    @Transient
    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }

    @Override
    @Transient
    public Res getRes() {
        if (getStoreRes() == null) {
            return super.getRes();
        } else {
            return getStoreRes().getRes();
        }
    }

    @Override
    @Transient
    public BigDecimal getFloatConvertRate() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("res is not float convert");
        }
        if (getStoreRes() == null) {
            return super.getFloatConvertRate();
        } else {
            return getStoreRes().getFloatConversionRate();
        }
    }

    @Transient
    public BigDecimal getUseUnitCount() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            throw new IllegalArgumentException("only fix convert res can call this function");
        }
        if (getMasterCount() == null){
            return null;
        }

        if (getUseUnit().isMasterUnit()) {
            return BigDecimalFormat.format(getMasterCount(), getUseUnit().getCountFormate());
        } else

            return BigDecimalFormat.format(
                    getMasterCount().divide(getUseUnit().getConversionRate(),
                            FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), getUseUnit().getCountFormate());

    }

    @Transient
    public void setUseUnitCount(BigDecimal count) {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            throw new IllegalArgumentException("only fix convert res can call this function");
        }
        if (count == null){
            setMasterCount(null);
            return;
        }
        if (getUseUnit().isMasterUnit()) {
            setMasterCount(BigDecimalFormat.format(count, getUseUnit().getCountFormate()));
        } else {
            setMasterCount(BigDecimalFormat.format(count.multiply(getUseUnit().getConversionRate()), getUseUnit().getCountFormate()));
        }
    }

    @Transient
    public BigDecimal getAuxCount() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float convert res can call this function");
        }

        if (getMasterCount() == null){
            return null;
        }
        return BigDecimalFormat.format(getMasterCount().multiply(getFloatConvertRate()),
                getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
    }

    @Transient
    public void setAuxCount(BigDecimal count) {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float convert res can call this function");
        }
        if (count == null){
            setMasterCount(null);
            return;
        }
        setMasterCount(BigDecimalFormat.
                format(count.divide(getFloatConvertRate(),
                        FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP),
                        getRes().getUnitGroup().getMasterUnit().getCountFormate()));
    }

    @Transient
    public String getDisplayMasterCount() {
        if (getMasterCount() == null){
            return null;
        }
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.applyPattern(getRes().getUnitGroup().getMasterUnit().getCountFormate());
        return df.format(getMasterCount()) + " " + getRes().getUnitGroup().getMasterUnit().getName();
    }

    @Transient
    public String getDisplayAuxCount() {
        if (getMasterCount() == null){
            return null;
        }
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);

        if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            df.applyPattern(getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
            return df.format(getAuxCount()) + " " + getRes().getUnitGroup().getFloatAuxiliaryUnit().getName();
        } else if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {

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

    @Transient
    public BigDecimal getCountByResUnit(ResUnit resUnit) {
        if (getMasterCount() == null){
            return null;
        }
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


    @Transient
    public boolean isSameItem(StoreResCountEntity other) {
        if ((getStoreRes() != null) && other.getStoreRes() != null) {
            return getStoreRes().equals(other.getStoreRes());
        }else{
            return super.isSameItem(other);
        }
    }

    @Transient
    public void subtract(StoreResCountEntity otherCount) {
        if (!isSameItem(otherCount)) {
            throw new IllegalArgumentException("not seam unit cant subtract");
        }
        if (otherCount.getMasterCount() == null){
            return;
        }

        BigDecimal thisMasterCount;
        if (getMasterCount() == null){
            thisMasterCount = BigDecimal.ZERO;
        }else{
            thisMasterCount = getMasterCount();
        }


        setMasterCount(thisMasterCount.subtract(otherCount.getMasterCount() ));
    }

    @Transient
    public void add(StoreResCountEntity otherCount) {
        if (!isSameItem(otherCount)) {
            throw new IllegalArgumentException("not seam unit cant subtract");
        }
        if (otherCount.getMasterCount() == null){
            return;
        }

        BigDecimal thisMasterCount;
        if (getMasterCount() == null){
            thisMasterCount = BigDecimal.ZERO;
        }else{
            thisMasterCount = getMasterCount();
        }
        setMasterCount(thisMasterCount.add(otherCount.getMasterCount()));

    }


}
