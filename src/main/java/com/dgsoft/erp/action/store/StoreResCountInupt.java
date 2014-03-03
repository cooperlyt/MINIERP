package com.dgsoft.erp.action.store;

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/23/13
 * Time: 11:34 AM
 */
@Deprecated
public class StoreResCountInupt extends ResCount {

    private Res res;

    private BigDecimal auxCount;

    public StoreResCountInupt(Res res) {
        super();
        this.res = res;
        init();
    }

    public StoreResCountInupt(StoreRes storeRes){
        this(storeRes.getRes(),storeRes.getRes().getUnitGroup().getMasterUnit());
        setFloatConvertRate(storeRes.getFloatConversionRate());
    }

    public StoreResCountInupt(Res res, ResUnit useUnit) {
        super();
        this.res = res;
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            this.useUnit = res.getUnitGroup().getMasterUnit();
        } else
            this.useUnit = useUnit;
        init();
    }

    private BigDecimal countFormat(BigDecimal count, ResUnit unit) {
        return DataFormat.format(count, unit.getCountFormate());
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


    @Override
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



    public Set<NoConvertCount> getNoConvertCounts() {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            Set<NoConvertCount> result = new HashSet<NoConvertCount>(noConvertCountList.size());
            for (NoConverCountEntry entry : noConvertCountList) {
                result.add(new NoConvertCount(entry.getResUnit(), entry.getCount()));
            }
            return result;
        } else
            return new HashSet<NoConvertCount>(0);
    }


    public void setMasterCount(BigDecimal count){
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)){
            this.count = DataFormat.format(count.divide(useUnit.getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), useUnit.getCountFormate());
        }else{
            this.count = count;
        }

        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            calcFloatAuxUnit();
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

    @Override
    public void add(ResCount otherCount) {
        super.add(otherCount);
        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            calcFloatQuantityByMasterUnit();
        }
    }

    @Override
    public void subtract(ResCount otherCount){
        super.subtract(otherCount);
        if (useUnit.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            calcFloatQuantityByMasterUnit();
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


