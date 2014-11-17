package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.AllocationHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.action.StockChangeHome;
import org.jboss.seam.annotations.In;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/03/14
 * Time: 13:36
 */
public abstract class AllocationTaskHandle extends TaskHandle {

    @In(create= true)
    protected AllocationHome allocationHome;


    @In(create = true)
    protected StockChangeHome stockChangeHome;

    protected void initOrderTask(){
    }

    protected String completeOrderTask(){
        return "taskComplete";
    }

    @Override
    protected final String completeTask() {
        if(!stockChangeHome.validDate()){
            return null;
        }

        return completeOrderTask();
    }

    @Override
    protected final void initTask() {
        allocationHome.setId(taskInstance.getProcessInstance().getKey());
        initOrderTask();
    }
}
