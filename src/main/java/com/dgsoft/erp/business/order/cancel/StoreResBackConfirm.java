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
    private List<BackItem> getConfirmBackItems(){
        return orderBackHome.getBackItems();
    }

    public BigDecimal getResTotalMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (BackItem item: getConfirmBackItems()){
            result = result.add(item.getTotalMoney());
        }
        return result;
    }


    @Override
    protected String completeOrderTask() {
        if ("updated".equals(orderBackHome.update())) {
            return "taskComplete";
        } else
            return null;

    }

}
