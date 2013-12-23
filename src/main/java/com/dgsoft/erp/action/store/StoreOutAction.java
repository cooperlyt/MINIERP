package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StockChangeModel;

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

    protected abstract String storeOut();

    private boolean groupByRes = true;

    private List<StoreOutItem> storeOutItems = new ArrayList<StoreOutItem>();

    private String selectStockId;

    private StoreOutItem editingItem;

    public boolean isGroupByRes() {
        return groupByRes;
    }

    public void setGroupByRes(boolean groupByRes) {
        this.groupByRes = groupByRes;
    }

    public List<StoreOutItem> getStoreOutItems() {
        return storeOutItems;
    }

    public void setStoreOutItems(List<StoreOutItem> storeOutItems) {
        this.storeOutItems = storeOutItems;
    }

    public String getSelectStockId() {
        return selectStockId;
    }

    public void setSelectStockId(String selectStockId) {
        this.selectStockId = selectStockId;
    }

    public StoreOutItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(StoreOutItem editingItem) {
        this.editingItem = editingItem;
    }

    public void beginAddItem(){
      // getEntityManager().find()
    }

    @Override
    public String addItem() {
        return null;
    }


    @Override
    public void removeItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String storeChange(boolean verify) {

        return storeOut();
    }



    public List<StoreOutItemGroup> getStoreOutItemGroups() {
        List<StoreOutItemGroup> result = new ArrayList<StoreOutItemGroup>();
        if (groupByRes) {
            Map<Res, List<StoreOutItem>> resGroup = new HashMap<Res, List<StoreOutItem>>();
            for (StoreOutItem storeOutItem : storeOutItems) {
                List<StoreOutItem> temp = resGroup.get(storeOutItem.getStock().getStoreRes().getRes());
                if (temp == null) {
                    temp = new ArrayList<StoreOutItem>();
                    resGroup.put(storeOutItem.getStock().getStoreRes().getRes(), temp);
                }
                temp.add(storeOutItem);
            }
            for (Res res : resGroup.keySet()) {
                result.add(new StoreOutItemGroup(res.getName() + "(" + res.getCode() + ")", resGroup.get(res)));
            }
        } else {
            Map<StoreRes, List<StoreOutItem>> storeResGroup = new HashMap<StoreRes, List<StoreOutItem>>();
            for (StoreOutItem storeOutItem : storeOutItems) {
                List<StoreOutItem> temp = storeResGroup.get(storeOutItem.getStock().getStoreRes());
                if (temp == null) {
                    temp = new ArrayList<StoreOutItem>();
                    storeResGroup.put(storeOutItem.getStock().getStoreRes(), temp);
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

        private List<StoreOutItem> items;

        private StoreRes storeRes;


        public StoreOutItemGroup(String title, List<StoreOutItem> items) {
            this.title = title;
            this.items = items;
        }

        public StoreOutItemGroup(StoreRes storeRes, List<StoreOutItem> items) {
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

        public List<StoreOutItem> getItems() {
            return items;
        }

        public void setItems(List<StoreOutItem> items) {
            this.items = items;
        }

        public ResCount getTotalCount() {
            if (storeRes == null){
                return null;
            }

            ResCount result = storeRes.getResCount(BigDecimal.ZERO);
            for (StoreOutItem storeOutItem : items) {
                result.add(storeOutItem.getStoreResCountInupt());
            }
            return result;
        }
    }

    public static class StoreOutItem {

        protected StoreResCountInupt storeResCountInupt;

        private Stock stock;

        public StoreOutItem(Stock stock) {
            storeResCountInupt = new StoreResCountInupt(stock.getStoreRes().getRes(), stock.getStoreRes().getRes().getResUnitByOutDefault());
            if (stock.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                storeResCountInupt.setFloatConvertRate(stock.getStoreRes().getFloatConversionRate());
            }
            this.stock = stock;
        }

        public StoreResCountInupt getStoreResCountInupt() {
            return storeResCountInupt;
        }

        public void setStoreResCountInupt(StoreResCountInupt storeResCountInupt) {
            this.storeResCountInupt = storeResCountInupt;
        }

        public Stock getStock() {
            return stock;
        }

        public void setStock(Stock stock) {
            this.stock = stock;
        }

        public boolean same(Stock other) {
            if (other == null || stock == null) {
                return false;
            }
            return other.getId().equals(stock.getId());
        }
    }
}
