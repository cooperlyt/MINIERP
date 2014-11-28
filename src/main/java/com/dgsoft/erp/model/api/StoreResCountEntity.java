package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

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
public abstract class StoreResCountEntity extends StoreResEntity{


    protected static final int FLOAT_CONVERT_SCALE = 10;

    public StoreResCountEntity() {
    }

    protected StoreResCountEntity(Res res, ResUnit defaultUnit) {
        super(res);
        setUseUnit(defaultUnit);
    }

    protected StoreResCountEntity(StoreRes storeRes, ResUnit defaultUnit) {
        super(storeRes);
        setUseUnit(defaultUnit);
    }

    public abstract BigDecimal getCount();

    public abstract void setCount(BigDecimal count);

    public BigDecimal getMasterCount() {
        return getCount();
    }

    public void setMasterCount(BigDecimal count) {
        setCount(count);
    }

    public abstract StoreRes getStoreRes();

    public abstract void setStoreRes(StoreRes storeRes);

    private ResUnit useUnit;

    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }

    @Override
    public String getCode() {
        if (getStoreRes() == null) {
            return super.getCode();
        } else {
            return getStoreRes().getCode();
        }
    }

    @Override
    public Res getRes() {
        if (getStoreRes() == null) {
            return super.getRes();
        } else {
            return getStoreRes().getRes();
        }
    }

    @Override
    public BigDecimal getFloatConvertRate() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return null;
        }
        if (getStoreRes() == null) {
            return super.getFloatConvertRate();
        } else {
            return DataFormat.format(getStoreRes().getFloatConversionRate(), getStoreRes().getRes().getUnitGroup().getFloatConvertRateFormat());
        }
    }

    @Override
    public List<Format> getFormats() {
        if (getStoreRes() == null) {
            return super.getFormats();
        } else {
            return ResFormatCache.instance().getFormats(getStoreRes());
        }
    }

    public BigDecimal getUseUnitCount() {

        if (getMasterCount() == null) {
            return null;
        }

        if (getUseUnit().isMasterUnit()) {
            return DataFormat.format(getMasterCount(), getUseUnit().getCountFormate());
        } else if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            return DataFormat.format(
                    getMasterCount().divide(getUseUnit().getConversionRate(),
                            FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), getUseUnit().getCountFormate());


        } else if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return getAuxCount();
        }
        return null;
    }

    public void setUseUnitCount(BigDecimal count) {
        if (count == null) {
            setMasterCount(null);
            return;
        }
        if (getUseUnit().isMasterUnit()) {
            setMasterCount(DataFormat.format(count, getUseUnit().getCountFormate()));
        } else {
            if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                setMasterCount(DataFormat.format(count.multiply(getUseUnit().getConversionRate()), getUseUnit().getCountFormate()));
            } else if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                setAuxCount(count);
            }
        }

    }

    public BigDecimal getAuxCount() {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return null;
        }

        if (getMasterCount() == null) {
            return null;
        }
        return calcAuxCount(getMasterCount(),getFloatConvertRate(),
                getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
    }


    protected BigDecimal calcAuxCount(BigDecimal masterCount, BigDecimal convertRate, String format){
        return DataFormat.format(masterCount.multiply(convertRate),format);
    }

    public void setAuxCount(BigDecimal count) {
        if (!getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float convert res can call this function");
        }
        if (count == null) {
            setMasterCount(null);
            return;
        }
        setMasterCount(DataFormat.
                format(count.divide(getFloatConvertRate(),
                        FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP),
                        getRes().getUnitGroup().getMasterUnit().getCountFormate()));
    }

    public String getDisplayMasterCount() {
        if (getMasterCount() == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.applyPattern(getRes().getUnitGroup().getMasterUnit().getCountFormate());
        return df.format(getMasterCount()) + " " + getRes().getUnitGroup().getMasterUnit().getName();
    }

    public String getDisplayAuxCount() {
        if (getMasterCount() == null) {
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

    public BigDecimal getCountByResUnit(ResUnit resUnit) {
        if (getMasterCount() == null) {
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
                return DataFormat.format(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), resUnit.getCountFormate());


            default:
                return null;
        }
    }


    @Override
    public boolean isSameItem(StoreResEntity other) {

        if (!(other instanceof StoreResCountEntity)) {
            return false;
        }

        StoreResCountEntity otherCount = (StoreResCountEntity) other;
        if ((otherCount.getStoreRes() == null) ||  (getStoreRes() == null)){
             return super.isSameItem(other);
        }else {
            return getStoreRes().equals(otherCount.getStoreRes());
        }
    }

    public void subtractCount(StoreResCountEntity otherCount) {
        if (!isSameItem(otherCount)) {
            throw new IllegalArgumentException("not seam unit cant subtractCount");
        }
        if (otherCount.getMasterCount() == null) {
            return;
        }

        BigDecimal thisMasterCount;
        if (getMasterCount() == null) {
            thisMasterCount = BigDecimal.ZERO;
        } else {
            thisMasterCount = getMasterCount();
        }


        setMasterCount(thisMasterCount.subtract(otherCount.getMasterCount()));
    }

    public void addCount(StoreResCountEntity otherCount) {
        if (!isSameItem(otherCount)) {
            throw new IllegalArgumentException("not seam unit cant add");
        }
        if (otherCount.getMasterCount() == null) {
            return;
        }

        BigDecimal thisMasterCount;
        if (getMasterCount() == null) {
            thisMasterCount = BigDecimal.ZERO;
        } else {
            thisMasterCount = getMasterCount();
        }
        setMasterCount(thisMasterCount.add(otherCount.getMasterCount()));

    }


    public StoreResCount getStoreResCount(){
        return new StoreResCount(getStoreRes(),getMasterCount());
    }

}
