package com.dgsoft.erp.business.inventory;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.erp.action.InventoryHome;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
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
    private StartData startData;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    @In(required = false)
    private InventoryHome inventoryHome;

    @In
    private Credentials credentials;


    public void init(){
        startData.generateKey();
        businessDefineHome.setId("erp.business.inventory");
    }

    @Observer("com.dgsoft.BusinessCreatePrepare.inventory")
    @Transactional
    public void beginInventory(){
        inventoryHome.getInstance().getStore().setEnable(false);
        inventoryHome.getInstance().setId(startData.getBusinessKey());
        inventoryHome.getInstance().setApplyEmp(credentials.getUsername());
        if (!"persisted".equals(inventoryHome.persist())) {
            throw new ProcessCreatePrepareException("inventory persist fail");
        }

        startData.setDescription(inventoryHome.getInstance().getStore().getName());
    }


}
