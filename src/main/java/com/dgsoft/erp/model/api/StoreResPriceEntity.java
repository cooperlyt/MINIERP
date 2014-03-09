package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.*;

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
        setRebate(new BigDecimal("100"));
    }

    protected StoreResPriceEntity(StoreRes storeRes, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(storeRes, formatHistory, floatConvertRateHistory, defaultUnit);
        setRebate(new BigDecimal("100"));
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    public abstract void setRebate(BigDecimal rebate);

    public abstract BigDecimal getRebate();

    public void setInputMoney(BigDecimal money) {
        setMoney(money);
        calcPriceByUnitPrice();
    }

    public BigDecimal getInputMoney() {
        return getMoney();
    }

    public void setInputRebate(BigDecimal rebate) {
        setRebate(rebate);
        calcPriceByRebate();
    }

    public BigDecimal getInputRebate() {
        return getRebate();
    }

    protected BigDecimal inputTotalPrice = null;

    public BigDecimal getInputTotalPrice() {
        return inputTotalPrice;
    }

    public void setInputTotalPrice(BigDecimal inputTotalPrice) {
        this.inputTotalPrice = inputTotalPrice;
        calcPriceByTotalPrice();
    }

    @Override
    public void setMasterCount(BigDecimal count) {
        setCount(count);
        calcPriceByCount();
    }

    @Override
    public ResUnit getUseUnit() {
        return getResUnit();
    }

    @Override
    public void setUseUnit(ResUnit useUnit) {
        setResUnit(useUnit);
        calcPriceByCount();
    }

    public BigDecimal getMoneyUnitCount() {
        if (getMasterCount() == null) {
            return null;
        }
        return getCountByResUnit(getUseUnit());
    }

    public BigDecimal getRebateUnitPrice() {
        if ((getRebate() == null) || (getMoney() == null)) {
            return null;
        }
        return DataFormat.halfUpCurrency(getMoney().multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public BigDecimal getTotalPrice() {
        if ((getMasterCount() == null) || (getMoney() == null)) {
            return null;
        }
        return getMoneyUnitCount().multiply(getRebateUnitPrice());
    }


    public boolean isSameItem(StoreResPriceEntity other) {
        return super.isSameItem(other) && getUseUnit().equals(other.getUseUnit()) &&
                getMoney().equals(other.getMoney()) && getRebate().equals(other.getRebate());
    }

    //-----------------------calc


    private BigDecimal calcUnitPrice(BigDecimal useRebate) {
        BigDecimal result = inputTotalPrice.divide(getUseUnitCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(useRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP);

        result = DataFormat.halfUpCurrency(result);
        return result;
    }

    private boolean canCalcTotalPrice() {
        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(getRebate()));
    }

    private void calcTotalPrice() {
        if (canCalcTotalPrice())
            inputTotalPrice = DataFormat.halfUpCurrency(getRebateUnitPrice().multiply(getUseUnitCount()));
    }

    private boolean canClacRebate() {
        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(inputTotalPrice));
    }

    private void calcRebate() {
        if (canClacRebate())
            setRebate(calcUnitPrice(new BigDecimal("100")).divide(getMoney(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
    }

    private boolean canCalcUnitPrice() {
        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(inputTotalPrice) && !DataFormat.isEmpty(getRebate()));
    }

    private void calcUnitPrice() {
        if (canCalcUnitPrice())
            setMoney(calcUnitPrice(getRebate()));
    }

    private boolean canCalcCount() {
        return (!DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(inputTotalPrice) && !DataFormat.isEmpty(getRebate()));
    }

    private void calcCount() {
        if (canCalcCount())
            setUseUnitCount(
                    inputTotalPrice.divide(getMoney().divide(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
                            Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                            BigDecimal.ROUND_HALF_UP), 20, BigDecimal.ROUND_HALF_UP));
    }


    private void calcPriceByCount() {
        if (DataFormat.isEmpty(getCount())) {
            inputTotalPrice = new BigDecimal(0);
            return;
        }

        if (canCalcTotalPrice()) {
            calcTotalPrice();
        } else if (canCalcUnitPrice()) {
            calcUnitPrice();
        } else if (canClacRebate()) {
            calcRebate();
        }

    }


    private void calcPriceByRebate() {
        if (DataFormat.isEmpty(getRebate())) {
            inputTotalPrice = new BigDecimal(0);
            return;
        }

        if (canCalcTotalPrice()) {
            calcTotalPrice();
        } else if (canCalcUnitPrice()) {
            calcUnitPrice();
        } else if (canCalcCount()) {
            calcCount();
        }
    }

    private void calcPriceByTotalPrice() {
        if (DataFormat.isEmpty(inputTotalPrice)) {
            setMoney(BigDecimal.ZERO);
            return;
        }

        if (canClacRebate()) {
            calcRebate();
        } else if (canCalcUnitPrice()) {
            calcUnitPrice();
        } else if (canCalcCount()) {
            calcCount();
        }

    }

    private void calcPriceByUnitPrice() {

        if (DataFormat.isEmpty(getMoney())) {
            inputTotalPrice = new BigDecimal(0);
            return;
        }
        if (canCalcTotalPrice()) {
            calcTotalPrice();
        } else if (canClacRebate()) {
            calcRebate();
        } else if (canCalcCount()) {
            calcCount();
        }
    }

}
