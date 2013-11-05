package com.dgsoft.erp.business.order;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:38 AM
 */
public abstract class OrderTaskHandle extends TaskHandle {

    @In(create= true)
    protected OrderHome orderHome;

    @In(required = false)
    @Out
    protected CustomerOrder bizOrder;

    protected String initOrderTask(){
        return "success";
    }

    protected String completeOrderTask(){

        return "taskComplete";
    }

    @Override
    protected final String completeTask() {


        return completeOrderTask();
    }

    @Override
    protected final String initTask() {
        orderHome.setId(taskInstance.getProcessInstance().getKey());
        bizOrder = orderHome.getInstance();
        return initOrderTask();
    }

}
