package com.dgsoft.erp.model.api;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.*;
import com.dgsoft.erp.total.data.ResCount;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 11/20/14.
 */
public class StockView extends StockCountView {

    private Stock stock;

    private StoreResCount saleCount;

    public StockView(Stock stock, BigDecimal saleMasterCount) {
        this.stock = stock;
        saleCount = new StoreResCount(stock.getStoreRes(),(saleMasterCount == null) ? BigDecimal.ZERO : saleMasterCount);
    }

    public Stock getStock() {
        return stock;
    }

    @Override
    public Res getRes() {
        return getStock().getRes();
    }

    @Override
    public com.dgsoft.erp.total.data.ResCount getStockCount() {
        return stock.getStoreResCount();
    }

    public com.dgsoft.erp.total.data.ResCount getSaleCount() {
        return saleCount;
    }

    public static StockTotalCount totalCount(Collection<? extends StockCountView> counts){
        StockTotalCount result = null;
        for(StockCountView count: counts){
            if (result == null){
                result = new StockTotalCount(count.getRes(),count.getStockCount(),count.getSaleCount());
            }else{
                result = result.add(count);
            }
        }
        return result;
    }

    public static class StockTotalCount extends StockCountView implements TotalDataGroup.GroupTotalData{

        private Res res;

        private ResCount stockCount;

        private ResCount saleCount;

        public StockTotalCount(Res res, ResCount stockCount, ResCount saleCount) {
            this.res = res;
            this.stockCount = stockCount;
            this.saleCount = saleCount;
        }

        public StockTotalCount add(StockCountView other){
            return new StockTotalCount(res,stockCount.add(other.getStockCount()),saleCount.add(other.getSaleCount()))
        }

        @Override
        public Res getRes() {
            return res;
        }

        @Override
        public ResCount getStockCount() {
            return stockCount;
        }

        @Override
        public ResCount getSaleCount() {
            return saleCount;
        }
    }

    public static class FormatCountGroupStrategy<E extends StockCountView> extends ResFormatGroupStrategy<E, ResCount> {
        @Override
        public ResCount totalGroupData(Collection<E> datas) {
            return ResTotalCount.total(datas);
        }
    }


    public static class ResCountGroupStrategy<E extends StockCountView> implements TotalGroupStrategy<Res, E, ResCount> {

        @Override
        public Res getKey(E e) {
            return e.getRes();
        }

        @Override
        public ResCount totalGroupData(Collection<E> datas) {
            return ResTotalCount.total(datas);
        }
    }

}
