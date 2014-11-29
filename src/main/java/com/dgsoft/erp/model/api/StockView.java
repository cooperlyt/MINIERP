package com.dgsoft.erp.model.api;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StoreRes;
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
        saleCount = new StoreResCount(stock.getStoreRes(), (saleMasterCount == null) ? BigDecimal.ZERO : saleMasterCount);
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

    public static StockTotalCount totalCount(Collection<? extends StockCountView> counts) {
        StockTotalCount result = ZERO;
        for (StockCountView count : counts) {

            result = result.add(count);
        }
        return result;
    }

    public static final StockTotalCount ZERO = new StockTotalCount(ResTotalCount.ZERO, ResTotalCount.ZERO);

    public static class StockTotalCount extends StockCountView implements TotalDataGroup.GroupTotalData {

        //private Res res;

        private ResCount stockCount;

        private ResCount saleCount;

        public StockTotalCount(ResCount stockCount, ResCount saleCount) {
            this.stockCount = stockCount;
            this.saleCount = saleCount;
        }


        public StockTotalCount add(StockCountView other) {
            return new StockTotalCount(stockCount.add(other.getStockCount()), saleCount.add(other.getSaleCount()));
        }

        @Override
        public Res getRes() {
            return stockCount.getRes();
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

    public static class FormatCountGroupStrategy implements TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, StockView, StockTotalCount> {


        @Override
        public ResFormatGroupStrategy.StoreResFormatKey getKey(StockView stockView) {
            return new ResFormatGroupStrategy.StoreResFormatKey(stockView.getStock().getStoreRes());
        }

        @Override
        public StockTotalCount totalGroupData(Collection<StockView> datas) {
            return totalCount(datas);
        }
    }


    public static class ResCountGroupStrategy<E extends StockCountView> implements TotalGroupStrategy<Res, E, StockTotalCount> {

        @Override
        public Res getKey(E e) {
            return e.getRes();
        }

        @Override
        public StockTotalCount totalGroupData(Collection<E> datas) {
            return totalCount(datas);
        }
    }

    public static class StoreResCountGroupStrategy implements TotalGroupStrategy<StoreRes, StockView, StockTotalCount> {


        @Override
        public StoreRes getKey(StockView stockView) {
            return stockView.getStock().getStoreRes();
        }

        @Override
        public StockTotalCount totalGroupData(Collection<StockView> datas) {
            return totalCount(datas);
        }
    }

}
