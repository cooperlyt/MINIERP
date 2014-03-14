package com.dgsoft.erp.total;

import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:15
 */
@Name("inOutStoreResBackTotal")
public class InOutStoreResBackTotal extends InOutStoreResTotal{

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{inOutStoreResBackTotal.changeType}",
            "stockChangeItem.stockChange.operDate >= #{inOutMoneyTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{inOutMoneyTotal.searchDateArea.searchDateTo}"};

    public InOutStoreResBackTotal(){
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }
    @Override
    public StockChange.StoreChangeType getChangeType(){
        return StockChange.StoreChangeType.SELL_BACK;
    }
}
