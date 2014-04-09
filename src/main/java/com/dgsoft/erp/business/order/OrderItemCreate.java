package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created by cooper on 4/6/14.
 */
@Name("orderItemCreate")
@Scope(ScopeType.CONVERSATION)
public class OrderItemCreate extends StoreResEntityItemCreate<OrderItem> {

    @In
    private ResHelper resHelper;

    @Override
    protected OrderItem createInstance(Res res) {
        return new OrderItem(res,
                resHelper.getFormatHistory(res),
                resHelper.getFloatConvertRateHistory(res),
                res.getResUnitByOutDefault());
    }

    @Override
    protected OrderItem createInstance(StoreRes storeRes) {
        return new OrderItem(storeRes,
                resHelper.getFormatHistory(storeRes.getRes()),
                resHelper.getFloatConvertRateHistory(storeRes.getRes()),
                storeRes.getRes().getResUnitByOutDefault());
    }
}
