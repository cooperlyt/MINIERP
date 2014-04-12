package com.dgsoft.erp.action.store;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.tuckey.web.filters.urlrewrite.Run;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:28 AM
 */

@Name("storeInAction")
@Scope(ScopeType.CONVERSATION)
public class StoreInAction {


    @In
    protected FacesMessages facesMessages;

    @In
    protected StockChangeHome stockChangeHome;

    @In(create=true)
    protected StockChangeItemCreate stockChangeItemCreate;

    //protected abstract StockChangeItem getSelectInItem();

    protected StockChangeItem editStockInItem;

    //protected List<StockChangeItem> storeChangeItems = new ArrayList<StockChangeItem>();

    public StockChangeItem getEditStockInItem() {
        return editStockInItem;
    }

    private String addItemLastState = "";

    public String getAddItemLastState() {
        return addItemLastState;
    }

    protected StoreResCountGroup<StockChangeItem> storeInItems = new StoreResCountGroup<StockChangeItem>();

    public StoreResCountGroup<StockChangeItem> getStoreInItems() {
        return storeInItems;
    }

    public String getEditItemCode() {
        if ((editStockInItem == null) || (editStockInItem.getStoreRes() == null)) {
            return null;
        }
        return editStockInItem.getStoreRes().getCode();
    }

    public void setEditItemCode(String code) {
        if (DataFormat.isEmpty(code)) {
            editStockInItem = null;
        }
        for (StockChangeItem item : storeInItems.values()) {
            if (item.getStoreRes().getCode().equals(code)) {
                editStockInItem = item;
                return;
            }
        }
        editStockInItem = null;
    }

    public void removeItem() {
        if (editStockInItem != null)
            storeInItems.remove(editStockInItem.getStoreRes());
    }


    public String addItem() {
        stockChangeItemCreate.getEditingItem().setStockChange(stockChangeHome.getInstance());
        addItemLastState = stockChangeItemCreate.addToGroup(storeInItems);
        if ("added".equals(addItemLastState)) stockChangeItemCreate.createNext();
        return addItemLastState;
    }

    //-------------------------------


    protected void storeChange(boolean verify) {


        if (verify) {
            stockChangeHome.resStockChange(storeInItems.values());
        } else {
            //prepareStoreIn();
        }


    }

//    private void prepareStoreIn(StoreInItem storeInItem) {
//        PrepareStockChange prepareStockChange = new PrepareStockChange(stockChangeHome.getInstance(),
//                storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount());
//        prepareStockChange.setNoConvertCounts(storeInItem.getStoreResCountInupt().getNoConvertCounts());
//        for (NoConvertCount noConvertCount : prepareStockChange.getNoConvertCounts()) {
//            noConvertCount.setPrepareStockChange(prepareStockChange);
//        }
//        stockChangeHome.getInstance().getPrepareStockChanges().add(prepareStockChange);
//
//    }

//    private void storeInNow(StoreInItem storeInItem) {
//        StockChangeItem stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
//                storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount());
//        stockChangeItem.setNoConvertCounts(storeInItem.getStoreResCountInupt().getNoConvertCounts());
//        for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
//            noConvertCount.setStockChangeItem(stockChangeItem);
//        }
//
//        if (storeResHome.isIdDefined()) {
//            Stock stock = storeResHome.getStock(stockChangeHome.getInstance().getStore());
//            if (stock != null) {
//                stockChangeItem.setStock(stock);
//                stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().add(storeInItem.getStoreResCountInupt().getMasterCount()));
//            }
//        }
//        if (stockChangeItem.getStock() == null) {
//            stockChangeItem.setStock(new Stock(storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount()));
//        }
//        stockChangeItem.getStock().setStore(stockChangeHome.getInstance().getStore());
//
//        if (storeInItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
//            for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
//                boolean added = false;
//                for (NoConvertCount stockCount : stockChangeItem.getStock().getNoConvertCounts()) {
//                    if (stockCount.getResUnit().equals(noConvertCount.getResUnit())) {
//                        stockCount.setCount(stockCount.getCount().add(noConvertCount.getCount()));
//                        added = true;
//                        break;
//                    }
//                }
//                if (!added) {
//                    stockChangeItem.getStock().getNoConvertCounts().add(new NoConvertCount(stockChangeItem.getStock(), noConvertCount.getResUnit(), noConvertCount.getCount()));
//                }
//            }
//        }
//
//
//        //stockChangeItem.getStock().getBatchStoreCounts()
//
//        stockChangeHome.getInstance().getStockChangeItems().add(stockChangeItem);
//    }

//    @Override
//    public String beginStoreChange() {
//        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
//            stockChangeHome.getInstance().setId("I" + numberBuilder.getDateNumber("storeInCode"));
//        }
//        return beginStoreIn();
//    }


}
