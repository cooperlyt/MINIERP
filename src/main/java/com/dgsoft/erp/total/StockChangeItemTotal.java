package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created by cooper on 4/28/14.
 */
@Name("stockChangeItemTotal")
public class StockChangeItemTotal extends ErpEntityQuery<StockChangeItem> {

    public enum  ResultGroupType{
        GORUP_BY_DAY, GROUP_BY_CHANGE, NO_GROUP;
    }

    private static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem  " +
            "where stockChangeItem.stockChange.verify = true";

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "stockChangeItem.stockChange.operType in (#{stockChangeTypeCondition.searchTypes})",
            "stockChangeItem.stockChange.store.id = #{stockChangeTypeCondition.storeId}",
            "stockChangeItem.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stockChangeItem.storeRes.res.id = #{storeResCondition.searchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stockChangeItem.storeRes.id in (#{storeResCondition.matchStoreResIds})"};


    public StockChangeItemTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("stockChangeItem.stockChange.operDate");
    }

    public EnumSet<ResultGroupType> getAllGroupType(){
        return EnumSet.allOf(ResultGroupType.class);
    }

    private ResultGroupType resultGroupBy = ResultGroupType.NO_GROUP;

    public ResultGroupType getResultGroupBy() {
        return resultGroupBy;
    }

    public void setResultGroupBy(ResultGroupType resultGroupBy) {
        this.resultGroupBy = resultGroupBy;
    }

    public TotalDataGroup<?, StockChangeItem,ResCount> getDayResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<TotalDataGroup.DateKey, StockChangeItem,ResCount>() {
            @Override
            public TotalDataGroup.DateKey getKey(StockChangeItem stockChangeItem) {
                return new TotalDataGroup.DateKey(DataFormat.halfTime(stockChangeItem.getStockChange().getOperDate()));
            }

            @Override
            public ResCount totalGroupData(Collection<StockChangeItem> datas) {
                return null;
            }
        }, new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }

    public TotalDataGroup<?, StockChangeItem,?> getResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(),
                new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }

    public TotalDataGroup<?, StockChangeItem,ResCount> getChangeResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<StockChange, StockChangeItem,ResCount>() {
            @Override
            public StockChange getKey(StockChangeItem stockChangeItem) {
                return stockChangeItem.getStockChange();
            }

            @Override
            public ResCount totalGroupData(Collection<StockChangeItem> datas) {
                return null;
            }
        },new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }


}
