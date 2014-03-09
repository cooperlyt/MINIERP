package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 3/6/14.
 */
@Name("storeResBackConfirm")
@Scope(ScopeType.CONVERSATION)
public class StoreResBackConfirm extends CancelOrderTaskHandle {

    @DataModel("confirmBackItems")
    private List<BackItem> confirmBackItems;

    @DataModelSelection
    private BackItem backItem;

    public BigDecimal getResTotalMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (BackItem item: confirmBackItems){
            result = result.add(item.getInputTotalPrice());
        }
        return result;
    }

    public void calcBackMoney(){
        orderBackHome.getInstance().setMoney(getResTotalMoney().subtract(orderBackHome.getInstance().getSaveMoney()));
    }

    @Override
    protected void initCancelOrderTask() {
        confirmBackItems = new ArrayList<BackItem>();
        for (ProductBackStoreIn backDispatch : orderBackHome.getInstance().getProductBackStoreIn()) {
            for (StockChangeItem storeChangeItem : backDispatch.getStockChange().getStockChangeItems()) {
                boolean matchOld = false;
                for (BackItem oldBackItem : orderBackHome.getInstance().getBackItems()) {
                    if (oldBackItem.getStoreRes().equals(storeChangeItem.getStoreRes())) {
                        matchOld = true;
                        confirmBackItems.add(new BackItem(storeChangeItem.getMasterCount(),
                                oldBackItem.getMoney(), oldBackItem.getMemo(), oldBackItem.getStoreRes(),
                                oldBackItem.getResUnit(), orderBackHome.getInstance()));
                        break;
                    }
                }

                if (!matchOld) {

                    confirmBackItems.add(new BackItem(orderBackHome.getInstance(), storeChangeItem.getStoreRes(),
                            storeChangeItem.getStoreRes().getRes().getResUnitByOutDefault(),
                            storeChangeItem.getMasterCount(),
                            BigDecimal.ZERO));
                }

            }
        }
        calcBackMoney();
    }

    @Override
    protected String completeOrderTask() {

        orderBackHome.getInstance().getBackItems().clear();
        orderBackHome.getInstance().getBackItems().addAll(confirmBackItems);

        if ("updated".equals(orderBackHome.update())) {
            return "taskComplete";
        } else
            return null;

    }

}
