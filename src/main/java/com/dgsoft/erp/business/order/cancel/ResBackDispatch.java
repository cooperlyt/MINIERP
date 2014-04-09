package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 2/27/14.
 */
@Name("resBackDispatch")
@Scope(ScopeType.CONVERSATION)
public class ResBackDispatch {

    @In
    private FacesMessages facesMessages;

    //@In
    //private OrderBackHome orderBackHome;

    @DataModel(value = "backDispatchWaitItems")
    private List<BackItem> waitDispatchItems;

    @DataModelSelection
    private BackItem selectBackItem;

    private BackItem editItem;

    private StoreResCount dispatchCount;

    @In
    private ActionExecuteState actionExecuteState;

    private List<BackDispatch> resBackDispatcheds;

    private Store selectStore;

    //private List<BackItem> backItems;

    private OrderBack orderBack;

    private Map<BackItem, List<BackItem>> splitItems;

    public void init(OrderBack orderBack) {
        this.orderBack = orderBack;
        splitItems = new HashMap<BackItem, List<BackItem>>();
        waitDispatchItems = orderBack.getBackItemList();
        for (BackItem item : waitDispatchItems) {
            splitItems.put(item, new ArrayList<BackItem>());
        }
        resBackDispatcheds = new ArrayList<BackDispatch>();
        selectStore = null;
    }

    public void reset() {
        for (Map.Entry<BackItem, List<BackItem>> entry : splitItems.entrySet()) {
            for (BackItem splitItem : entry.getValue()) {
                entry.getKey().setCount(entry.getKey().getCount().add(splitItem.getCount()));
                entry.getKey().calcMoney();
            }
        }
        init(orderBack);
    }


    public Store getSelectStore() {
        return selectStore;
    }

    public void setSelectStore(Store selectStore) {
        this.selectStore = selectStore;
    }

    public void beginDispatchItem() {
        selectStore = null;
        dispatchCount = new StoreResCount(selectBackItem.getStoreRes(),selectBackItem.getCount());
        editItem = selectBackItem;
        actionExecuteState.clearState();
    }

    public void addDispatchItem() {
        if (dispatchCount.getMasterCount().compareTo(editItem.getMasterCount()) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_item_count_over_error",
                    dispatchCount.getDisplayMasterCount(),editItem.getDisplayMasterCount());
        } else {
            addDispatchItem(editItem,dispatchCount);
            actionExecuteState.actionExecute();
        }
    }

    private void addDispatchItem(BackItem backItem, StoreResCountEntity count) {
        boolean added = false;
        boolean all = backItem.getCount().compareTo(count.getCount()) == 0;
        BackItem dispatchItem;
        if (all){
            dispatchItem = backItem;
        }else{
            dispatchItem = new BackItem(orderBack,backItem.getStoreRes(),count.getCount(),
                    backItem.getMoney(),backItem.getResUnit(), BackItem.BackItemStatus.CREATE,backItem.getRebate(),backItem.getMemo());
            splitItems.get(backItem).add(dispatchItem);
        }

        for (BackDispatch item : resBackDispatcheds) {

            if (item.getStore().getId().equals(selectStore.getId())) {
                item.getBackItems().add(dispatchItem);
                dispatchItem.setDispatch(item);
                added = true;
                break;
            }
        }

        if (!added) {
            BackDispatch newDispatch = new BackDispatch(orderBack,selectStore);
            resBackDispatcheds.add(newDispatch);
            newDispatch.getBackItems().add(dispatchItem);
            dispatchItem.setDispatch(newDispatch);
        }

        if (all){
            waitDispatchItems.remove(backItem);
        }

    }

    public void dispatchAll() {
        for (BackItem item: waitDispatchItems){
            addDispatchItem(item,item);
        }
    }

    public boolean isComplete() {
        return waitDispatchItems.isEmpty();
    }

    public List<BackDispatch> getResBackDispatcheds() {
        return resBackDispatcheds;
    }
}
