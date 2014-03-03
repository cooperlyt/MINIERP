package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;

import java.util.*;

/**
 * Created by cooper on 3/2/14.
 */
public abstract class CustomerResConfirm extends StoreChangeResTotal {


    public abstract StockChange.StoreChangeType getChangeType();

    private String coustomerId;

    public String getCoustomerId() {
        return coustomerId;
    }

    public void setCoustomerId(String coustomerId) {
        this.coustomerId = coustomerId;
    }

}
