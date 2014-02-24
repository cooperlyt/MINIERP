package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:58 PM
 */
public abstract class StoreResPriceEntity extends StoreResCountEntity {

    protected static final int MONEY_MAX_SCALE = 10;


    protected StoreResPriceEntity() {
    }

    protected StoreResPriceEntity(StoreRes storeRes) {
        super(storeRes);
    }

    protected StoreResPriceEntity(Res res, Map<String, List<Object>> formatHistory, ResUnit defaultUnit) {
        super(res, formatHistory, defaultUnit);
    }

    protected StoreResPriceEntity(Res res, Map<String, List<Object>> formatHistory) {
        super(res, formatHistory);
    }

    protected StoreResPriceEntity(Res res, Map<String, List<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        super(res, formatHistory, floatConvertRateHistory);
    }

    protected StoreResPriceEntity(Res res, Map<String, List<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(res, formatHistory, floatConvertRateHistory, defaultUnit);
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    public abstract BigDecimal getCount();

    public abstract void setCount(BigDecimal count);


    @Override
    @Transient
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
    @Transient
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

    @Override
    @Transient
    public ResUnit getUseUnit() {
        return getResUnit();
    }

    @Override
    @Transient
    public void setUseUnit(ResUnit useUnit) {
        setResUnit(useUnit);
    }

    @Transient
    public BigDecimal getTotalPrice(){
       return getCount().multiply(getMoney());
    }

    @Transient
    public void setTotalPrice(BigDecimal price){
       setMoney(BigDecimalFormat.halfUpCurrency(price.divide(getCount(), MONEY_MAX_SCALE, BigDecimal.ROUND_HALF_UP)));
    }

}
