package com.dgsoft.erp.action.store;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/1/13
 * Time: 9:32 AM
 */
public class OrderNeedItem extends StoreChangeItem {

    public OrderNeedItem(Res res) {
        super(res, res.getResUnitByOutDefault());
        useUnit = res.getResUnitByOutDefault();
    }

    public OrderNeedItem(Res res, BigDecimal floatConvertRate) {
        this(res);
        storeResCountInupt.setFloatConvertRate(floatConvertRate);
    }

    public OrderNeedItem(StoreRes storeRes, ResUnit useUnit,
                         BigDecimal resCount, BigDecimal unitPrice, BigDecimal rebate) {
        super(storeRes.getRes(), useUnit);
        storeResCountInupt.setFloatConvertRate(storeRes.getFloatConversionRate());
        storeResCountInupt.setCount(resCount);
        this.storeRes = storeRes;
        this.useUnit = useUnit;

        this.unitPrice = unitPrice;
        this.rebate = rebate;
        calcByCU();
    }

    private BigDecimal unitPrice = BigDecimal.ZERO;

    private BigDecimal rebate = new BigDecimal("100");

    private BigDecimal totalPrice = BigDecimal.ZERO;

    private ResUnit useUnit;



    private String memo;


    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }



    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getRebate() {
        return rebate;
    }

    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void merger(OrderNeedItem other) {
        if (!same(other)) {
            throw new IllegalArgumentException("not same orderItem can't merger");
        }

        storeResCountInupt.add(other.storeResCountInupt);

        calcByCU();
    }

    public boolean isZero() {

        return (storeResCountInupt.getMasterCount().compareTo(BigDecimal.ZERO) <= 0);

    }


    private boolean decimalIsTyped(BigDecimal value) {
        return value != null && (value.compareTo(new BigDecimal("0")) != 0);
    }

    private void calcByCU() {
        totalPrice = unitPrice.multiply(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).
                multiply(getCount());

        totalPrice = BigDecimalFormat.halfUpCurrency(totalPrice);

    }

    private BigDecimal calcUnitPrice(BigDecimal useRebate) {
        BigDecimal result = totalPrice.divide(getCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(useRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP);

        result = BigDecimalFormat.halfUpCurrency(result);
        return result;

    }

    private void calcByCT() {
        if (!decimalIsTyped(unitPrice)) {
            unitPrice = calcUnitPrice(rebate);
        } else {

            rebate = calcUnitPrice(new BigDecimal("100")).divide(unitPrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        }
    }

    private void calcByCR() {
        if (decimalIsTyped(unitPrice)) {
            calcByCU();
        } else if (decimalIsTyped(totalPrice)) {
            calcByCT();
        }
    }

    private void calcByUT() {
        getStoreResCountInupt().setCount(
                totalPrice.divide(unitPrice.divide(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
                        Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                        BigDecimal.ROUND_HALF_UP), 20, BigDecimal.ROUND_HALF_UP));
    }

    private boolean countIsTyped() {

        return decimalIsTyped(getStoreResCountInupt().getCount());

    }

    public BigDecimal getCount() {

        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
            return storeResCountInupt.getCount();
        } else {
            return storeResCountInupt.getCountByResUnit(useUnit);
        }


    }


    public void calcPriceByCount() {
        if (!countIsTyped()) {
            totalPrice = new BigDecimal(0);
            return;
        }

        if (decimalIsTyped(unitPrice)) {
            calcByCU();
        } else if (decimalIsTyped(totalPrice)) {
            calcByCT();
        }

    }

    public void calcPriceByRebate() {
        if (!decimalIsTyped(rebate)) {
            totalPrice = new BigDecimal(0);
            return;
        }
        if (countIsTyped()) {
            calcByCR();
        } else if (decimalIsTyped(unitPrice) && decimalIsTyped(totalPrice)) {
            calcByUT();
        }
    }

    public void calcPriceByTotalPrice() {
        if (!decimalIsTyped(totalPrice)) {
            unitPrice = new BigDecimal(0);
            return;
        }
        if (countIsTyped()) {
            calcByCT();
        } else if (decimalIsTyped(unitPrice)) {
            calcByUT();
        }

    }

    public void calcPriceByUnitPrice() {

        if (!decimalIsTyped(unitPrice)) {
            totalPrice = new BigDecimal(0);
            return;
        }
        if (countIsTyped()) {
            calcByCU();
        } else if (decimalIsTyped(totalPrice)) {
            calcByUT();
        }
    }

    @Override
    public List<Format> getFormats() {
        if ((storeRes != null))
            return storeRes.getFormatList();
        else
            return new ArrayList<Format>(0);
    }

    @Override
    public boolean same(StoreChangeItem storeChangeItem) {
        if (!(storeChangeItem instanceof OrderNeedItem)) {
            return false;
        }
        OrderNeedItem other = (OrderNeedItem) storeChangeItem;


        if (other.getRebate().compareTo(getRebate()) != 0) {
            return false;
        }

        if (other.getUnitPrice().compareTo(getUnitPrice()) != 0) {
            return false;
        }

        if (!other.getUseUnit().getId().equals(getUseUnit().getId())) {
            return false;
        }


        return super.same(storeChangeItem);


    }
}
