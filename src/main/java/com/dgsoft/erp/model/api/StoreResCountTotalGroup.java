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


    public <E extends StoreResCountEntity> StoreResCountTotalGroup(Collection<E> values) {
        super();
        put(values);
    }

    public StoreResCountTotalGroup() {
    }

    public <E extends StoreResCountEntity> StoreResCount put(E v) {
        return super.put(new StoreResCount(v.getStoreRes(), v.getMasterCount()));
    }

    public <E extends StoreResCountEntity> void put(Collection<E> values) {
        for (E v : values) {
            put(v);
        }
    }
}
