package com.dgsoft.erp.model.api;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;
import org.jboss.seam.log.Logging;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
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

    protected StoreResPriceEntity(StoreRes storeRes) {
        super(storeRes);
    }

    protected StoreResPriceEntity(Res res, Map<String, Set<Object>> formatHistory, ResUnit defaultUnit) {
        super(res, formatHistory, defaultUnit);
        setResUnit(defaultUnit);
    }

    protected StoreResPriceEntity(Res res, Map<String, Set<Object>> formatHistory) {
        super(res, formatHistory);
        setResUnit(res.getResUnitByMasterUnit());
    }

    protected StoreResPriceEntity(Res res, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory) {
        super(res, formatHistory, floatConvertRateHistory);
        setResUnit(res.getResUnitByMasterUnit());
    }

    protected StoreResPriceEntity(Res res, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(res, formatHistory, floatConvertRateHistory, defaultUnit);
        setResUnit(defaultUnit);
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

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
    public BigDecimal getTotalPrice() {

        if ((getMasterCount() == null) || (getMoney() == null)) {
            return null;
        }
        return getCountByResUnit(getUseUnit()).multiply(getMoney());
    }

    @Transient
    public void setTotalPrice(BigDecimal price) {
        if ((getMasterCount() == null) || (price == null)) {
            setMoney(null);
        } else
            setMoney(BigDecimalFormat.halfUpCurrency(price.divide(getCountByResUnit(getUseUnit()), MONEY_MAX_SCALE, BigDecimal.ROUND_HALF_UP)));
    }

}
