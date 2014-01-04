package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Batch;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;

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

    private List<Format> formats;

    public StoreInItem(Res res, BigDecimal floatConvertRate){
        this(res);
        storeResCountInupt.setFloatConvertRate(floatConvertRate);
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
        storeResCountInupt.add(storeInItem.storeResCountInupt);
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

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    @Override
    public List<Format> getFormats() {
        return formats;
    }

    @Override
    public boolean same(StoreChangeItem storeInItem) {
        //TODO batch
        //TODO storeaArea
        return super.same(storeInItem);
    }




}
