package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.*;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/23/13
 * Time: 11:34 AM
 */
public class StoreResCount implements java.io.Serializable {

    private static final int FLOAT_CONVERT_SCALE = 10;

    public static class NoConverCountEntry implements java.io.Serializable {

        private BigDecimal count;

        private ResUnit resUnit;

        public NoConverCountEntry(ResUnit resUnit, BigDecimal count) {
            this.count = count;
            this.resUnit = resUnit;
        }

        public BigDecimal getCount() {
            return count;
        }

        public void setCount(BigDecimal count) {
            this.count = count;
        }

        public ResUnit getResUnit() {
            return resUnit;
        }

        public void setResUnit(ResUnit resUnit) {
            this.resUnit = resUnit;
        }
    }

    private Res res;

    private BigDecimal count;

    private ResUnit useUnit;

    private BigDecimal auxCount;

    private BigDecimal floatConvertRate;

    private List<NoConverCountEntry> noConvertCountList;

    public StoreResCount(Res res) {
        this.res = res;
        init();
    }

    public StoreResCount(Res res, ResUnit useUnit) {
        this.res = res;
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            this.useUnit = res.getUnitGroup().getMasterUnit();
        } else
            this.useUnit = useUnit;
        init();
    }

    private BigDecimal countFormat(BigDecimal count, ResUnit unit) {
        DecimalFormat df = new DecimalFormat(unit.getCountFormate());
        df.setGroupingUsed(false);
        df.setRoundingMode(RoundingMode.HALF_UP);
        try {
            return new BigDecimal(df.parse(df.format(count)).toString());
        } catch (ParseException e) {
            Logging.getLog(this.getClass()).warn("count cant be format:" + count);
            return count;
        }

    }

    public Res getRes() {
        return res;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        if (useUnit != null) {
            this.count = countFormat(count, useUnit);
        } else {
            this.count = count;
        }
    }

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {

        this.floatConvertRate = floatConvertRate;
        if ((res != null) && (res.getUnitGroup().getFloatConvertRateFormat() != null)) {
            DecimalFormat df = new DecimalFormat(res.getUnitGroup().getFloatConvertRateFormat());
            df.setRoundingMode(RoundingMode.HALF_UP);
            df.setGroupingUsed(false);
            try {
                this.floatConvertRate = new BigDecimal(df.parse(df.format(floatConvertRate)).toString());
            } catch (ParseException e) {
                Logging.getLog(this.getClass()).warn("floatConvertRate cant be format:" + this.floatConvertRate);
            }
        }
    }

    public List<NoConverCountEntry> getNoConvertCountList() {
        return noConvertCountList;
    }

    public void setNoConvertCountList(List<NoConverCountEntry> noConvertCountList) {
        this.noConvertCountList = noConvertCountList;
    }

    public BigDecimal getAuxCount() {
        return auxCount;
    }

    public void setAuxCount(BigDecimal auxCount) {
        if (res != null) {
            this.auxCount = countFormat(auxCount, res.getUnitGroup().getFloatAuxiliaryUnit());
        } else {
            this.auxCount = auxCount;
        }
    }

    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }

    public String getDisplayAuxCount() {
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);
        String result;
        switch (res.getUnitGroup().getType()) {
            case FIX_CONVERT:

                result = "";
                for (ResUnit resUnit : res.getUnitGroup().getResUnitList()) {
                    df.applyPattern(resUnit.getCountFormate());
                    if (resUnit.getConversionRate().compareTo(new BigDecimal("1")) != 0) {
                        result += df.format(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP))
                                + " " + resUnit.getName() + ",";
                    }
                }

                if (!"".equals(result))
                    result = result.substring(0, result.length() - 1);

                return result;
            case FLOAT_CONVERT:
                df.applyPattern(res.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
                return df.format(getAuxCount()) + " " + res.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate();
            case NO_CONVERT:

                result = "";
                for (NoConverCountEntry entry : noConvertCountList) {
                    df.applyPattern(entry.getResUnit().getCountFormate());
                    try {
                        result += new BigDecimal(df.parse(df.format(entry.getCount())).toString());
                    } catch (ParseException e) {
                        Logging.getLog(this.getClass()).warn("cnat form noConvertCount:" + entry.getCount());
                        result += entry.getCount().toPlainString();
                    }
                    result += " " + entry.getResUnit().getName() + ",";
                }
                if (!"".equals(result))
                    result = result.substring(0, result.length() - 1);
                return result;
            default:
                return null;
        }

    }

    public Set<NoConvertCount> getNoConvertCounts(StockChangeItem stockChangeItem) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            Set<NoConvertCount> result = new HashSet<NoConvertCount>(noConvertCountList.size());
            for (NoConverCountEntry entry : noConvertCountList) {
                result.add(new NoConvertCount(stockChangeItem, entry.resUnit, entry.getCount()));
            }
            return result;
        } else
            return new HashSet<NoConvertCount>(0);
    }

    public String getMasterDisplayCount() {
        DecimalFormat df = new DecimalFormat(res.getUnitGroup().getMasterUnit().getCountFormate());
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(getMasterCount()) + " " + res.getUnitGroup().getMasterUnit().getName();
    }

    public BigDecimal getMasterCount() {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            return count.multiply(useUnit.getConversionRate());
        } else {
            return count;
        }
    }

    public BigDecimal getCountByResUnit(ResUnit resUnit) {
        if (resUnit.getId().equals(useUnit.getId())) {
            return count;
        }
        switch (res.getUnitGroup().getType()){
            case FLOAT_CONVERT:
                if (resUnit.isMasterUnit()) {
                    return getMasterCount();
                } else {
                    //calcFloatQuantityByMasterUnit();
                    return getAuxCount();
                }
            case FIX_CONVERT:
                return countFormat(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP),resUnit);
            case NO_CONVERT:
                for (NoConverCountEntry entry: noConvertCountList){
                    if (entry.getResUnit().getId().equals(resUnit.getId())){
                        return entry.getCount();
                    }
                }
            default:
                return null;
        }

    }

    public void init() {
        count = new BigDecimal(0);
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            useUnit = res.getResUnitByMasterUnit();
            setFloatConvertRate(res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate());
            setAuxCount(new BigDecimal(0));
        } else if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            useUnit = res.getResUnitByMasterUnit();

            if (res.getUnitGroup().getResUnits().size() > 1) {
                noConvertCountList = new ArrayList<NoConverCountEntry>(res.getUnitGroup().getResUnits().size() - 1);
                for (ResUnit unit : res.getUnitGroup().getResUnitList()) {
                    if (!unit.equals(res.getResUnitByMasterUnit())) {
                        noConvertCountList.add(new NoConverCountEntry(unit, new BigDecimal(0)));
                    }
                }
            }
        }
    }

    public void add(StoreResCount otherCount) {
        if (!otherCount.res.equals(res) || (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                && (otherCount.getFloatConvertRate().compareTo(floatConvertRate) != 0))) {
            throw new IllegalArgumentException("not same storeInItem can't merger, float:" + otherCount.getFloatConvertRate() + "=" + floatConvertRate);
        }

        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            setCount(count.add(useUnit.getConversionRate().multiply(otherCount.getMasterCount())));

        } else {
            setCount(count.add(otherCount.getCount()));
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                for (NoConverCountEntry noConvertauxCount : noConvertCountList) {
                    for (NoConverCountEntry otherAuxCount : otherCount.noConvertCountList) {
                        if (noConvertauxCount.getResUnit().equals(otherAuxCount.getResUnit())) {
                            noConvertauxCount.setCount(noConvertauxCount.getCount().add(otherAuxCount.getCount()));
                        }
                    }
                }
            } else if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {


                calcFloatQuantityByMasterUnit();

            } else {
                throw new IllegalArgumentException("not define UnitGorupTYpe");
            }
        }
    }


    public void masterCountChangeListener() {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            calcFloatQuantityByMasterUnit();
        }
        Events.instance().raiseEvent("storeResCountIsChanged");
    }

    private void calcFloatAuxUnit() {
        if ((count != null) && (count.compareTo(new BigDecimal("0")) != 0) && (floatConvertRate != null) && (floatConvertRate.doubleValue() != 0)) {
            setAuxCount(count.multiply(floatConvertRate));

        }
        Events.instance().raiseEvent("storeResCountIsChanged");
    }

    private void calcFloatQuantityByMasterUnit() {
        if ((count == null) || (count.compareTo(new BigDecimal("0")) == 0)) {
            setAuxCount(new BigDecimal(0));
            //setFloatConvertRate(new BigDecimal(0));
            return;
        }

        if ((floatConvertRate != null) && (floatConvertRate.compareTo(new BigDecimal("0")) != 0)) {
            setAuxCount(count.multiply(floatConvertRate));
            Events.instance().raiseEvent("storeResCountIsChanged");
        } else if ((auxCount != null) && (auxCount.doubleValue() != 0)) {
            setFloatConvertRate(auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void calcFloatQuantityByRate() {
        if ((floatConvertRate == null) || (floatConvertRate.compareTo(new BigDecimal("0")) == 0)) {
            setCount(new BigDecimal(0));
            Events.instance().raiseEvent("storeResCountIsChanged");
            setAuxCount(new BigDecimal(0));
            return;
        }

        if ((count != null) && (count.compareTo(new BigDecimal("0")) != 0)) {
            setAuxCount(count.multiply(floatConvertRate));
        } else if ((auxCount != null) && (auxCount.compareTo(new BigDecimal("0")) != 0)) {
            setCount(auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP));
        }
        calcFloatAuxUnit();
    }

    public void calcFloatQuantityByAuxUnit() {

        if ((auxCount == null) || (auxCount.compareTo(new BigDecimal("0")) == 0)) {
            setCount(new BigDecimal(0));
            Events.instance().raiseEvent("storeResCountIsChanged");
            //setFloatConvertRate(new BigDecimal(0));
            return;
        }

        if ((floatConvertRate != null) && (floatConvertRate.compareTo(new BigDecimal("0")) != 0)) {
            setCount(auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP));
        } else if ((count != null) && (count.compareTo(new BigDecimal("0")) != 0)) {
            setFloatConvertRate(auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP));
        }
        calcFloatAuxUnit();

    }

}


