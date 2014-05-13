package com.dgsoft.erp.action.store;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.action.StockHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StockChangeModel;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.application.FacesMessage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:29 AM
 */
public abstract class StoreOutAction {

    @In
    protected FacesMessages facesMessages;

    @In
    private ActionExecuteState actionExecuteState;

    @In(create = true)
    protected StockChangeHome stockChangeHome;

    protected abstract StockChangeItem getSelectOutItem();

    @In(create = true)
    protected StockHome stockHome;

    protected StockChangeItem editStockOutItem;

    protected List<StockChangeItem> storeOutItems = new ArrayList<StockChangeItem>();

    public StockChangeItem getEditStockOutItem() {
        return editStockOutItem;
    }

    public void setEditStockOutItem(StockChangeItem editStockOutItem) {
        this.editStockOutItem = editStockOutItem;
    }

    public void beginAddItem() {
        if (stockHome.isIdDefined()) {
            for (StockChangeItem item : storeOutItems) {
                if (item.getStock().getId().equals(stockHome.getInstance().getId())) {
                    editStockOutItem = item;
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,"sameStoreOutItemInfo");
                    return;
                }
            }
            editStockOutItem = new StockChangeItem(stockChangeHome.getInstance(), stockHome.getInstance(), BigDecimal.ZERO);
            editStockOutItem.setUseUnit(editStockOutItem.getRes().getResUnitByInDefault());
        }
        actionExecuteState.clearState();
        Logging.getLog(this.getClass()).warn("store out beginAdd not set STOCK");

    }

    public void addItem() {
        if (editStockOutItem.getCount().compareTo(editStockOutItem.getStock().getCount()) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "StoreOutOutStockError", editStockOutItem.getStock().getDisplayMasterCount());
            editStockOutItem.setCount(editStockOutItem.getStock().getCount());
            return;
        }

        if (editStockOutItem.getCount().compareTo(BigDecimal.ZERO) <= 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "StoreOutIsZeroError");
            return;
        }

        if (!storeOutItems.contains(editStockOutItem)) {
            storeOutItems.add(editStockOutItem);
        }
        editStockOutItem = null;
        stockHome.clearInstance();
        actionExecuteState.actionExecute();
    }


    public void removeItem() {
        storeOutItems.remove(getSelectOutItem());
    }


    protected void storeChange(boolean verify) {

        if (verify) {
            stockChangeHome.resStockChange(storeOutItems);
        } else {
            throw new IllegalStateException("not implement");
        }

    }

}
