package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.NoConvertCount;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/10/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResCount implements java.io.Serializable {

    public static final ResCount ZERO = new ResCount(BigDecimal.ZERO);

    protected static final int FLOAT_CONVERT_SCALE = 10;


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


    protected BigDecimal count;

    protected ResUnit useUnit;

    protected BigDecimal floatConvertRate;

    protected List<NoConverCountEntry> noConvertCountList;

    private ResCount(BigDecimal count){
        this.count = count;
    }

    protected ResCount() {

    }

    public ResCount(BigDecimal masterCount, UnitGroup unitGroup) {
        if (!unitGroup.getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            throw new IllegalArgumentException("unitGroup must be FIX_CONVERT");
        }
        this.count = masterCount;
        this.useUnit = unitGroup.getMasterUnit();

    }

    public ResCount(BigDecimal count, ResUnit unit) {
        if (unit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("unitGroup must be FIX_CONVERT");
        }

        if (unit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)){
            this.count = count;
            this.useUnit = unit;
        }else{
            useUnit = unit.getUnitGroup().getMasterUnit();
            if (unit.isMasterUnit()){
                this.count = count;
                noConvertCountList = new ArrayList<NoConverCountEntry>(0);
            }else{
                this.count = BigDecimal.ZERO;
                noConvertCountList = new ArrayList<NoConverCountEntry>(1);
                noConvertCountList.add(new NoConverCountEntry(unit,count));
            }
        }


    }

    public ResCount(BigDecimal masterCount, UnitGroup unitGroup, BigDecimal floatConvertRate) {
        if (!unitGroup.getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("unitGroup must be floatConvertRate");
        }
        this.count = masterCount;
        this.useUnit = unitGroup.getMasterUnit();
        this.floatConvertRate = floatConvertRate;
    }

    public ResCount(BigDecimal count, ResUnit unit, BigDecimal floatConvertRate) {
        if (!unit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("unitGroup must be floatConvertRate");
        }

        this.useUnit = unit.getUnitGroup().getMasterUnit();
        this.floatConvertRate = floatConvertRate;
        if (unit.isMasterUnit()) {
            this.count = count;
        } else {

            this.count = count.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
            this.count = BigDecimalFormat.format(this.count, useUnit.getCountFormate());
        }
    }

    public ResCount(BigDecimal masterCount,UnitGroup unitGroup,List<NoConvertCount> noConvertCounts){
        if (!unitGroup.getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            throw new IllegalArgumentException("unitGroup must be NO_CONVERT");
        }
        this.count = masterCount;
        this.useUnit = unitGroup.getMasterUnit();
        noConvertCountList = new ArrayList<NoConverCountEntry>();
        for (NoConvertCount ncc: noConvertCounts){
            noConvertCountList.add(new NoConverCountEntry(ncc.getResUnit(),ncc.getCount()));
        }
        Collections.sort(noConvertCountList,new Comparator<NoConverCountEntry>() {
            @Override
            public int compare(NoConverCountEntry o1, NoConverCountEntry o2) {
                return new Integer(o1.getResUnit().getPriority()).compareTo(o2.getResUnit().getPriority());
            }
        });
    }


    public BigDecimal getMasterCount() {
        if (count.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }
        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            return count.multiply(useUnit.getConversionRate());
        } else {
            return count;
        }
    }

    public BigDecimal getAuxCount() {
        if (count.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }
        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return BigDecimalFormat.format(count.multiply(floatConvertRate),
                    useUnit.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
        } else

            throw new IllegalStateException("only float convert unit can call this method");
    }


    public BigDecimal getCountByResUnit(ResUnit resUnit) {
        if (count.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }
        if (resUnit.getId().equals(useUnit.getId())) {
            return count;
        }
        switch (useUnit.getUnitGroup().getType()) {
            case FLOAT_CONVERT:
                if (resUnit.isMasterUnit()) {
                    return getMasterCount();
                } else {
                    //calcFloatQuantityByMasterUnit();
                    return getAuxCount();
                }
            case FIX_CONVERT:
                return BigDecimalFormat.format(getMasterCount().divide(resUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), resUnit.getCountFormate());
            case NO_CONVERT:
                if (resUnit.isMasterUnit()) {
                    return count;
                } else {
                    for (NoConverCountEntry entry : noConvertCountList) {
                        if (entry.getResUnit().getId().equals(resUnit.getId())) {
                            return entry.getCount();
                        }
                    }
                }
            default:
                return null;
        }
    }

    public List<NoConverCountEntry> getNoConvertCountList() {
        return noConvertCountList;
    }

    public String getDisplayAuxCount() {
        if (count.equals(BigDecimal.ZERO)){
            return "";
        }
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_UP);
        String result;
        switch (useUnit.getUnitGroup().getType()) {
            case FIX_CONVERT:

                result = "";
                for (ResUnit resUnit : useUnit.getUnitGroup().getResUnitList()) {
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
                df.applyPattern(useUnit.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
                return df.format(getAuxCount()) + " " + useUnit.getUnitGroup().getFloatAuxiliaryUnit().getName();
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

    public boolean canMerger(ResCount resCount){
        if (!resCount.useUnit.getUnitGroup().getId().equals(useUnit.getUnitGroup().getId())){
            return false;
        }

        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)){
            if (resCount.noConvertCountList.size() != noConvertCountList.size()){
                return false;
            }
            if (!getSingleNoConverCount().getResUnit().equals(resCount.getSingleNoConverCount().getResUnit())){
                return false;
            }
        }

        if ((useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                && (resCount.getFloatConvertRate().compareTo(floatConvertRate) != 0))) {
           return false;
        }

        return true;
    }

    public NoConverCountEntry getSingleNoConverCount(){
        if (!useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)){
            throw new IllegalStateException("only noConverCount can call this method");
        }
        if (BigDecimal.ZERO.equals(count)){
            return noConvertCountList.get(0);
        }else{
            return new NoConverCountEntry(useUnit,count);
        }
    }

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public String getMasterDisplayCount() {
        if (count.equals(BigDecimal.ZERO)){
            return "";
        }
        DecimalFormat df = new DecimalFormat(useUnit.getUnitGroup().getMasterUnit().getCountFormate());
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(getMasterCount()) + " " + useUnit.getUnitGroup().getMasterUnit().getName();
    }

    public void subtract(ResCount otherCount){
        if (!canMerger(otherCount)){
            throw new IllegalArgumentException("not seam unit cant subtract");
        }


        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            count = BigDecimalFormat.format(count.subtract(otherCount.getMasterCount().divide(useUnit.getConversionRate(),FLOAT_CONVERT_SCALE,BigDecimal.ROUND_HALF_UP)),useUnit.getCountFormate());

        } else {
            count = BigDecimalFormat.format(count.subtract(otherCount.count),useUnit.getCountFormate());


            if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                if (noConvertCountList.size() != otherCount.noConvertCountList.size()){
                    throw new IllegalArgumentException("NO ConvertRate Unit count aux unit not same");
                }
                for (NoConverCountEntry noConvertauxCount : noConvertCountList) {
                    for (NoConverCountEntry otherAuxCount : otherCount.noConvertCountList) {
                        if (noConvertauxCount.getResUnit().equals(otherAuxCount.getResUnit())) {
                            noConvertauxCount.setCount(noConvertauxCount.getCount().subtract(otherAuxCount.getCount()));
                        }
                    }
                }
            }
        }


    }

    public void add(ResCount otherCount) {
        if (!canMerger(otherCount)){
            throw new IllegalArgumentException("not seam unit cant add");
        }

        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
           count = BigDecimalFormat.format(count.add( otherCount.getMasterCount().divide(useUnit.getConversionRate(),FLOAT_CONVERT_SCALE,BigDecimal.ROUND_HALF_UP)),useUnit.getCountFormate());

        } else {
            count = BigDecimalFormat.format(count.add(otherCount.count),useUnit.getCountFormate());


            if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                if (noConvertCountList.size() != otherCount.noConvertCountList.size()){
                    throw new IllegalArgumentException("NO ConvertRate Unit count aux unit not same");
                }
                for (NoConverCountEntry noConvertauxCount : noConvertCountList) {
                    for (NoConverCountEntry otherAuxCount : otherCount.noConvertCountList) {
                        if (noConvertauxCount.getResUnit().equals(otherAuxCount.getResUnit())) {
                            noConvertauxCount.setCount(noConvertauxCount.getCount().add(otherAuxCount.getCount()));
                        }
                    }
                }
            }
        }
    }

}
