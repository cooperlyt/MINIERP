package com.dgsoft.erp.action.store;

import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/1/13
 * Time: 9:21 AM
 */
@Deprecated
public abstract class StoreChangeItem implements java.io.Serializable {

    protected Res res;
    protected StoreResCountInupt storeResCountInupt;
    protected StoreRes storeRes = null;

    public StoreChangeItem(Res res, ResUnit useUnit) {
        this.res = res;
        storeResCountInupt = new StoreResCountInupt(res, useUnit);
    }

    public StoreResCountInupt getStoreResCountInupt() {
        return storeResCountInupt;
    }

    public void setStoreResCountInupt(StoreResCountInupt storeResCountInupt) {
        this.storeResCountInupt = storeResCountInupt;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public void setStoreRes(StoreRes storeRes) {
        this.storeRes = storeRes;
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public abstract List<Format> getFormats();


    public boolean same(StoreChangeItem storeChangeItem) {
        return res.getId().equals(storeChangeItem.getRes().getId()) &&
                (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                || storeResCountInupt.getFloatConvertRate().compareTo(storeChangeItem.storeResCountInupt.getFloatConvertRate()) == 0) &&
                ResHelper.sameFormat(storeChangeItem.getFormats(), getFormats());
    }
}
