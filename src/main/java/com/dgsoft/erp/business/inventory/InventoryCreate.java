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

    private boolean lockStore = false;

    public boolean isLockStore() {
        return lockStore;
    }

    public void setLockStore(boolean lockStore) {
        this.lockStore = lockStore;
    }

    @Create
    public void init() {
        inventoryHome.getInstance().setId("P" + numberBuilder.getSampleNumber("inventory"));
    }

    public String getBusinessKey() {
        return inventoryHome.getInstance().getId();
    }

    public void setBusinessKey(String key) {
        inventoryHome.getInstance().setId(key);
    }


    @CreateProcess(definition = "inventory", processKey = "#{inventoryHome.instance.id}")
    @Transactional
    public String beginInventory() {
        if (lockStore)
            inventoryHome.getInstance().getStore().setEnable(false);

        inventoryHome.getInstance().setApplyEmp(credentials.getUsername());
        if (!"persisted".equals(inventoryHome.persist())) {
            return null;
        }
        return "businessCreated";

    }


}
