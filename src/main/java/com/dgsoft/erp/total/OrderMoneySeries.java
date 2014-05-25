package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by cooper on 3/31/14.
 */
public class OrderMoneySeries {

    public OrderMoneySeries() {
    }

    public OrderMoneySeries(Object key) {
        this.key = key;
    }

    public OrderMoneySeries(Object key, BigDecimal money, Long count) {
        this.key = key;
        this.money = money;
        this.count = count;
    }

    public OrderMoneySeries(BigDecimal money, Long count) {
        this.count = count;
        this.money = money;
    }

    private Object key;

    private BigDecimal money;

    private Long count;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public BigDecimal getMoney() {
        if (money == null) {
            return DataFormat.halfUpCurrency(BigDecimal.ZERO);
        } else
            return DataFormat.halfUpCurrency(money);
    }

    public Long getCount() {
        if (count == null) {
            return new Long(0);
        } else
            return count;
    }

    public String getDisplayMoney() {
        if (money == null) {
            return "";
        }
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(Locale.CHINA);
        return currencyFormat.format(money);
    }
}
