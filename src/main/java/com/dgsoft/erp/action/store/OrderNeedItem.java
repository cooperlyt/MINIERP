package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
        storeResCount.setFloatConvertRate(floatConvertRate);
    }

    private BigDecimal unitPrice = new BigDecimal("0");

    private BigDecimal rebate = new BigDecimal("100");

    private BigDecimal totalPrice = new BigDecimal("0");

    private boolean storeResItem;

    private ResUnit useUnit;

    private BigDecimal resCount;

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
        if (storeResItem){
            storeResCount.add(other.storeResCount);
        }else{
            resCount = resCount.add(other.getResCount());
        }
        calcByCU();
    }


    private boolean decimalIsTyped(BigDecimal value) {
        return value != null && (value.compareTo(new BigDecimal("0")) != 0);
    }

    private BigDecimal halfUpCurrency(BigDecimal number) throws ParseException {
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(Locale.CHINA);
        return new BigDecimal(currencyFormat.parse(currencyFormat.format(number)).toString());
    }

    private void calcByCU() {
        totalPrice = unitPrice.multiply(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).
                multiply(getCount());
        try {
            totalPrice = halfUpCurrency(totalPrice);
        } catch (ParseException e) {
            Logging.getLog(this.getClass()).warn("Number convert error CU");
        }
    }

    private void calcByCT() {

        unitPrice = totalPrice.divide(getCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP));
        try {
            unitPrice = halfUpCurrency(unitPrice);
        } catch (ParseException e) {
            Logging.getLog(this.getClass()).warn("Number convert error CT");
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
        getStoreResCount().setCount(
                totalPrice.divide(unitPrice.divide(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP),
                        Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                        BigDecimal.ROUND_HALF_UP), 20, BigDecimal.ROUND_HALF_UP));
    }

    private boolean countIsTyped(){
        if (storeResItem){
            return decimalIsTyped(getStoreResCount().getCount());
        }else{
            return decimalIsTyped(resCount);
        }
    }

    public BigDecimal getCount(){
        if (storeResItem){
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)){
                return storeResCount.getCount();
            }else{
                return storeResCount.getCountByResUnit(useUnit);
            }

        }else{
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

        if (other.getUnitPrice().compareTo(getUnitPrice()) != 0){
            return false;
        }

        if (!other.getUseUnit().getId().equals(getUseUnit().getId())){
            return false;
        }

        if (storeResItem) {
            return super.same(storeChangeItem);

        } else {
            return res.getId().equals(other.getRes().getId());
        }

    }
}
