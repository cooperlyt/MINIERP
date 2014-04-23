package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.erp.action.InventoryHome;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.security.Credentials;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/8/13
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */

@Name("inventoryCreate")
@Scope(ScopeType.CONVERSATION)
public class InventoryCreate {

    @In(create = true)
    private InventoryHome inventoryHome;

    @In
    private NumberBuilder numberBuilder;

    @In
    private Credentials credentials;

    //TODO move to process EL
    //----------------
    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessDescription;

    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessName;

    //-----------------

    private boolean lockStore = false;

    public boolean isLockStore() {
        return lockStore;
    }

    public void setLockStore(boolean lockStore) {
        this.lockStore = lockStore;
    }

    public void init() {
        inventoryHome.getInstance().setId("P" + numberBuilder.getSampleNumber("inventory"));
    }


    @CreateProcess(definition = "order", processKey = "#{orderCreate.instance.id}")
    @Transactional
    public String beginInventory() {
        if (lockStore)
            inventoryHome.getInstance().getStore().setEnable(false);

        inventoryHome.getInstance().setApplyEmp(credentials.getUsername());
        if (!"persisted".equals(inventoryHome.persist())) {
            return null;
        }
        businessDescription = inventoryHome.getInstance().getStore().getName();
        businessName = "盘点";
        return "businessCreated";

    }


}
