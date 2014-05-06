package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.OrderBack;
import org.jboss.seam.annotations.In;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/20/13
 * Time: 4:54 PM
 */
public abstract class CancelOrderTaskHandle extends TaskHandle {

    @In(create = true)
    protected OrderBackHome orderBackHome;


    protected void initCancelOrderTask() {
    }

    protected String completeOrderTask() {
        return "taskComplete";
    }



    @Override
    protected String completeTask() {
        String result = completeOrderTask();
//        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL) &&
//                orderBackHome.getInstance().isResComplete() && orderBackHome.getInstance().isMoneyComplete()){
//            businessProcess.stopProcess("order",orderBackHome.getInstance().getCustomerOrder().getId());
//        }
        return result;
    }

    @Override
    protected void initTask() {
        orderBackHome.setId(taskInstance.getProcessInstance().getKey());
        initCancelOrderTask();
    }
}
