package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.log.Log;

import java.util.List;

/**
 * Created by cooper on 2/23/14.
 */
@Name("storeResBackCreate")
@Scope(ScopeType.CONVERSATION)
public class StoreResBackCreate {

    @Logger
    private Log log;

    @DataModel
    private List<BackItem> backItems;

    @In
    private ResHelper resHelper;

    @DataModelSelection
    private BackItem selectBackItem;

    private BackItem operBackItem;

    public BackItem getOperBackItem() {
        return operBackItem;
    }

    public void setOperBackItem(BackItem operBackItem) {
        this.operBackItem = operBackItem;
    }

    private void createNewBackItem(Res res){
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            operBackItem = new BackItem(res,resHelper.getFormatHistory(res),resHelper.getFloatConvertRateHistory(res),res.getResUnitByOutDefault());
        }else{
            operBackItem = new BackItem(res,resHelper.getFormatHistory(res),res.getResUnitByOutDefault());
        }
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        log.debug("storeResFormat selectedStoreRes Observer ");
        createNewBackItem(storeRes.getRes());
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        log.debug("selectedRes selectedStoreRes Observer ");
        createNewBackItem(res);
    }

    public String createBack() {
        return null;
    }



}
