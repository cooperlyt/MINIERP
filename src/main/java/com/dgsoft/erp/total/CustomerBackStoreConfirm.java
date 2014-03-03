package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by cooper on 3/2/14.
 */
@Name("customerBackStoreConfirm")
public class CustomerBackStoreConfirm extends CustomerResConfirm {

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{customerBackStoreConfirm.changeType}",
            "stockChangeItem.stockChange.operDate >= #{customerBackStoreConfirm.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{customerBackStoreConfirm.searchDateArea.searchDateTo}",
            "stockChangeItem.stockChange.productBackStoreIn.orderBack.customer.id = #{customerBackStoreConfirm.coustomerId}"};


    public CustomerBackStoreConfirm() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

    @Override
    public StockChange.StoreChangeType getChangeType(){
        return StockChange.StoreChangeType.SELL_BACK;
    }
}
