package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 3/3/14.
 */
public class StoreResCountTotalGroup extends StoreResCountGroup<StoreResCount> implements java.io.Serializable {


    public StoreResCountTotalGroup(Collection<? extends StoreResCountEntity> values) {
        super();
        put(values);
    }

    public StoreResCountTotalGroup() {
    }

    public <E extends StoreResCountEntity> StoreResCount put(E v) {
        return super.put(new StoreResCount(v.getStoreRes(), v.getMasterCount()));
    }

    public void put(Collection<? extends StoreResCountEntity> values) {
        for (StoreResCountEntity v : values) {
            put(v);
        }
    }
}
