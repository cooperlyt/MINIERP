package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
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
        this.useUnit = useUnit;
        init();
    }

    public Res getRes() {
        return res;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
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
        this.auxCount = auxCount;
    }

    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
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

    public BigDecimal getMasterCount() {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            return count.multiply(useUnit.getConversionRate());
        } else {
            return count;
        }
    }

    public void init() {
        count = new BigDecimal(0);
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            useUnit = res.getResUnitByMasterUnit();
            floatConvertRate = res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate();
            auxCount = new BigDecimal(0);
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
                && (otherCount.getFloatConvertRate() != floatConvertRate))) {
            throw new IllegalArgumentException("not same storeInItem can't merger");
        }

        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            count = count.add(useUnit.getConversionRate().multiply(otherCount.getMasterCount()));
            System.out.println(useUnit.getConversionRate().toPlainString() + "*"
                    + otherCount.getMasterCount());
        } else {
            this.count.add(otherCount.getCount());
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                for (NoConverCountEntry auxCount : noConvertCountList) {
                    for (NoConverCountEntry otherAuxCount : otherCount.noConvertCountList) {
                        if (auxCount.getResUnit().equals(otherAuxCount.getResUnit())) {
                            auxCount.setCount(auxCount.getCount().add(otherAuxCount.getCount()));
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


    public void calcFloatQuantityByMasterUnit() {
        if ((count == null) || (count.doubleValue() == 0)) {
            auxCount = new BigDecimal(0);
            floatConvertRate = new BigDecimal(0);
            return;
        }

        if ((floatConvertRate != null) && (floatConvertRate.doubleValue() != 0)) {
            auxCount = count.multiply(floatConvertRate);
        } else if ((auxCount != null) && (auxCount.doubleValue() != 0)) {
            floatConvertRate = auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
    }

    public void calcFloatQuantityByRate() {
        if ((floatConvertRate == null) || (floatConvertRate.doubleValue() == 0)) {
            count = new BigDecimal(0);
            auxCount = new BigDecimal(0);
            return;
        }

        if ((count != null) && (count.doubleValue() != 0)) {
            auxCount = count.multiply(floatConvertRate);
        } else if ((auxCount != null) && (auxCount.doubleValue() != 0)) {
            count = auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }

    }

    public void calcFloatQuantityByAuxUnit() {
        if ((auxCount == null) || (auxCount.doubleValue() == 0)) {
            count = new BigDecimal(0);
            floatConvertRate = new BigDecimal(0);
            return;
        }

        if ((floatConvertRate != null) && (floatConvertRate.doubleValue() != 0)) {
            count = auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        } else if ((count != null) && (count.doubleValue() != 0)) {
            floatConvertRate = auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
    }

}


