package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.erp.action.ProductBackStoreInHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;
import sun.rmi.runtime.Log;
import sun.util.logging.resources.logging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by cooper on 3/1/14.
 */

@Name("storeResBackStoreIn")
public class StoreResBackStoreIn extends CancelOrderTaskHandle {

    public final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private ProductBackStoreInHome productBackStoreInHome;

    @In(create = true)
    private StockChangeHome stockChangeHome;

    @In
    protected NumberBuilder numberBuilder;

    @In
    private ActionExecuteState actionExecuteState;

    @In
    private ResHelper resHelper;

    @In(create = true)
    private StoreResHome storeResHome;

    @DataModel("storeResBackStoreInItems")
    private List<ResBackItem> inItems;

    @DataModelSelection
    private ResBackItem resBackItem;

    private ResBackItem editingItem;

    private StockChangeItem newStockChangeItem;

    public StockChangeItem getNewStockChangeItem() {
        return newStockChangeItem;
    }

    public void setNewStockChangeItem(StockChangeItem newStockChangeItem) {
        this.newStockChangeItem = newStockChangeItem;
    }

    public ResBackItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(ResBackItem editingItem) {
        this.editingItem = editingItem;
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        Logging.getLog(getClass()).debug("StoreResBackStoreIn selectedStoreRes Observer ");
        newStockChangeItem = new StockChangeItem(storeRes,
                resHelper.getFormatHistory(storeRes.getRes()),
                resHelper.getFloatConvertRateHistory(storeRes.getRes()),
                storeRes.getRes().getResUnitByInDefault());

    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        Logging.getLog(getClass()).debug("StoreResBackStoreIn selectedRes Observer ");
        newStockChangeItem = new StockChangeItem(res,
                resHelper.getFormatHistory(res),
                resHelper.getFloatConvertRateHistory(res),
                res.getResUnitByInDefault());
    }

    private String addLastState = null;

    public String getAddLastState() {
        return addLastState;
    }

    public void setAddLastState(String addLastState) {
        this.addLastState = addLastState;
    }

    private String addNewInItemSuccess(){
        newStockChangeItem =  new StockChangeItem(newStockChangeItem.getStoreRes(),
                resHelper.getFormatHistory(newStockChangeItem.getRes()),
                resHelper.getFloatConvertRateHistory(newStockChangeItem.getRes()),
                newStockChangeItem.getRes().getResUnitByInDefault());

        addLastState = "added";
        return  addLastState;
    }

    public String addNewInItem() {
        storeResHome.setRes(newStockChangeItem.getRes(), newStockChangeItem.getFormats(), newStockChangeItem.getFloatConvertRate());


        if (!storeResHome.isIdDefined() && DataFormat.isEmpty(newStockChangeItem.getCode())){
            addLastState = "newStoreRes";
            return  addLastState;
        }else{
            if (!storeResHome.isIdDefined()){
                storeResHome.getInstance().setCode(newStockChangeItem.getCode());
            }
            newStockChangeItem.setStoreRes(storeResHome.getInstance());

            for (ResBackItem item : inItems) {
                if (item.getStockChangeItem().getStoreRes().equals(newStockChangeItem.getStoreRes())){
                    item.getStockChangeItem().add(newStockChangeItem);
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,"StoreChangeItemIsExists",newStockChangeItem.getStoreRes().getCode());

                    return  addNewInItemSuccess();
                }
            }
            inItems.add(new ResBackItem(newStockChangeItem));

            return  addNewInItemSuccess();

        }
    }

    public void deleteInItem() {
        if (resBackItem.getBackDispatchItem() != null){
            Logging.getLog(getClass()).warn("can't delete dispatchItem");
            return ;
        }
        inItems.remove(resBackItem);
    }

    public void editInItem(){
        editingItem = resBackItem;
        actionExecuteState.clearState();
    }

    @Override
    protected void initCancelOrderTask() {
        Logging.getLog(getClass()).debug("resBackDispatchTask init...");
        String storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }


        for (ProductBackStoreIn storeIn : orderBackHome.getInstance().getProductBackStoreIn()) {
            if (storeIn.getStore().getId().equals(storeId)) {
                productBackStoreInHome.setId(storeIn.getId());

                stockChangeHome.clearInstance();
                stockChangeHome.getInstance().setStore(storeIn.getStore());
                stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.SELL_BACK);
                stockChangeHome.getInstance().setVerify(true);

                initInItem();
                return;
            }
        }
        throw new ProcessDefineException("Order Store out store ID not exists");
    }


    private void initInItem() {
        inItems = new ArrayList<ResBackItem>();
        for (BackDispatchItem item : productBackStoreInHome.getInstance().getBackDispatchItems()) {
            inItems.add(new ResBackItem(item));
        }
    }

    @Override
    protected String completeOrderTask() {
//        for (BackDispatchItem item : productBackStoreInHome.getInstance().getBackDispatchItems()) {
//            stockChangeHome.resStockChange(item, null);
//        }

        for(ResBackItem item: inItems){
            stockChangeHome.resStockChange(item.getStockChangeItem());
        }

        stockChangeHome.getInstance().setId(orderBackHome.getInstance().getId() + "-" + numberBuilder.getNumber("storeInCode"));
        stockChangeHome.getInstance().setProductBackStoreIn(productBackStoreInHome.getInstance());

        Logging.getLog(getClass()).debug("res back store in change stoce item count:" + stockChangeHome.getInstance().getStockChangeItems().size());
        productBackStoreInHome.getInstance().setStockChange(stockChangeHome.getReadyInstance());
        productBackStoreInHome.getInstance().getOrderBack().setResComplete(true);


        if ("updated".equals(productBackStoreInHome.update())) {
            return "taskComplete";
        } else
            return null;
    }

    public class ResBackItem {

        private BackDispatchItem backDispatchItem;

        private StockChangeItem stockChangeItem;

        public ResBackItem(BackDispatchItem backDispatchItem) {
            this.backDispatchItem = backDispatchItem;
            this.stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
                    backDispatchItem.getStoreRes(), backDispatchItem.getMasterCount());
        }

        public ResBackItem(StockChangeItem stockChangeItem) {
            this.stockChangeItem = stockChangeItem;
        }

        public BackDispatchItem getBackDispatchItem() {
            return backDispatchItem;
        }

        public void setBackDispatchItem(BackDispatchItem backDispatchItem) {
            this.backDispatchItem = backDispatchItem;
        }

        public StockChangeItem getStockChangeItem() {
            return stockChangeItem;
        }

        public void setStockChangeItem(StockChangeItem stockChangeItem) {
            this.stockChangeItem = stockChangeItem;
        }
    }
}
