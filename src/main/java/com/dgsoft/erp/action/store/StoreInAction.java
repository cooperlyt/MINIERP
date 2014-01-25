package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;

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

    @In
    private ResHelper resHelper;

    @In(create = true)
    protected StoreResFormatFilter storeResFormatFilter;

    //@In(required = false)
    // protected ResLocate resLocate;

    @In(create = true)
    protected StoreResHome storeResHome;

    @In(create = true)
    protected ResHome resHome;

    protected List<StoreInItem> storeInItems = new ArrayList<StoreInItem>();

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
    protected String storeChange(boolean verify) {

        boolean haveItem = false;
        for (StoreInItem storeInItem : storeInItems) {

            if (storeInItem.getStoreResCountInupt().getMasterCount().compareTo(BigDecimal.ZERO) == 0) {

                continue;
            }

            if (storeInItem.getStoreRes() == null) {
                storeResHome.setRes(storeInItem.getRes(), storeInItem.getFormats(), storeInItem.getStoreResCountInupt().getFloatConvertRate());
            } else {
                storeResHome.setId(storeInItem.getStoreRes().getId());
            }

            if (!storeResHome.isIdDefined()) {
                storeResHome.getInstance().setCode(storeInItem.getStoreResCode());
            }

            if (verify) {
                storeInNow(storeInItem);
            } else {
                prepareStoreIn(storeInItem);
            }


            haveItem = true;
        }
        if (!haveItem) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeInNotItem");
            return null;
        }
        getInstance().setStockChange(stockChangeHome.getInstance());
        persist();
        clearInstance();
        return storeIn();
    }

    private void prepareStoreIn(StoreInItem storeInItem) {
        PrepareStockChange prepareStockChange = new PrepareStockChange(stockChangeHome.getInstance(),
                storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount());
        prepareStockChange.setNoConvertCounts(storeInItem.getStoreResCountInupt().getNoConvertCounts());
        for (NoConvertCount noConvertCount : prepareStockChange.getNoConvertCounts()) {
            noConvertCount.setPrepareStockChange(prepareStockChange);
        }
        stockChangeHome.getInstance().getPrepareStockChanges().add(prepareStockChange);

    }

    private void storeInNow(StoreInItem storeInItem) {
        StockChangeItem stockChangeItem = new StockChangeItem(stockChangeHome.getInstance(),
                storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount(), false);
        stockChangeItem.setNoConvertCounts(storeInItem.getStoreResCountInupt().getNoConvertCounts());
        for (NoConvertCount noConvertCount : stockChangeItem.getNoConvertCounts()) {
            noConvertCount.setStockChangeItem(stockChangeItem);
        }

        if (storeResHome.isIdDefined()) {
            Stock stock = storeResHome.getStock(stockChangeHome.getInstance().getStore());
            if (stock != null) {
                stockChangeItem.setStock(stock);
                stockChangeItem.setBefortCount(stockChangeItem.getStock().getCount());
                stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().add(storeInItem.getStoreResCountInupt().getMasterCount()));
                stockChangeItem.setAfterCount(stockChangeItem.getStock().getCount());
            }
        }
        if (stockChangeItem.getStock() == null) {
            stockChangeItem.setStock(new Stock(storeResHome.getInstance(), storeInItem.getStoreResCountInupt().getMasterCount()));
            stockChangeItem.setBefortCount(new BigDecimal(0));
            stockChangeItem.setAfterCount(storeInItem.getStoreResCountInupt().getMasterCount());
        }
        stockChangeItem.getStock().setStore(stockChangeHome.getInstance().getStore());

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
            for (BatchStoreCount batchStoreCount : stockChangeItem.getStock().getBatchStoreCounts()) {
                if (batchStoreCount.getBatch().isDefaultBatch()) {
                    batch = batchStoreCount.getBatch();
                    break;
                }
            }

        }
        if (batch == null) {
            batch = new Batch(UUID.randomUUID().toString().replace("-", ""), storeInItem.getRes(), true, false, true, stockChangeHome.getInstance().getOperDate());
        }

        if (batch.getBatchStoreCount() == null) {
            batch.setBatchStoreCount(new BatchStoreCount(batch, stockChangeItem.getStock(), storeInItem.getStoreResCountInupt().getMasterCount()));
            batch.getBatchStoreCount().setBatch(batch);
            stockChangeItem.getStock().getBatchStoreCounts().add(batch.getBatchStoreCount());
        } else {
            batch.getBatchStoreCount().setCount(batch.getBatchStoreCount().getCount().add(storeInItem.getStoreResCountInupt().getMasterCount()));
        }

        batch.getStockChangeItems().add(stockChangeItem);
        stockChangeItem.setBatch(batch);


        //stockChangeItem.getStock().getBatchStoreCounts()

        stockChangeHome.getInstance().getStockChangeItems().add(stockChangeItem);
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void generateStoreInItemByRes(Res res) {
        editingItem = new StoreInItem(res);
        addItemLastState = "";
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {
        editingItem = new StoreInItem(storeRes.getRes(), storeRes.getFloatConversionRate());
        addItemLastState = "";
    }

    @Override
    public String beginStoreChange() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            stockChangeHome.getInstance().setId("I" + numberBuilder.getDateNumber("storeInCode"));
        }
        return beginStoreIn();
    }


    private String addItemLastState = "";

    public String getAddItemLastState() {
        return addItemLastState;
    }

    public void setAddItemLastState(String addItemLastState) {
        this.addItemLastState = addItemLastState;
    }

    @Override
    public String addItem() {
        addItemLastState = "";
        if (editingItem == null) {
            throw new IllegalArgumentException("editingItem state error");
        }


        editingItem.setFormats(storeResFormatFilter.getResFormatList());
        for (StoreInItem storeInItem : storeInItems) {
            if (storeInItem.same(editingItem)) {
                storeInItem.merger(editingItem);
                editingItem = null;
                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "");
                break;
            }
        }

        if (editingItem != null) {

            if ((editingItem.getStoreRes() == null)) {
                if ((editingItem.getStoreResCode() == null) || "".equals(editingItem.getStoreResCode().trim())) {
                    storeResHome.setRes(editingItem.getRes(),
                            storeResFormatFilter.getResFormatList(), editingItem.getStoreResCountInupt().getFloatConvertRate());
                    if (!storeResHome.isIdDefined()) {
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                                "newSotreResTypedCodePlase");
                        editingItem.setStoreResCode(resHelper.genStoreResCode(editingItem.getRes().getCode(), editingItem.getFormats(),
                                (editingItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) ?
                                        editingItem.getStoreResCountInupt().getFloatConvertRate().toString() : null));
                        addItemLastState = "code_not_set";
                        return addItemLastState;
                    } else {
                        editingItem.setStoreRes(storeResHome.getInstance());
                    }

                } else if (!editingItem.getStoreResCode().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", editingItem.getStoreResCode(),
                            runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    addItemLastState = "code_not_rule";
                    return addItemLastState;
                }

                if (!getEntityManager().createQuery("select storeRes from StoreRes storeRes where code = :code")
                        .setParameter("code", editingItem.getStoreResCode()).getResultList().isEmpty()) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", editingItem.getStoreResCode());
                    addItemLastState = "code_exists";
                    return addItemLastState;
                }
            }


            storeInItems.add(editingItem);
        }
        editingItem = null;
        //resHome.clearInstance();
        //storeResHome.clearInstance();
        if (storeResHome.isIdDefined()) {
            storeResFormatFilter.selectedStoreRes(storeResHome.getInstance());
            generateStoreInItemByStoreRes(storeResHome.getInstance());
        } else if (resHome.isIdDefined()) {
            storeResFormatFilter.selectedRes(resHome.getInstance());
            generateStoreInItemByRes(resHome.getInstance());
        }


        addItemLastState = "added";
        return addItemLastState;
    }

}
