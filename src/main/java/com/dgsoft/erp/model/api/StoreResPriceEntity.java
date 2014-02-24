package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:58 PM
 */
public abstract class StoreResPriceEntity extends StoreResCountEntity {


    @Override
    public BigDecimal getMasterCount() {

        if (getResUnit().isMasterUnit()) {
            return getCount();
        }

        switch (getResUnit().getUnitGroup().getType()) {
            case FLOAT_CONVERT:
                return BigDecimalFormat.format(getCount().multiply(getFloatConvertRate()),
                        getRes().getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());

            case FIX_CONVERT:
                return BigDecimalFormat.format(getCount().multiply(getResUnit().getConversionRate()), getResUnit().getCountFormate());
            default:
                return null;
        }
    }

    @Override
    public void setMasterCount(BigDecimal count) {
        if (getResUnit().isMasterUnit()) {
            setCount(count);
        }

        switch (getResUnit().getUnitGroup().getType()) {
            case FLOAT_CONVERT:
                setCount(BigDecimalFormat.format(count.multiply(getFloatConvertRate()), getResUnit().getCountFormate()));

            case FIX_CONVERT:
                setCount(BigDecimalFormat.format(
                        count.divide(
                                getResUnit().getConversionRate(), FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP), getResUnit().getCountFormate()));
            default:
                setCount(null);
        }
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    public abstract BigDecimal getCount();

    public abstract void setCount(BigDecimal count);

    public StoreResPriceEntity(Res res, Map<FormatDefine, List<Format>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        super(res, formatHistory, floatConvertRateHistory);
        setResUnit(getRes().getResUnitByMasterUnit());
    }

    public StoreResPriceEntity(Res res,
                               Map<FormatDefine, List<Format>> formatHistory,
                               List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(res, formatHistory, floatConvertRateHistory);
        setResUnit(defaultUnit);
    }

    public StoreResPriceEntity(StoreRes storeRes) {
        super(storeRes);
    }

    public StoreResPriceEntity(Res res, Map<FormatDefine, List<Format>> formatHistory) {
        super(res, formatHistory);
        setResUnit(getRes().getResUnitByMasterUnit());
    }

    public StoreResPriceEntity(Res res,
                               Map<FormatDefine, List<Format>> formatHistory, ResUnit defaultUnit) {
        super(res, formatHistory);
        setResUnit(defaultUnit);
    }

    public StoreResPriceEntity() {

    }

}
