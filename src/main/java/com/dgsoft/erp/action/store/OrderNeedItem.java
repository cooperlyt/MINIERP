package com.dgsoft.erp.action.store;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.UnitGroup;

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

    private BigDecimal unitPrice = new BigDecimal("0");

    private BigDecimal rebate = new BigDecimal("100");

    private BigDecimal totalPrice = new BigDecimal("0");

    private boolean storeResItem;

    private ResUnit useUnit;

    private BigDecimal resCount;


    public Res getUseRes(){
        if (isStoreResItem()) {
            return getStoreRes().getRes();
        } else {
            return getRes();
        }
    }


    public boolean isStoreResItem() {
        return storeResItem;
    }

    public void setStoreResItem(boolean storeResItem) {
        this.storeResItem = storeResItem;
    }

    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }

    public BigDecimal getResCount() {
        return resCount;
    }

    public void setResCount(BigDecimal resCount) {
        this.resCount = resCount;
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

    public void merger(OrderNeedItem other) {
        if (!same(other)) {
            throw new IllegalArgumentException("not same orderItem can't merger");
        }
        if (storeResItem) {
            storeResCountInupt.add(other.storeResCountInupt);
        } else {
            resCount = resCount.add(other.getResCount());
        }
        calcByCU();
    }


    private boolean decimalIsTyped(BigDecimal value) {
        return value != null && (value.compareTo(new BigDecimal("0")) != 0);
    }

    private void calcByCU() {
        totalPrice = unitPrice.multiply(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).
                multiply(getCount());

        totalPrice = BigDecimalFormat.halfUpCurrency(totalPrice);

    }

    private void calcByCT() {

        unitPrice = totalPrice.divide(getCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP));

        unitPrice = BigDecimalFormat.halfUpCurrency(unitPrice);


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
        if (storeResItem) {
            return decimalIsTyped(getStoreResCountInupt().getCount());
        } else {
            return decimalIsTyped(resCount);
        }
    }

    public BigDecimal getCount() {
        if (storeResItem) {
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                return storeResCountInupt.getCount();
            } else {
                return storeResCountInupt.getCountByResUnit(useUnit);
            }

        } else {
            return resCount;
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

        if (storeResItem != other.isStoreResItem()) {
            return false;
        }

        if (other.getRebate().compareTo(getRebate()) != 0) {
            return false;
        }

        if (other.getUnitPrice().compareTo(getUnitPrice()) != 0) {
            return false;
        }

        if (!other.getUseUnit().getId().equals(getUseUnit().getId())) {
            return false;
        }

        if (storeResItem) {
            return super.same(storeChangeItem);

        } else {
            return res.getId().equals(other.getRes().getId());
        }

    }
}
