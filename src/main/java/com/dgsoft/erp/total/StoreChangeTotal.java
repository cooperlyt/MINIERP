package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.annotations.Name;

import java.util.Map;

/**
 * Created by cooper on 6/19/14.
 */
@Name("storeChangeTotal")
public class StoreChangeTotal extends ErpEntityQuery<StockChange> {


    public static class totalChangeData {

        private StoreRes storeRes;

        protected Map<StockChange.StoreChangeType, StoreResCount> changeCount;

        protected Map<String,StoreResCount> allocationOutCount;

    }

}
