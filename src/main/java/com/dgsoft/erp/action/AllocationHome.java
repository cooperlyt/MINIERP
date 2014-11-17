package com.dgsoft.erp.action;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.business.finance.AccountDateHelper;
import com.dgsoft.erp.model.Allocation;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
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


    private void revertStockChange(StockChange stockChange) {
        if (stockChange == null) {
            return;
        }
        for (StockChangeItem item : stockChange.getStockChangeItems()) {
            if (stockChange.getOperType().isOut()) {
                item.getStock().setCount(item.getStock().getCount().add(item.getCount()));
            } else {
                item.getStock().setCount(item.getStock().getCount().subtract(item.getCount()));
            }
        }
    }

    @In(create = true)
    private ProcessInstanceHome processInstanceHome;

    @Override
    public String remove() {
        if (getInstance().getStockChangeByStoreOut() != null) {
            if (((getInstance().getStockChangeByStoreOut() != null) &&
                    (getInstance().getStockChangeByStoreOut().getOperDate().compareTo(AccountDateHelper.instance().getNextBeginDate()) < 0)) ||
                    ( (getInstance().getStockChangeByStoreIn() != null) &&
                            (getInstance().getStockChangeByStoreIn().getOperDate().compareTo(AccountDateHelper.instance().getNextBeginDate()) < 0))){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"StoreChangeDateIsClose");
                return null;
            }
        }


        revertStockChange(getInstance().getStockChangeByStoreIn());
        revertStockChange(getInstance().getStockChangeByStoreOut());
        processInstanceHome.setProcessDefineName("stockAllocation");
        processInstanceHome.setProcessKey(getInstance().getId());
        //processInstanceHome.stop();
        String result = super.remove();
        if ("removed".equals(result)) {
            processInstanceHome.stop();
        }
        return result;
    }


    @Transactional
    public String createItem() {
        if (getInstance().getInStore().equals(getInstance().getOutStore())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "AllocationStoreSameError");
            return null;
        }


        getInstance().setId("D" + numberBuilder.getSampleNumber("Allocation"));

        if (getInstance().getType().equals(Allocation.AllocationType.ALLOCATION)) {

            if (!stockChangeHome.validDate()){
                return null;
            }

            getInstance().setState(Allocation.AllocationState.WAITING_IN);
            stockChangeHome.getInstance().setStore(getInstance().getOutStore());
            if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
                stockChangeHome.getInstance().setId("AO-" + getInstance().getId());
            }
            stockChangeHome.getInstance().setMemo(getInstance().getMemo());
            stockChangeHome.getInstance().setVerify(true);
            stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.ALLOCATION_OUT);
            ((StockSearchList) Component.getInstance("stockSearchList", true, true)).setStoreId(getInstance().getOutStore().getId());
            return "/business/startPrepare/erp/store/AllocationStoreOut.xhtml";
        } else {
            getInstance().setState(Allocation.AllocationState.WAITING_OUT);
            return "/business/startPrepare/erp/store/AllocationApply.xhtml";
        }
    }

}
