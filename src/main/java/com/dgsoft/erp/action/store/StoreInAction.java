package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.ResLocateHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:28 AM
 */
public abstract class StoreInAction<E extends StockChangeModel> extends StoreChangeHelper<E> implements StoreChangeAction {

    @In
    protected RunParam runParam;

    @In
    protected NumberBuilder numberBuilder;

    @In(required = false)
    protected StoreResFormatFilter storeResFormatFilter;

    @In(required = false)
    protected ResLocateHome resLocateHome;

    @In(create = true)
    protected StoreResHome storeResHome;

    @DataModel(value = "storeInItems")
    protected List<StoreInItem> storeInItems = new ArrayList<StoreInItem>();

    @DataModelSelection
    protected StoreInItem selectedStoreInItem;

    protected StoreInItem editingItem;

    protected abstract String beginStoreIn();

    protected abstract String storeIn();

    public StoreInItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(StoreInItem editingItem) {
        this.editingItem = editingItem;
    }

    @Override
    protected String storeChange() {

        for (StoreInItem storeInItem : storeInItems) {

            storeResHome.setRes(storeInItem.getRes(), storeInItem.getFormats(), storeInItem.getStoreResCount().getFloatConvertRate());
            StockChangeItem stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
                    storeResHome.getInstance(), storeInItem.getStoreResCount().getMasterCount(), false);
            stockChangeItem.setNoConvertCounts(storeInItem.getStoreResCount().getNoConvertCounts(stockChangeItem));


            if (storeResHome.isIdDefined()) {
                Stock stock = storeResHome.getInstance().getStock();
                if (stock != null) {
                    stockChangeItem.setStock(stock);
                    stockChangeItem.setBefortCount(stockChangeItem.getStock().getCount());
                    stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().add(storeInItem.getStoreResCount().getMasterCount()));
                    stockChangeItem.setAfterCount(stockChangeItem.getStock().getCount());
                }
            }
            if (stockChangeItem.getStock() == null) {
                stockChangeItem.setStock(new Stock(storeResHome.getInstance(), storeInItem.getStoreResCount().getMasterCount()));
                stockChangeItem.setBefortCount(new BigDecimal(0));
                stockChangeItem.setAfterCount(storeInItem.getStoreResCount().getMasterCount());
            }

            if (storeInItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
                for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
                    boolean added = false;
                    for (NoConvertCount stockCount : stockChangeItem.getStock().getNoConvertCounts()) {
                        if (stockCount.getResUnit().equals(noConvertCount.getResUnit())) {
                            stockCount.setCount(stockCount.getCount().add(noConvertCount.getCount()));
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        stockChangeItem.getStock().getNoConvertCounts().add(new NoConvertCount(stockChangeItem.getStock(), noConvertCount.getResUnit(), noConvertCount.getCount()));
                    }
                }
            }

            Batch batch = storeInItem.getBatch();
            if (batch == null) {
                batch = new Batch(UUID.randomUUID().toString().replace("-", ""), storeInItem.getRes(), true, false, true, stockChangeHome.getInstance().getOperDate());
            }
            batch.getStockChangeItems().add(stockChangeItem);
            stockChangeItem.setBatch(batch);

            //stockChangeItem.getStock().getBatchStoreCounts()

            getInstance().getStockChange().getStockChangeItems().add(stockChangeItem);
        }
        persist();
        clearInstance();
        return storeIn();


        //return storeIn();
    }


    @Observer("erp.resLocateSelected")
    public void generateStoreInItem() {
        editingItem = new StoreInItem(storeResFormatFilter.getRes());
    }

    @Override
    public String beginStoreChange() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            stockChangeHome.getInstance().setId("I" + numberBuilder.getDateNumber("storeInCode"));
        }
        return beginStoreIn();
    }


    @Override
    public void removeItem() {
        log.debug("call remove Item :" + selectedStoreInItem.getRes().getName());
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void addItem() {
        if (editingItem == null) {
            throw new IllegalArgumentException("no UNIT type");
        }
        editingItem.setFormats(storeResFormatFilter.getResFormatList());
        for (StoreInItem storeInItem : storeInItems) {
            if (storeInItem.same(editingItem)) {
                storeInItem.merger(editingItem);
                editingItem = null;
                break;
            }
        }

        if (editingItem != null) {
            storeInItems.add(editingItem);
        }
        if (resLocateHome != null)
            resLocateHome.clearInstance();
    }

}
