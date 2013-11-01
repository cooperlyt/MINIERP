package com.dgsoft.erp.action.store;

import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/23/13
 * Time: 10:48 AM
 */
public class StoreInItem extends StoreChangeItem {


    //TODO batch

    //TODO storeaArea

    private Batch batch;

    private String storeResCode = null;

    public StoreInItem(Res res, BigDecimal floatConvertRate){
        this(res);
        storeResCount.setFloatConvertRate(floatConvertRate);
    }

    public StoreInItem(Res res) {
        super(res,res.getResUnitByInDefault());
        if (res.isBatchMgr()){
            batch = new Batch();
        }
    }

    public void merger(StoreInItem storeInItem) {
        if (!same(storeInItem)) {
            throw new IllegalArgumentException("not same storeInItem can't merger");
        }
        storeResCount.add(storeInItem.storeResCount);
    }

    public String getStoreResCode() {
        return storeResCode;
    }

    public void setStoreResCode(String storeResCode) {
        this.storeResCode = storeResCode;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    @Override
    public boolean same(StoreChangeItem storeInItem) {
        //TODO batch
        //TODO storeaArea
        return super.same(storeInItem);
    }

}
