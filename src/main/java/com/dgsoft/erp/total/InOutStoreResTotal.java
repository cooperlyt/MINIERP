package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 15:02
 */
public abstract class InOutStoreResTotal extends StoreChangeResTotal{

    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem left join fetch stockChangeItem.stockChange sc left join fetch sc.orderDispatch od left join fetch od.needRes nr left join fetch nr.customerOrder co left join fetch co.customer customer where stockChangeItem.stockChange.verify = true";

    public InOutStoreResTotal(){
       super();
        setEjbql(EJBQL);
    }

    protected abstract StockChange.StoreChangeType getChangeType();

    public List<TotalDataGroup<Date,StockChangeItem>> getDayGroupResultList(){
        return TotalDataGroup.groupBy(getResultList(),new TotalGroupStrategy<Date, StockChangeItem>() {
                    @Override
                    public Date getKey(StockChangeItem stockChangeItem) {
                        return DataFormat.halfTime(stockChangeItem.getStockChange().getOperDate());
                    }

                    @Override
                    public Object totalGroupData(Collection<StockChangeItem> datas){
                        return null;
                    }

                } , new TotalGroupStrategy<StockChange, StockChangeItem>() {
                    @Override
                    public StockChange getKey(StockChangeItem stockChangeItem) {
                        return stockChangeItem.getStockChange();
                    }

                    @Override
                    public Object totalGroupData(Collection<StockChangeItem> datas){
                        return null;
                    }
                });
    }
}
