package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StockChangeModel;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.international.StatusMessage;

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
public abstract class StoreOutAction<E extends StockChangeModel> extends StoreChangeHelper<E> implements StoreChangeAction {

    @In
    protected RunParam runParam;

    @In
    protected NumberBuilder numberBuilder;

    protected abstract String storeOut();

    protected abstract String beginStoreOut();

    private boolean groupByRes = true;

    protected List<Stock> storeOutItems = new ArrayList<Stock>();

    private String selectStockId;

    private Stock editingItem;

    public boolean isGroupByRes() {
        return groupByRes;
    }

    public void setGroupByRes(boolean groupByRes) {
        this.groupByRes = groupByRes;
    }

    public List<Stock> getStoreOutItems() {
        return storeOutItems;
    }

    public void setStoreOutItems(List<Stock> storeOutItems) {
        this.storeOutItems = storeOutItems;
    }

    public String getSelectStockId() {
        return selectStockId;
    }

    public void setSelectStockId(String selectStockId) {
        this.selectStockId = selectStockId;
    }

    public Stock getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(Stock editingItem) {
        this.editingItem = editingItem;
    }

    public void beginAddItem() {
        editingItem = null;
        for (Stock outItem : storeOutItems) {
            if (outItem.getId().equals(selectStockId)) {
                editingItem = outItem;
                break;
            }
        }
        if (editingItem == null) {
            editingItem = getEntityManager().find(Stock.class, selectStockId);

        }

    }

    @org.jboss.seam.annotations.Observer(value = "erp.resLocateSelected", create = false)
    public void codeTypeByRes(Res res) {
        editingItem = null;
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeOutOnlySelectStoreRes");
    }

    @org.jboss.seam.annotations.Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {

        for (Stock item : storeOutItems) {
            if (item.getStoreRes().getId().equals(storeRes.getId())) {
                editingItem = item;
                return;
            }
        }

        for (Stock stock : stockChangeHome.getInstance().getStore().getStocks()) {
            if (stock.getStoreRes().getId().equals(storeRes.getId())) {

                editingItem = stock;
                return;
            }
        }

        editingItem = null;
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResNotInStore");

    }


    @Override
    public String addItem() {

        boolean inItems = false;
        for (Stock outItem : storeOutItems) {
            if (outItem.getId().equals(editingItem.getId())) {
                inItems = true;
                break;
            }
        }

        if (!inItems) {
            storeOutItems.add(editingItem);
        }

        if (editingItem.getCount().compareTo(editingItem.getCount()) > 0) {
            editingItem.setCount(editingItem.getCount());
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "storeOutCountNotEnough");
        }
        editingItem = null;
        return "added";
    }


    @Override
    public void removeItem() {
        for (Stock outItem : storeOutItems) {
            if (outItem.getId().equals(selectStockId)) {
                storeOutItems.remove(outItem);
                break;
            }
        }
    }

    private void storeOutNow(Stock outItem) {

        StockChangeItem stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
                outItem, outItem.getCount());

        stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().subtract(outItem.getCount()));


        if (stockChangeItem.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
                for (NoConvertCount stockCount : stockChangeItem.getStock().getNoConvertCounts()) {
                    if (stockCount.getResUnit().equals(noConvertCount.getResUnit())) {
                        stockCount.setCount(stockCount.getCount().subtract(noConvertCount.getCount()));
                        break;
                    }
                }

            }
        }

        //TODO batch

        stockChangeHome.getInstance().getStockChangeItems().add(stockChangeItem);
    }

    @Override
    public String beginStoreChange() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
            stockChangeHome.getInstance().setId("O" + numberBuilder.getDateNumber("storeOutCode"));
        }
        return beginStoreOut();
    }


    @Override
    protected String storeChange(boolean verify) {

        boolean haveItem = false;
        for (Stock outItem : storeOutItems) {

            if (outItem.getCount().compareTo(BigDecimal.ZERO) == 0) {

                continue;
            }


            if (verify) {
                storeOutNow(outItem);
            } else {
                throw new IllegalStateException("not implement");
            }


            haveItem = true;
        }
        if (!haveItem) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeOutNotItem");
            return null;
        }
        getInstance().setStockChange(stockChangeHome.getInstance());
        persist();
        clearInstance();

        return storeOut();
    }


    public List<StoreOutItemGroup> getStoreOutItemGroups() {
        List<StoreOutItemGroup> result = new ArrayList<StoreOutItemGroup>();
        if (groupByRes) {
            Map<Res, List<Stock>> resGroup = new HashMap<Res, List<Stock>>();
            for (Stock storeOutItem : storeOutItems) {
                List<Stock> temp = resGroup.get(storeOutItem.getStoreRes().getRes());
                if (temp == null) {
                    temp = new ArrayList<Stock>();
                    resGroup.put(storeOutItem.getStoreRes().getRes(), temp);
                }
                temp.add(storeOutItem);
            }
            for (Res res : resGroup.keySet()) {
                result.add(new StoreOutItemGroup(res.getName() + "(" + res.getCode() + ")", resGroup.get(res)));
            }
        } else {
            Map<StoreRes, List<Stock>> storeResGroup = new HashMap<StoreRes, List<Stock>>();
            for (Stock storeOutItem : storeOutItems) {
                List<Stock> temp = storeResGroup.get(storeOutItem.getStoreRes());
                if (temp == null) {
                    temp = new ArrayList<Stock>();
                    storeResGroup.put(storeOutItem.getStoreRes(), temp);
                }
                temp.add(storeOutItem);
            }
            for (StoreRes storeRes : storeResGroup.keySet()) {
                result.add(new StoreOutItemGroup(storeRes, storeResGroup.get(storeRes)));
            }

        }
        return result;
    }


    public static class StoreOutItemGroup {

        private String title;

        private List<Stock> items;

        private StoreRes storeRes;


        public StoreOutItemGroup(String title, List<Stock> items) {
            this.title = title;
            this.items = items;
        }

        public StoreOutItemGroup(StoreRes storeRes, List<Stock> items) {
            this.items = items;
            this.storeRes = storeRes;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public List<Stock> getItems() {
            return items;
        }

        public void setItems(List<Stock> items) {
            this.items = items;
        }

        public StoreResCount getTotalCount() {
            if (storeRes == null) {
                return null;
            }

            StoreResCount result = new StoreResCount(storeRes, BigDecimal.ZERO);
            for (Stock storeOutItem : items) {
                result.add(storeOutItem);
            }
            return result;
        }
    }
}
