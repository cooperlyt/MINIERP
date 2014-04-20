package com.dgsoft.erp.action.store;

import com.dgsoft.erp.ResCountEntityItemCreate;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.annotations.Name;

/**
 * Created by cooper on 4/12/14.
 */
@Name("stockChangeItemCreate")
public class StockChangeItemCreate extends ResCountEntityItemCreate<StockChangeItem> {


    @Override
    protected StockChangeItem createInstance(Res res) {
        return new StockChangeItem(res, res.getResUnitByInDefault());
    }

    @Override
    protected StockChangeItem createInstance(StoreRes storeRes) {
        return new StockChangeItem(storeRes,storeRes.getRes().getResUnitByInDefault());

    }
}
