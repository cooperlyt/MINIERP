package com.dgsoft.erp.business.order;

import com.dgsoft.erp.ResCountEntityItemCreate;
import com.dgsoft.erp.ResEntityItemCreate;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 09/04/14
 * Time: 17:01
 */
@Name("backItemCreate")
@Scope(ScopeType.CONVERSATION)
public class BackItemCreate extends ResCountEntityItemCreate<BackItem> {


    @Override
    protected BackItem createInstance(Res res) {
        return new BackItem(res, resHelper.getFormatHistory(res),
                res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ? resHelper.getFloatConvertRateHistory(res) : null,
                res.getResUnitByOutDefault());
    }

    @Override
    protected BackItem createInstance(StoreRes storeRes) {
        return new BackItem(storeRes, resHelper.getFormatHistory(storeRes.getRes()),
                storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ? resHelper.getFloatConvertRateHistory(storeRes.getRes()) : null,
                storeRes.getRes().getResUnitByOutDefault());
    }
}
