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
    }

    protected StoreResPriceEntity(StoreRes storeRes, Map<String, Set<Object>> formatHistory, List<BigDecimal> floatConvertRateHistory, ResUnit defaultUnit) {
        super(storeRes, formatHistory, floatConvertRateHistory, defaultUnit);
    }

    public abstract BigDecimal getMoney();

    public abstract void setMoney(BigDecimal money);

    public abstract ResUnit getResUnit();

    public abstract void setResUnit(ResUnit resUnit);

    public abstract void setTotalMoney(BigDecimal money);

    public abstract BigDecimal getTotalMoney();

    public abstract boolean isPresentation();

    public abstract void setPresentation(boolean presentation);

    private boolean scaleRebate;

    public boolean isScaleRebate() {
        return scaleRebate;
    }

    public void setScaleRebate(boolean scaleRebate) {
        this.scaleRebate = scaleRebate;
    }

    public boolean isFree() {
        return isPresentation();
    }

    public void setFree(boolean free) {
        setPresentation(free);
        if (free) {
            setMoney(BigDecimal.ZERO);
        }
    }

    @Override
    public void setMasterCount(BigDecimal count) {
        if (count == null) {
            setCount(null);
            setTotalMoney(null);
            return;
        }
        BigDecimal oldRebateUnitPrice = null;
        if (!count.equals(this.getCount())) {
            oldRebateUnitPrice = getRebateUnitPrice();
        }

        setCount(count);

        if (oldRebateUnitPrice != null) {
            setTotalMoney(getUseUnitCount().multiply(oldRebateUnitPrice));
        }else{
            setTotalMoney(null);
        }
    }

    @Override
    public ResUnit getUseUnit() {
        return getResUnit();
    }

    @Override
    public void setUseUnit(ResUnit useUnit) {
        if (useUnit == null) {
            setResUnit(null);
            setTotalMoney(null);
            return;
        }
        BigDecimal oldRebateUnitPrice = null;
        if (!useUnit.getId().equals(getUseUnit().getId())) {
            oldRebateUnitPrice = getRebateUnitPrice();
        }

        setResUnit(useUnit);
        if ((getCount() != null) && (oldRebateUnitPrice != null)) {
            setTotalMoney(getUseUnitCount().multiply(oldRebateUnitPrice));
        }else{
            setTotalMoney(null);
        }
    }

    public void setInputMoney(BigDecimal money) {
        if (money == null) {
            setMoney(null);
            setTotalMoney(null);
            return;
        }

        BigDecimal moneyRebate = null;
        if (!money.equals(getMoney())) {
            moneyRebate = getMoneyRebate();
        }


        setMoney(money);

        if ((getCount() != null) && (moneyRebate != null)) {
            setTotalMoney(getUseUnitCount().multiply(money.subtract(moneyRebate)));
        }else{
            setTotalMoney(null);
        }


    }

    public BigDecimal getInputMoney() {
        return getMoney();
    }


    public BigDecimal getScaleRebate() {
        if ((getCount() == null) || (getMoney() == null) || (getTotalMoney() == null)) {
            return null;
        }
        BigDecimal rebateUnitPrice = getRebateUnitPrice();
        if (rebateUnitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return rebateUnitPrice.divide(getMoney(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }

    public void setScaleRebate(BigDecimal rebate) {

        if (DataFormat.isEmpty(rebate)) {
            setTotalMoney(BigDecimal.ZERO);
        }
        if ((getUseUnit() != null) && (getMoney() != null) && (getCount() != null)) {
            setTotalMoney(getMoney().multiply(rebate.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP)).multiply(getUseUnitCount()));
        }
        setTotalMoney(null);
    }

    public BigDecimal getMoneyRebate() {
        if ((getCount() == null) || (getMoney() == null) || (getTotalMoney() == null)) {
            return null;
        }
        return getMoney().subtract(getRebateUnitPrice());
    }

    public void setMoneyRebate(BigDecimal rebate) {
        if (rebate == null) {
            setTotalMoney(BigDecimal.ZERO);
        }
        if ((rebate != null) &&
                (getUseUnit() != null) &&
                (getMoney() != null) && (getCount() != null)) {
            setTotalMoney(getUseUnitCount().multiply(getMoney().subtract(rebate)));
        }
        setTotalMoney(null);
    }


    public BigDecimal getRebateUnitPrice() {
        if ((getCount() == null) || (getTotalMoney() == null)) {
            return null;
        }
        if (getTotalMoney().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getTotalMoney().divide(getUseUnitCount(), Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
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


        return super.isSameItem(other) && getUseUnit().equals(other.getUseUnit()) &&
                (isPresentation() == other.isPresentation()) &&
                getMoney().equals(other.getMoney()) && getMoneyRebate().equals(other.getMoneyRebate());
    }

    //-----------------------calc


//    private BigDecimal calcUnitPrice(BigDecimal useRebate) {
//        BigDecimal result = inputTotalPrice.divide(getUseUnitCount(),
//                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
//                BigDecimal.ROUND_HALF_UP).divide(useRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
//                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
//                BigDecimal.ROUND_HALF_UP);
//
//        result = DataFormat.halfUpCurrency(result);
//        return result;
//    }

//    private boolean canCalcTotalPrice() {
//        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(getRebate()));
//    }
//
//    private void calcTotalPrice() {
//        if (canCalcTotalPrice())
//            inputTotalPrice = DataFormat.halfUpCurrency(getRebateUnitPrice().multiply(getUseUnitCount()));
//    }
//
//    private boolean canClacRebate() {
//        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(inputTotalPrice));
//    }
//
//    private void calcRebate() {
//        if (canClacRebate())
//            setRebate(calcUnitPrice(new BigDecimal("100")).divide(getMoney(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
//    }
//
//    private boolean canCalcUnitPrice() {
//        return (!DataFormat.isEmpty(getCount()) && !DataFormat.isEmpty(inputTotalPrice) && !DataFormat.isEmpty(getRebate()));
//    }
//
//    private void calcUnitPrice() {
//        if (canCalcUnitPrice())
//            setMoney(calcUnitPrice(getRebate()));
//    }
//
//    private boolean canCalcCount() {
//        return (!DataFormat.isEmpty(getMoney()) && !DataFormat.isEmpty(inputTotalPrice) && !DataFormat.isEmpty(getRebate()));
//    }
//
//    private void calcCount() {
//        if (canCalcCount())
//            setUseUnitCount(
//                    inputTotalPrice.divide(getMoney().divide(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
//                            Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
//                            BigDecimal.ROUND_HALF_UP), 20, BigDecimal.ROUND_HALF_UP));
//    }
//
//
//    private void calcPriceByCount() {
//        if (DataFormat.isEmpty(getCount())) {
//            inputTotalPrice = new BigDecimal(0);
//            return;
//        }
//
//        if (canCalcTotalPrice()) {
//            calcTotalPrice();
//        } else if (canCalcUnitPrice()) {
//            calcUnitPrice();
//        } else if (canClacRebate()) {
//            calcRebate();
//        }
//
//    }
//
//
//    private void calcPriceByRebate() {
//        if (DataFormat.isEmpty(getRebate())) {
//            inputTotalPrice = new BigDecimal(0);
//            return;
//        }
//
//        if (canCalcTotalPrice()) {
//            calcTotalPrice();
//        } else if (canCalcUnitPrice()) {
//            calcUnitPrice();
//        } else if (canCalcCount()) {
//            calcCount();
//        }
//    }
//
//    private void calcPriceByTotalPrice() {
//        if (DataFormat.isEmpty(inputTotalPrice)) {
//            setMoney(BigDecimal.ZERO);
//            return;
//        }
//
//        if (canClacRebate()) {
//            calcRebate();
//        } else if (canCalcUnitPrice()) {
//            calcUnitPrice();
//        } else if (canCalcCount()) {
//            calcCount();
//        }
//
//    }
//
//    private void calcPriceByUnitPrice() {
//
//        if (DataFormat.isEmpty(getMoney())) {
//            inputTotalPrice = new BigDecimal(0);
//            return;
//        }
//        if (canCalcTotalPrice()) {
//            calcTotalPrice();
//        } else if (canClacRebate()) {
//            calcRebate();
//        } else if (canCalcCount()) {
//            calcCount();
//        }
//    }

}
