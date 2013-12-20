package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.OrderHome;
import org.jboss.seam.annotations.In;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/20/13
 * Time: 4:54 PM
 */
public abstract class CancelOrderTaskHandle extends TaskHandle {

    @In(create = true)
    protected OrderHome orderHome;

    @In(create = true)
    protected OrderBackHome orderBackHome;

    protected String initCancelOrderTask() {
        return "success";
    }

    protected String completeOrderTask() {
        return "taskComplete";
    }


    @Override
    protected String completeTask() {
        return completeOrderTask();
    }

    @Override
    protected String initTask() {
        orderBackHome.setId(taskInstance.getProcessInstance().getKey());
        orderHome.setId(orderBackHome.getInstance().getCustomerOrder().getId());
        return initCancelOrderTask();
    }
}
