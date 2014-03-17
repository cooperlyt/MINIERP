package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataUnionStrategy;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;

/**
 * Created by cooper on 3/16/14.
 */
public class StoreResCountUnionStrategy<E extends StoreResCountEntity> implements TotalDataUnionStrategy<StoreRes, E> {


    @Override
    public StoreRes getKey(E v) {
        return v.getStoreRes();
    }

    @Override
    public StoreResCountEntity unionData(StoreResCountEntity v1, StoreResCountEntity v2) {
        return new StoreResCount(v1.getStoreRes(), v1.getCount().add(v2.getCount()));
    }
}
