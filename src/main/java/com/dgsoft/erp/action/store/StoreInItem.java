package com.dgsoft.erp.action.store;

import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/23/13
 * Time: 10:48 AM
 */
public class StoreInItem implements java.io.Serializable{


    //TODO batch

    //TODO storeaArea

    // private

    private Res res;

    private List<Format> formats;

    private StoreResCount storeResCount;

    private Batch batch;

    public StoreInItem(Res res) {
        this.res = res;
        if (res.isBatchMgr()){
            batch = new Batch();
        }
        storeResCount = new StoreResCount(res,res.getResUnitByInDefault());
    }

    public StoreResCount getStoreResCount() {
        return storeResCount;
    }

    public void setStoreResCount(StoreResCount storeResCount) {
        this.storeResCount = storeResCount;
    }

    public void merger(StoreInItem storeInItem) {
        if (!same(storeInItem)) {
            throw new IllegalArgumentException("not same storeInItem can't merger");
        }
        storeResCount.add(storeInItem.storeResCount);
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public boolean same(StoreInItem storeInItem) {
        //TODO batch
        //TODO storeaArea
        return (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                || storeResCount.getFloatConvertRate().equals(storeInItem.storeResCount.getFloatConvertRate())) &&
                res.getId().equals(storeInItem.getRes().getId()) &&
                StoreChangeHelper.sameFormat(storeInItem.getFormats(), formats);
    }

}
