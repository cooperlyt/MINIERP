package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import org.jboss.seam.log.Logging;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/24/14
 * Time: 3:58 PM
 */
public abstract class StoreResPriceEntity extends StoreResCountEntity {

    protected StoreResPriceEntity() {
    }

    protected StoreResPriceEntity(Res res, ResUnit defaultUnit) {
        super(res, defaultUnit);
        setRebate(new BigDecimal("100"));
    }

    protected StoreResPriceEntity(StoreRes storeRes, ResUnit defaultUnit) {
        super(storeRes, defaultUnit);
        setRebate(new BigDecimal("100"));
    }

    public abstract String getType();

    public abstract String getMemo();

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    public abstract void setTotalMoney(BigDecimal money);

    public abstract BigDecimal getTotalMoney();

    public abstract boolean isPresentation();

    public abstract void setPresentation(boolean presentation);

    public abstract BigDecimal getRebate();

    public abstract void setRebate(BigDecimal rebate);

    public boolean isFree() {
        return isPresentation();
    }

    public abstract BigDecimal getSaleCount();

    public abstract void setSaleCount(BigDecimal saleCount);

    public void setFree(boolean free) {
        setPresentation(free);
        if (free) {
            setMoney(BigDecimal.ZERO);
            setTotalMoney(BigDecimal.ZERO);
            setRebate(new BigDecimal("100"));
        } else {
            calcTotalMoney();
        }
    }

    public BigDecimal getInputRebate() {
        return getRebate();
    }

    public void setInputRebate(BigDecimal rebate) {
        setRebate(rebate);
        calcTotalMoney();
    }

    @Override
    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        super.setFloatConvertRate(floatConvertRate);
        calcTotalMoney();
    }

    @Override
    public void setMasterCount(BigDecimal count) {
        setCount(count);
        calcTotalMoney();
    }

    @Override
    public ResUnit getUseUnit() {
        return getResUnit();
    }

    @Override
    public void setUseUnit(ResUnit useUnit) {
        setResUnit(useUnit);
        calcTotalMoney();
    }

    protected void calcSaleCount() {
        if (getUseUnit() != null)
            setSaleCount(getUseUnitCount());
    }

    protected void calcTotalMoney() {
        if (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(getMoney())) {
            setTotalMoney(getUseUnitCount().multiply(getRebateUnitPrice()));
        } else {
            setTotalMoney(BigDecimal.ZERO);
        }
    }

    public void calcMoney() {
        if (getCount() == null || getResUnit() == null || getMoney() == null || getRebateUnitPrice() == null) {
            Logging.getLog(getClass()).warn("param not enough can't calc");
            setTotalMoney(BigDecimal.ZERO);
            return;
            //throw new IllegalArgumentException("param not enough can't calc");
        }
        calcTotalMoney();
        calcSaleCount();
    }

    public void setInputMoney(BigDecimal money) {
        setMoney(money);
        calcTotalMoney();
    }

    public BigDecimal getInputMoney() {
        return getMoney();
    }

    public BigDecimal getRebateUnitPrice() {
        if (getMoney() != null) {
            if (getMoney().compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return getMoney().multiply(getRebate().divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP));
        } else
            return null;
    }


    @Override
    public boolean isSameItem(StoreResEntity otherEntity) {
        if (!(otherEntity instanceof StoreResPriceEntity)) {
            return false;
        }
        if ((getMoney() == null) || (getTotalMoney() == null)) {
            return false;
        }

        StoreResPriceEntity other = (StoreResPriceEntity) otherEntity;

        if ((getMoney() == null) || (other.getMoney() == null)) {
            return false;
        }

        if ((getUseUnit() == null) || other.getUseUnit() == null) {
            return false;
        }


        return super.isSameItem(other) && getUseUnit().equals(other.getUseUnit()) &&
                (isPresentation() == other.isPresentation()) &&
                getMoney().equals(other.getMoney()) && getRebate().equals(other.getRebate());
    }


}
