package com.dgsoft.erp.business.inventory;

import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/16/13
 * Time: 3:01 PM
 */

@Name("inventoryLastCheck")
@Scope(ScopeType.CONVERSATION)
public class InventoryLastCheck extends InventoryTaskHandle {


    @Override
    protected String completeInventoryTask() {
        inventoryHome.getInstance().setCheckEmp(credentials.getUsername());
        inventoryHome.getInstance().setStatus(Inventory.InvertoryStatus.INVERTORY_COMPLETE);
        return "taskComplete";
    }
}
