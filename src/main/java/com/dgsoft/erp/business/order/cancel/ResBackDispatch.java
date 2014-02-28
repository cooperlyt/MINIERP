package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
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

    private Map<StoreRes, StoreResCount> waitDispatchItems;

    @In
    private ActionExecuteState actionExecuteState;

    @DataModel
    private List<ProductBackStoreIn> resBackDispatcheds;

    @DataModelSelection
    private ProductBackStoreIn selectDispatchItem;

    @In
    private ResHelper resHelper;

    private BackDispatchItem newDispatchItem;

    private Store selectStore;

    private StoreRes selectDispatchStoreRes;

    public BackDispatchItem getNewDispatchItem() {
        return newDispatchItem;
    }

    public void setNewDispatchItem(BackDispatchItem newDispatchItem) {
        this.newDispatchItem = newDispatchItem;
    }

    private List<BackItem> backItems;

    public void init(List<BackItem> backItems) {
        this.backItems = backItems;
        waitDispatchItems = new HashMap<StoreRes, StoreResCount>(backItems.size());
        waitDispatchItems = resHelper.groupByStoreRes(backItems);
        resBackDispatcheds = new ArrayList<ProductBackStoreIn>();
        selectStore = null;
    }

    public void reset(){
        init(backItems);
    }

    public List<Map.Entry<StoreRes, StoreResCount>> getWaitDispatchItems() {
        List<Map.Entry<StoreRes, StoreResCount>> result = new ArrayList<Map.Entry<StoreRes, StoreResCount>>();
        for (Map.Entry<StoreRes, StoreResCount> entry : waitDispatchItems.entrySet()) {
            if (entry.getValue().getMasterCount().compareTo(BigDecimal.ZERO) > 0) {
                result.add(entry);
            }
        }
        Collections.sort(result, new Comparator<Map.Entry<StoreRes, StoreResCount>>() {
            @Override
            public int compare(Map.Entry<StoreRes, StoreResCount> o1, Map.Entry<StoreRes, StoreResCount> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public String getSelectWaitDispatchItemId() {
        if (selectDispatchStoreRes == null) {
            return null;
        } else {
            return selectDispatchStoreRes.getId();
        }
    }

    public void setSelectWaitDispatchItemId(String id) {
        selectDispatchStoreRes = null;
        if (!StringUtil.isEmpty(id)) {
            for (Map.Entry<StoreRes, StoreResCount> entry : waitDispatchItems.entrySet()) {
                if (entry.getKey().getId().equals(id)) {
                    selectDispatchStoreRes = entry.getKey();
                    return;
                }
            }
        }

    }

    public Store getSelectStore() {
        return selectStore;
    }

    public void setSelectStore(Store selectStore) {
        this.selectStore = selectStore;
    }

    public void beginDispatchItem() {
        newDispatchItem = new BackDispatchItem(selectDispatchStoreRes, BigDecimal.ZERO);
        selectStore = null;
        actionExecuteState.clearState();
    }

    public void addDispatchItem() {
        if (newDispatchItem.getMasterCount().compareTo(waitDispatchItems.get(newDispatchItem.getStoreRes()).getMasterCount()) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_item_count_over_error",
                    newDispatchItem.getDisplayMasterCount(),
                    waitDispatchItems.get(newDispatchItem.getStoreRes()).getDisplayMasterCount());
        } else {
            addDispatchItem(newDispatchItem);
            actionExecuteState.actionExecute();
        }


    }

    private void addDispatchItem(BackDispatchItem newItem) {
        boolean added = false;

        for (ProductBackStoreIn item : resBackDispatcheds) {

            if (item.getStore().getId().equals(selectStore.getId())) {
                for (BackDispatchItem bItem : item.getBackDispatchItems()) {
                    if (bItem.getStoreRes().equals(newItem.getStoreRes())) {
                        bItem.add(newItem);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    item.getBackDispatchItems().add(newItem);
                    newItem.setProductBackStoreIn(item);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            ProductBackStoreIn newDispatch = new ProductBackStoreIn(selectStore);
            resBackDispatcheds.add(newDispatch);
            newDispatch.getBackDispatchItems().add(newItem);
            newItem.setProductBackStoreIn(newDispatch);
        }
        waitDispatchItems.get(newItem.getStoreRes()).subtract(newItem);
    }

    public void dispatchAll() {
        for (Map.Entry<StoreRes, StoreResCount> entry : getWaitDispatchItems()) {
            addDispatchItem(new BackDispatchItem(entry.getKey(), entry.getValue().getMasterCount()));
        }

    }

    public void removeDispatched() {
        for (BackDispatchItem item : selectDispatchItem.getBackDispatchItems()) {
            waitDispatchItems.get(item.getStoreRes()).add(item);
        }
        resBackDispatcheds.remove(selectDispatchItem);
    }

    public List<ProductBackStoreIn> getResBackDispatcheds() {
        return resBackDispatcheds;
    }

    public  List<ProductBackStoreIn> getResBackDispatcheds(OrderBack orderBack){
        for (ProductBackStoreIn pbsi: resBackDispatcheds){
            pbsi.setOrderBack(orderBack);
        }
        return resBackDispatcheds;
    }

    public boolean isComplete(){
        return getWaitDispatchItems().isEmpty();
    }


}
