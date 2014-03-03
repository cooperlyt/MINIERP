package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    protected StoreResPriceEntity(Res res, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(res, formatHistory, floatConvertRateHistory, defaultUnit);
    }

    protected StoreResPriceEntity(StoreRes storeRes, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(storeRes, formatHistory, floatConvertRateHistory, defaultUnit);
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    @Override
    public ResUnit getUseUnit() {
        return getResUnit();
    }

    @Override
    public void setUseUnit(ResUnit useUnit) {
        setResUnit(useUnit);
    }

    public BigDecimal getMoneyUnitCount() {
        return getCountByResUnit(getUseUnit());
    }

    public BigDecimal getTotalPrice() {

        if ((getMasterCount() == null) || (getMoney() == null)) {
            return null;
        }
        return getCountByResUnit(getUseUnit()).multiply(getMoney());
    }

    public void setTotalPrice(BigDecimal price) {
        if ((getMasterCount() == null) || (price == null)) {
            setMoney(null);
        } else
            setMoney(DataFormat.halfUpCurrency(price.divide(getCountByResUnit(getUseUnit()), MONEY_MAX_SCALE, BigDecimal.ROUND_HALF_UP)));
    }

    public boolean isSameItem(StoreResPriceEntity other) {
        return super.isSameItem(other) && getUseUnit().equals(other.getUseUnit()) && getMoney().equals(other.getMoney());
    }

}
