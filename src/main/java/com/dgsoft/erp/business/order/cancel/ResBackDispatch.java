package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 2/27/14.
 */
@Name("resBackDispatch")
@Scope(ScopeType.CONVERSATION)
public class ResBackDispatch {

    @In
    private OrderBackHome orderBackHome;

    private Map<StoreRes, StoreResCount> waitDispatchItems;

    @In
    private ActionExecuteState actionExecuteState;

    @DataModel
    private List<ProductBackStoreIn> dispatchs;

    @DataModelSelection
    private ProductBackStoreIn selectDispatchItem;

    @In
    private ResHelper resHelper;

    private BackDispatchItem newDispatchItem;

    private Store selectStore;

    private StoreRes selectDispatchStoreRes;


    public void init() {
        waitDispatchItems = new HashMap<StoreRes, StoreResCount>(orderBackHome.getInstance().getBackItems().size());
        waitDispatchItems = resHelper.groupByStoreRes(orderBackHome.getInstance().getBackItemList());
        dispatchs = new ArrayList<ProductBackStoreIn>();
        selectStore = null;

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
        if (!StringUtil.isEmpty(id)) {
            for (Map.Entry<StoreRes, StoreResCount> entry : waitDispatchItems.entrySet()) {
                if (entry.getKey().getId().equals(id)) {
                    selectDispatchStoreRes = entry.getKey();
                    return;
                }
            }
        }
        selectDispatchStoreRes = null;
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
        addDispatchItem(newDispatchItem);
    }

    private void addDispatchItem(BackDispatchItem newItem) {
        boolean added = false;

        for (ProductBackStoreIn item : dispatchs) {

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
            ProductBackStoreIn newDispatch = new ProductBackStoreIn(orderBackHome.getInstance(), selectStore);
            dispatchs.add(newDispatch);
            newDispatch.getBackDispatchItems().add(newItem);
            newItem.setProductBackStoreIn(newDispatch);
        }
        waitDispatchItems.get(selectDispatchStoreRes).subtract(newItem);
    }

    public void dispatchAll() {
        for (Map.Entry<StoreRes, StoreResCount> entry : waitDispatchItems.entrySet()) {
            addDispatchItem(new BackDispatchItem(entry.getKey(), entry.getValue().getMasterCount()));
        }

    }

    public void removeDispatched() {
        for (BackDispatchItem item : selectDispatchItem.getBackDispatchItems()) {
            waitDispatchItems.get(item.getStoreRes()).add(item);
        }
        dispatchs.remove(selectDispatchItem);
    }

    public List<ProductBackStoreIn> getDispatchs() {
        return dispatchs;
    }


}
