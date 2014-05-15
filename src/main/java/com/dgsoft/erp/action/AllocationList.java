package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Allocation;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cooper on 5/15/14.
 */
@Name("allocationList")
public class AllocationList extends ErpEntityQuery<Allocation> {

    private static final String EJBQL = "select allocation from Allocation allocation where (allocation.inStore.id in (#{myStoreIds}) or allocation.outStore.id in (#{myStoreIds}) )";

    private static final String[] RESTRICTIONS = {
            "allocation.createDate >=  #{searchDateArea.dateFrom} ",
            "allocation.createDate <=  #{searchDateArea.searchDateTo} ",
            "allocation.inStore.id =  #{allocationList.inStoreId}",
            "allocation.outStore.id =  #{allocationList.outStoreId}",
            "allocation.reason =  #{allocationList.reason}",
            "allocation.state =  #{allocationList.allocationState}",
            "allocation.type =  #{allocationList.allocationType}"};


    public AllocationList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    @Factory(value = "allAllocationStates" , scope = ScopeType.CONVERSATION)
    public Allocation.AllocationState[] getAllAllocationStates(){
        return Allocation.AllocationState.values();
    }

    private String outStoreId;

    private String inStoreId;

    private Allocation.AllocationState allocationState;

    private Allocation.AllocationType allocationType;

    private String reason;

    public Allocation.AllocationState getAllocationState() {
        return allocationState;
    }

    public void setAllocationState(Allocation.AllocationState allocationState) {
        this.allocationState = allocationState;
    }

    public Allocation.AllocationType getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(Allocation.AllocationType allocationType) {
        this.allocationType = allocationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOutStoreId() {
        return outStoreId;
    }

    public void setOutStoreId(String outStoreId) {
        this.outStoreId = outStoreId;
    }

    public String getInStoreId() {
        return inStoreId;
    }

    public void setInStoreId(String inStoreId) {
        this.inStoreId = inStoreId;
    }
}