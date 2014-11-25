package com.dgsoft.erp.business.allocation;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.action.AllocationHome;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 3/11/14.
 */
@Name("allocationCreateApply")
@Scope(ScopeType.CONVERSATION)
public class AllocationCreateApply {

    @In(create = true)
    private ResHome resHome;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private FacesMessages facesMessages;

    @In
    private AllocationHome allocationHome;

    @DataModel("allocationCreateApplyItems")
    private List<AllocationRes> applyItems = new ArrayList<AllocationRes>();


    public List<TotalDataGroup<Res, AllocationRes,ResCount>> getApplyGroup() {
        return TotalDataGroup.groupBy(applyItems, new ResTotalCount.ResCountGroupStrategy<AllocationRes>(), new ResTotalCount.FormatCountGroupStrategy<AllocationRes>());
    }



    @DataModelSelection
    private AllocationRes selectApplyItems;

    private AllocationRes editingApplyItems;

    public AllocationRes getEditingApplyItems() {
        return editingApplyItems;
    }

    public void setEditingApplyItems(AllocationRes editingApplyItems) {
        this.editingApplyItems = editingApplyItems;
    }

    @org.jboss.seam.annotations.Observer(value = "erp.resLocateSelected", create = false)
    public void codeTypeByRes(Res res) {
        editingApplyItems = new AllocationRes(res);
    }

    @org.jboss.seam.annotations.Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {
        editingApplyItems = new AllocationRes(storeRes);
    }



    @CreateProcess(definition = "stockAllocation" , processKey = "#{allocationHome.instance.id}")
    @Transactional
    public String complete() {
        if (applyItems.isEmpty()){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"noApplyItemData");
            return null;
        }
        allocationHome.getInstance().getAllocationReses().clear();
        allocationHome.getInstance().getAllocationReses().addAll(applyItems);
        if ("persisted".equals(allocationHome.persist())) {
            return "businessCreated";
        } else {
            return null;
        }
    }

    public void addItem() {

        if (editingApplyItems.getMasterCount().compareTo(BigDecimal.ZERO) == 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"allocation_item_zero");
            return;
        }

        storeResHome.setRes(editingApplyItems.getRes(), editingApplyItems.getFormats(), editingApplyItems.getFloatConvertRate());
        if (storeResHome.isIdDefined()) {
            boolean find = false;
            for (AllocationRes item : applyItems) {
                if (item.getStoreRes().equals(editingApplyItems.getStoreRes())) {
                    find = true;
                    item.addCount(editingApplyItems);
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"allocation_applyItemExists");
                    break;
                }
            }

            if (!find) {
                editingApplyItems.setStoreRes(storeResHome.getInstance());
                editingApplyItems.setAllocation(allocationHome.getInstance());
                applyItems.add(editingApplyItems);
            }

            editingApplyItems = new AllocationRes(storeResHome.getInstance());


        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
        }
    }

    public void removeItem() {
        applyItems.remove(selectApplyItems);
    }

    public void editItem() {
        editingApplyItems = selectApplyItems;

        resHome.setId(editingApplyItems.getStoreRes().getRes().getId());
    }

}
