package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Allocation;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;


/**
 * Created by cooper on 3/6/14.
 */
@Name("allocationHome")
public class AllocationHome extends ErpEntityHome<Allocation> {

    @In
    private NumberBuilder numberBuilder;

    @In
    protected RunParam runParam;

    @In(required = false)
    private StockChangeHome stockChangeHome;


    @Factory(value = "allAllocationTypes", scope = ScopeType.SESSION)
    public Allocation.AllocationType[] getAllAllocationTypes() {
        return Allocation.AllocationType.values();
    }

    @In
    private org.jboss.seam.security.Credentials credentials;

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            getInstance().setApplyEmp(credentials.getUsername());
        }

        return true;
    }

    @In
    private FacesMessages facesMessages;





    @Transactional
    public String createItem() {
        if (getInstance().getInStore().equals(getInstance().getOutStore())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "AllocationStoreSameError");
            return null;
        }



        getInstance().setId("D" + numberBuilder.getSampleNumber("Allocation"));

        if (getInstance().getType().equals(Allocation.AllocationType.ALLOCATION)) {
            getInstance().setState(Allocation.AllocationState.WAITING_IN);
            stockChangeHome.getInstance().setStore(getInstance().getOutStore());
            if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
                stockChangeHome.getInstance().setId("AO-" + getInstance().getId());
            }
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
