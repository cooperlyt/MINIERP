package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.system.business.TaskHandle;
import com.dgsoft.erp.action.InventoryHome;
import com.dgsoft.erp.model.PrepareStockChange;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.security.Credentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/8/13
 * Time: 10:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InventoryTaskHandle extends TaskHandle {

    @In
    protected Credentials credentials;

    @In(create = true)
    protected InventoryHome inventoryHome;


    protected String completeInventoryTask() {
        return "taskComplete";
    }

    @Override
    protected String completeTask() {
        return completeInventoryTask();
    }

    @Override
    protected void initTask() {
        inventoryHome.setId(taskInstance.getProcessInstance().getKey());
    }
}
