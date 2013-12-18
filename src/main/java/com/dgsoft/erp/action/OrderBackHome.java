package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.OrderBack;
import com.dgsoft.erp.model.ProductBackStoreIn;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/18/13
 * Time: 2:52 PM
 */
@Name("orderBackHome")
public class OrderBackHome extends ErpEntityHome<OrderBack> {

    public boolean needStoreIn(String storeId) {
        if (isIdDefined()) {
            for (ProductBackStoreIn productBackStoreIn : getInstance().getProductBackStoreIn()) {
                if (productBackStoreIn.getStore().getId().equals(storeId)) {
                    return true;
                }
            }
            return false;
        }
        throw new IllegalThreadStateException("business not init;");
    }

}
