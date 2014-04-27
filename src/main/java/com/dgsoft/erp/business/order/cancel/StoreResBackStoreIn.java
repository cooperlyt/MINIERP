package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.jbpm.TaskDescription;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.business.order.BackItemCreate;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cooper on 3/1/14.
 */

@Name("storeResBackStoreIn")
public class StoreResBackStoreIn extends CancelOrderTaskHandle {

    public final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

//    @In(create = true)
//    private BackDispatchHome backDispatchHome;

    @In(create = true)
    private StockChangeHome stockChangeHome;

    @In
    private ActionExecuteState actionExecuteState;

    @In(required = false)
    private BackItemCreate backItemCreate;

    @In(create = true)
    private StoreResHome storeResHome;

    @In(create = true)
    private ResHelper resHelper;

    @In
    protected RunParam runParam;

    @DataModel("storeResBackStoreInItems")
    private List<BackItem> inItems;

    @DataModelSelection
    private BackItem resBackItem;

    private BackItem editingItem;

    private BackDispatch dispatch;


    public BackItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(BackItem editingItem) {
        this.editingItem = editingItem;
    }

    private String addLastState = null;

    public String getAddLastState() {
        return addLastState;
    }

    public String addNewInItem() {

        BackItem item = backItemCreate.getEditingItem();

        storeResHome.setRes(item.getRes(), item.getFormats(), item.getFloatConvertRate());


        if (!storeResHome.isIdDefined() && DataFormat.isEmpty(item.getCode())) {
            item.setCode(resHelper.genStoreResCode(item.getRes().getCode(), item.getFormats(),
                    (item.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) ?
                            item.getFloatConvertRate().toString() : null
            ));
            addLastState = "newStoreRes";
            return addLastState;
        } else {

            if (!storeResHome.isIdDefined()) {
                if (!item.getCode().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", item.getCode(),
                            runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    addLastState = "newStoreRes";
                    return addLastState;
                }


                storeResHome.getInstance().setCode(item.getCode());
                item.setStoreRes(storeResHome.getReadyInstance());
            } else {

                for (BackItem bItem : inItems) {
                    if (bItem.getStoreRes().equals(storeResHome.getInstance())) {
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "sameStoreInItemInfo");
                        addLastState = "fail";
                        return addLastState;
                    }
                }
                item.setStoreRes(storeResHome.getInstance());
            }

            item.setDispatch(dispatch);
            item.setOrderBack(orderBackHome.getInstance());
            item.setMoney(BigDecimal.ZERO);
            item.setResUnit(item.getRes().getResUnitByOutDefault());
            item.setBackItemStatus(BackItem.BackItemStatus.DISPATCH);
            item.setRebate(new BigDecimal("100"));
            item.setTotalMoney(BigDecimal.ZERO);
            orderBackHome.getInstance().getBackItems().add(item);
            inItems.add(item);

            backItemCreate.createNext();
            addLastState = "added";
            return addLastState;

        }
    }

    public void deleteInItem() {
        inItems.remove(resBackItem);
        orderBackHome.getInstance().getBackItems().remove(resBackItem);
    }

    public void editInItem() {
        editingItem = resBackItem;
        actionExecuteState.clearState();
    }

    public void saveEditItem(){
        if (editingItem.getCount().compareTo(BigDecimal.ZERO) <= 0){
            inItems.remove(editingItem);
            orderBackHome.getInstance().getBackItems().remove(editingItem);
        }
        actionExecuteState.actionExecute();
    }

    @Override
    protected void initCancelOrderTask() {
        Logging.getLog(getClass()).debug("resBackDispatchTask init...");
        String storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }


        for (BackDispatch storeIn : orderBackHome.getInstance().getBackDispatchs()) {
            if (storeIn.getStore().getId().equals(storeId)) {
                dispatch = storeIn;
                stockChangeHome.clearInstance();
                stockChangeHome.getInstance().setStore(storeIn.getStore());
                stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.SELL_BACK);
                stockChangeHome.getInstance().setVerify(true);

                inItems = new SetLinkList<BackItem>(dispatch.getBackItems());
                Collections.sort(inItems, new Comparator<BackItem>() {
                    @Override
                    public int compare(BackItem o1, BackItem o2) {
                        return o1.getStoreRes().compareTo(o2.getStoreRes());
                    }
                });
                return;
            }
        }
        throw new ProcessDefineException("Order Store out store ID not exists");
    }

    @Override
    protected String completeOrderTask() {

        stockChangeHome.getInstance().setId("RI-" + orderBackHome.getInstance().getId());
        stockChangeHome.getInstance().setBackDispatch(dispatch);

        for (BackItem item : inItems) {
            if (item.getCount().compareTo(BigDecimal.ZERO) > 0) {
                item.calcMoney();
                item.setBackItemStatus(BackItem.BackItemStatus.STORE_IN);
                stockChangeHome.resStockChange(new StockChangeItem(stockChangeHome.getInstance(), item.getStoreRes(), item.getCount()));
            }
        }


        Logging.getLog(getClass()).debug("res back store in change stoce item count:" + stockChangeHome.getInstance().getStockChangeItems().size());
        dispatch.setStockChange(stockChangeHome.getReadyInstance());
        dispatch.setStoreOut(true);
        orderBackHome.getInstance().setResComplete(true);


        if ("updated".equals(orderBackHome.update())) {
            return "taskComplete";
        } else
            return null;
    }

}
