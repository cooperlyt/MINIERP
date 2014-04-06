package com.dgsoft.erp.business.order;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.OrderHome;
import org.jboss.seam.annotations.In;

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

    protected void initOrderTask(){
    }

    protected String completeOrderTask(){
        return "taskComplete";
    }

    @Override
    protected final String completeTask() {


        return completeOrderTask();
    }

    @Override
    protected final void initTask() {
        orderHome.setId(taskInstance.getProcessInstance().getKey());
        initOrderTask();
    }

}
