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
@Name("customerShipConfirm")
public class CustomeShipConfirm extends CustomerResConfirm {


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{customerShipConfirm.changeType}",
            "stockChangeItem.stockChange.operDate >= #{customerShipConfirm.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{customerShipConfirm.searchDateArea.searchDateTo}",
            "stockChangeItem.stockChange.orderDispatch.needRes.customerOrder.customer.id = #{customerShipConfirm.coustomerId}"};


    public CustomeShipConfirm() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

    @Override
    public StockChange.StoreChangeType getChangeType(){
        return StockChange.StoreChangeType.SELL_OUT;
    }


}
