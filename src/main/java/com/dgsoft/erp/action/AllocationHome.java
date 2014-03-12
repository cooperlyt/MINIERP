package com.dgsoft.erp.action;

import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Allocation;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.Date;

/**
 * Created by cooper on 3/6/14.
 */
@Name("allocationHome")
public class AllocationHome extends ErpEntityHome<Allocation> {

    @In(create = true)
    protected StartData startData;

    @In(required = false)
    private StockChangeHome stockChangeHome;

    @In(create = true)
    protected BusinessDefineHome businessDefineHome;

    @Factory(value = "allAllocationTypes", scope = ScopeType.SESSION)
    public Allocation.AllocationType[] getAllAllocationTypes() {
        return Allocation.AllocationType.values();
    }

    @In
    private org.jboss.seam.security.Credentials credentials;

    @Override
    protected boolean wire() {
        if (!isManaged()){
            getInstance().setApplyEmp(credentials.getUsername());
        }

        return true;
    }

    @In
    private FacesMessages facesMessages;


    public String createItem() {
        if (getInstance().getInStore().equals(getInstance().getOutStore())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "AllocationStoreSameError");
            return null;
        }

        businessDefineHome.setId("erp.business.allocation");
        startData.generateKey();
        startData.setDescription(getInstance().getOutStore().getName() + "->" + getInstance().getInStore().getName());
        getInstance().setId(startData.getBusinessKey());

        if (getInstance().getType().equals(Allocation.AllocationType.ALLOCATION)) {
            getInstance().setState(Allocation.AllocationState.WAITING_IN);
            stockChangeHome.getInstance().setStore(getInstance().getOutStore());
            stockChangeHome.getInstance().setId("AO-" + startData.getBusinessKey());
            stockChangeHome.getInstance().setMemo(getInstance().getMemo());
            stockChangeHome.getInstance().setVerify(true);
            stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.ALLOCATION_OUT);
            ((StockList) Component.getInstance("stockList", true, true)).setStoreId(getInstance().getOutStore().getId());
            return "/business/startPrepare/erp/store/AllocationStoreOut.xhtml";
        } else {
            getInstance().setState(Allocation.AllocationState.WAITING_OUT);
            return "/business/startPrepare/erp/store/AllocationApply.xhtml";
        }
    }

}
