package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.erp.model.BackItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

/**
 * Created by cooper on 2/27/14.
 */
@Name("resBackDispatchTask")
@Scope(ScopeType.CONVERSATION)
public class ResBackDispatchTask extends CancelOrderTaskHandle {

    @In(create = true)
    private ResBackDispatch resBackDispatch;


    @Override
    protected void initCancelOrderTask() {
        Logging.getLog(getClass()).debug("resBackDispatchTask init...");
        resBackDispatch.init(orderBackHome.getInstance());
    }



    @Override
    protected String completeOrderTask() {
        if (!resBackDispatch.isComplete()){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"dispatch_res_item_not_assigned");
            return null;
        }

        orderBackHome.getInstance().setDispatched(true);
        for (BackItem item: orderBackHome.getInstance().getBackItems()){
            item.setBackItemStatus(BackItem.BackItemStatus.DISPATCH);
        }
        orderBackHome.getInstance().getBackDispatchs().addAll(resBackDispatch.getResBackDispatcheds());
        if ("updated".equals(orderBackHome.update())) {
            return "taskComplete";
        }else{
            return null;
        }
    }
}
