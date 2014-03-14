package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:14
 */
@Name("inOutStoreResSellTotal")
public class InOutStoreResSellTotal extends InOutStoreResTotal{


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{inOutStoreResSellTotal.changeType}",
            "stockChangeItem.stockChange.operDate >= #{inOutMoneyTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{inOutMoneyTotal.searchDateArea.searchDateTo}"};

    public InOutStoreResSellTotal(){
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


    @Override
    public StockChange.StoreChangeType getChangeType(){
        return StockChange.StoreChangeType.SELL_OUT;
    }

}
