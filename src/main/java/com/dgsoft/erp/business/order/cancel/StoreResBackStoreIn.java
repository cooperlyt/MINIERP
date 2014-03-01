package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.erp.action.ProductBackStoreInHome;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.BackDispatchItem;
import com.dgsoft.erp.model.ProductBackStoreIn;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.util.logging.Logger;

/**
 * Created by cooper on 3/1/14.
 */

@Name("storeResBackStoreIn")
public class StoreResBackStoreIn extends CancelOrderTaskHandle {

    public final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private ProductBackStoreInHome productBackStoreInHome;

    @In(create = true)
    private StockChangeHome stockChangeHome;

    @In
    protected NumberBuilder numberBuilder;

    @Override
    protected void initCancelOrderTask() {
        Logging.getLog(getClass()).debug("resBackDispatchTask init...");
        String storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }


        for (ProductBackStoreIn storeIn : orderBackHome.getInstance().getProductBackStoreIn()) {
            if (storeIn.getStore().getId().equals(storeId)) {
                productBackStoreInHome.setId(storeIn.getId());

                stockChangeHome.clearInstance();
                stockChangeHome.getInstance().setStore(storeIn.getStore());
                stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.SELL_BACK);
                stockChangeHome.getInstance().setVerify(true);

                return;
            }
        }
        throw new ProcessDefineException("Order Store out store ID not exists");
    }


    @Override
    protected String completeOrderTask() {
        for (BackDispatchItem item: productBackStoreInHome.getInstance().getBackDispatchItems()){
            stockChangeHome.resStockChange(item, null);
        }
        stockChangeHome.getInstance().setId(orderBackHome.getInstance().getId() + "-" + numberBuilder.getNumber("storeInCode"));
        stockChangeHome.getInstance().setProductBackStoreIn(productBackStoreInHome.getInstance());

        Logging.getLog(getClass()).debug("res back store in change stoce item count:" + stockChangeHome.getInstance().getStockChangeItems().size());
        productBackStoreInHome.getInstance().setStockChange(stockChangeHome.getReadyInstance());
        productBackStoreInHome.getInstance().getOrderBack().setResComplete(true);


        if ("updated".equals(productBackStoreInHome.update())){
            return "taskComplete";
        }else
            return null;
    }
}
