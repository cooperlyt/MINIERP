package com.dgsoft.erp.action.store;

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
    protected RunParam runParam;

    @In
    protected NumberBuilder numberBuilder;

    @In
    protected FacesMessages facesMessages;

    @In(create = true)
    protected StockChangeHome stockChangeHome;

    protected abstract String storeOut();

    protected abstract String beginStoreOut();

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
        }
        Logging.getLog(this.getClass()).warn("store out beginAdd not set STOCK");

    }

    @org.jboss.seam.annotations.Observer(value = "erp.resLocateSelected", create = false)
    public void codeTypeByRes(Res res) {
        editStockOutItem = null;
        stockHome.clearInstance();
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeOutOnlySelectStoreRes");
    }

    @org.jboss.seam.annotations.Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {

        for (Stock stock : storeRes.getStocks()) {
            if (stock.getStore().getId().equals(stockChangeHome.getInstance().getStore().getId())) {
                stockHome.setId(stock.getId());
                beginAddItem();
                return;
            }
        }
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResNotInStore");
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

    }


    public void removeItem() {
        storeOutItems.remove(getSelectOutItem());
    }


    public String begin() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
            stockChangeHome.getInstance().setId("O" + numberBuilder.getDateNumber("storeOutCode"));
        }
        return beginStoreOut();
    }


    protected String storeChange(boolean verify) {

        if (verify) {
            stockChangeHome.resStockChange(storeOutItems);
        } else {
            throw new IllegalStateException("not implement");
        }

        return storeOut();
    }


//    public List<StoreOutItemGroup> getStoreOutItemGroups() {
//        List<StoreOutItemGroup> result = new ArrayList<StoreOutItemGroup>();
//        if (groupByRes) {
//            Map<Res, List<Stock>> resGroup = new HashMap<Res, List<Stock>>();
//            for (Stock storeOutItem : storeOutItems) {
//                List<Stock> temp = resGroup.get(storeOutItem.getStoreRes().getRes());
//                if (temp == null) {
//                    temp = new ArrayList<Stock>();
//                    resGroup.put(storeOutItem.getStoreRes().getRes(), temp);
//                }
//                temp.add(storeOutItem);
//            }
//            for (Res res : resGroup.keySet()) {
//                result.add(new StoreOutItemGroup(res.getName() + "(" + res.getCode() + ")", resGroup.get(res)));
//            }
//        } else {
//            Map<StoreRes, List<Stock>> storeResGroup = new HashMap<StoreRes, List<Stock>>();
//            for (Stock storeOutItem : storeOutItems) {
//                List<Stock> temp = storeResGroup.get(storeOutItem.getStoreRes());
//                if (temp == null) {
//                    temp = new ArrayList<Stock>();
//                    storeResGroup.put(storeOutItem.getStoreRes(), temp);
//                }
//                temp.add(storeOutItem);
//            }
//            for (StoreRes storeRes : storeResGroup.keySet()) {
//                result.add(new StoreOutItemGroup(storeRes, storeResGroup.get(storeRes)));
//            }
//
//        }
//        return result;
//    }

}
