package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StockChangeModel;
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

    protected List<StoreOutItem> storeOutItems = new ArrayList<StoreOutItem>();

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
        editingItem = null;
        for (StoreOutItem outItem: storeOutItems){
            if (outItem.getStock().getId().equals(selectStockId)){
                editingItem = outItem;
                break;
            }
        }
        if (editingItem == null){
            editingItem = new StoreOutItem(getEntityManager().find(Stock.class,selectStockId));

        }

    }

    @org.jboss.seam.annotations.Observer(value = "erp.resLocateSelected", create = false)
    public void codeTypeByRes(Res res) {
        editingItem = null;
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"storeOutOnlySelectStoreRes");
    }

    @org.jboss.seam.annotations.Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {

        for (StoreOutItem item: storeOutItems){
            if (item.getStock().getStoreRes().getId().equals(storeRes.getId())){
                editingItem = item;
                return;
            }
        }

        for (Stock stock: stockChangeHome.getInstance().getStore().getStocks()){
             if (stock.getStoreRes().getId().equals(storeRes.getId())){

                 editingItem = new StoreOutItem(stock);
                 return;
             }
        }

        editingItem = null;
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"storeResNotInStore");

    }


    @Override
    public String addItem() {

        boolean inItems =false;
        for (StoreOutItem outItem: storeOutItems){
            if (outItem.getStock().getId().equals(editingItem.getStock().getId())){
                inItems = true;
                break;
            }
        }

        if (!inItems){
            storeOutItems.add(editingItem);
        }

        if (editingItem.getStoreResCountInupt().getMasterCount().compareTo(editingItem.getStock().getCount()) > 0){
            editingItem.getStoreResCountInupt().setMasterCount(editingItem.getStock().getCount());
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN,"storeOutCountNotEnough");
        }
        editingItem = null;
        return "added";
    }


    @Override
    public void removeItem() {
        for (StoreOutItem outItem: storeOutItems){
            if (outItem.getStock().getId().equals(selectStockId)){
                storeOutItems.remove(outItem);
                break;
            }
        }
    }

    private void storeOutNow(StoreOutItem outItem) {

        StockChangeItem stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
                outItem.getStock(), outItem.getStoreResCountInupt().getMasterCount(), true);
        stockChangeItem.setNoConvertCounts(outItem.getStoreResCountInupt().getNoConvertCounts());
        for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
            noConvertCount.setStockChangeItem(stockChangeItem);
        }
        if (stockChangeItem.isStoreOut()){
            stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().subtract(outItem.getStoreResCountInupt().getMasterCount()));
        }else{
            stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().add(outItem.getStoreResCountInupt().getMasterCount()));
        }



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
        for (StoreOutItem outItem : storeOutItems) {

            if (outItem.getStoreResCountInupt().getMasterCount().compareTo(BigDecimal.ZERO) == 0) {

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
