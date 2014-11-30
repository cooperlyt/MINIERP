package com.dgsoft.erp.total.data;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.ResPriceGroupStrategy;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by cooper on 11/14/14.
 */
public class ResPriceTotal implements TotalDataGroup.GroupTotalData{


    public static ResPriceTotal total(Collection<? extends StoreResPriceEntity> datas) {
        ResPriceTotal result = null;
        for (StoreResPriceEntity data : datas) {
            if (result == null) {
                result = new ResPriceTotal(data.getRes());
            }
            result.putItem(data);
        }
        return result;
    }

    protected Res res;

    public ResPriceTotal(Res res) {
        this.res = res;
        resCount = ResTotalCount.ZERO(res);
    }

    private ResCount resCount;

    private BigDecimal money = BigDecimal.ZERO;

    private int count = 0;

    public Res getRes() {
        return res;
    }

    public ResCount getResCount() {
        return resCount;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getMoneyAVG() {
        if (BigDecimal.ZERO.equals(money) || BigDecimal.ZERO.equals(count)) {
            return BigDecimal.ZERO;
        }
        NumberFormat format = DecimalFormat.getCurrencyInstance(Locale.CHINA);
        return money.divide(new BigDecimal(count), format.getMaximumFractionDigits(), format.getRoundingMode());
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void putItem(StoreResPriceEntity other) {
        if (!res.equals(other.getRes())) {
            throw new IllegalArgumentException("only same res");
        }
        resCount = resCount.add(other.getStoreResCount());
        money = money.add(other.getTotalMoney());
        if (BigDecimal.ZERO.compareTo(money) < 0) {
            count++;
        }
    }

    public static class FormatMoneyGroupStrategy<E extends StoreResPriceEntity> extends ResFormatGroupStrategy<E, ResPriceTotal> {
        @Override
        public ResPriceTotal totalGroupData(Collection<E> datas) {
            return ResPriceTotal.total(datas);
        }
    }


    public static class ResMoneyGroupStrategy<E extends StoreResPriceEntity> extends ResPriceGroupStrategy<E,ResPriceTotal> {
        @Override
        public ResPriceTotal totalGroupData(Collection<E> datas) {
            return ResPriceTotal.total(datas);
        }
    }

    public static class StoreResMoneyGroupStrategy<E extends StoreResPriceEntity> implements TotalGroupStrategy<StoreRes, E, ResPriceTotal>{

        @Override
        public StoreRes getKey(E e) {
            return e.getStoreRes();
        }

        @Override
        public ResPriceTotal totalGroupData(Collection<E> datas) {
            return ResPriceTotal.total(datas);
        }
    }

}
