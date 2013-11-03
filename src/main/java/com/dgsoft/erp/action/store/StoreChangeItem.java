package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/1/13
 * Time: 9:21 AM
 */
public abstract class StoreChangeItem implements java.io.Serializable {

    protected Res res;
    protected StoreResCount storeResCount;
    protected StoreRes storeRes = null;

    public StoreChangeItem(Res res, ResUnit useUnit) {
        this.res = res;
        storeResCount = new StoreResCount(res, useUnit);
    }

    public StoreResCount getStoreResCount() {
        return storeResCount;
    }

    public void setStoreResCount(StoreResCount storeResCount) {
        this.storeResCount = storeResCount;
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
        return (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                || storeResCount.getFloatConvertRate().compareTo(storeChangeItem.storeResCount.getFloatConvertRate()) == 0) &&
                res.getId().equals(storeChangeItem.getRes().getId()) &&
                StoreChangeHelper.sameFormat(storeChangeItem.getFormats(), getFormats());
    }
}
