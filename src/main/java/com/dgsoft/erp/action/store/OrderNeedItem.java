package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Res;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
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
    }

    public OrderNeedItem(Res res, BigDecimal floatConvertRate){
        this(res);
        storeResCount.setFloatConvertRate(floatConvertRate);
    }

    private BigDecimal unitPrice = new BigDecimal(0);

    private BigDecimal rebate = new BigDecimal(0);

    private BigDecimal totalPrice = new BigDecimal(0);

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

    private boolean decimalIsTyped(BigDecimal value) {
        return value != null && value.doubleValue() > 0;
    }

    private BigDecimal halfUpCurrency(BigDecimal number) throws ParseException {
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(Locale.CHINA);
        return new BigDecimal(currencyFormat.parse(currencyFormat.format(number)).toString());
    }

    private void calcByCU() {
        totalPrice = unitPrice.multiply(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).
                multiply(getStoreResCount().getCount());
        try {
            totalPrice = halfUpCurrency(totalPrice);
        } catch (ParseException e) {
            Logging.getLog(this.getClass()).warn("Number convert error CU");
        }
    }

    private void calcByCT() {

        unitPrice = totalPrice.divide(getStoreResCount().getCount(),
                Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),
                BigDecimal.ROUND_HALF_UP).divide(rebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP));
        try {
            unitPrice = halfUpCurrency(totalPrice);
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


    public void calcPriceByCount() {
        if ((getStoreResCount().getCount() == null) ||
                getStoreResCount().getCount().doubleValue() == 0) {
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
        if (decimalIsTyped(rebate)) {
            totalPrice = new BigDecimal(0);
            return;
        }
        if (decimalIsTyped(getStoreResCount().getCount())) {
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
        if (decimalIsTyped(getStoreResCount().getCount())) {
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
        if (decimalIsTyped(getStoreResCount().getCount())) {
            calcByCU();
        }else if(decimalIsTyped(totalPrice)){
            calcByUT();
        }
    }
}
