package com.dgsoft.erp.action.store;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
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
public abstract class StoreInAction {

    @In(create= true)
    private ResLocate resLocate;

    public void locateByCode(){

        switch (resLocate.locateByCode(stockChangeHome.getInstance().getOperType())){

            case NOT_FOUND:
                break;
            case FOUND_STORERES:
                storeResSelected();
                break;
            case FOUND_RES:
                resSelected();
                break;
        }
    }


    @In
    protected FacesMessages facesMessages;

    @In
    protected StockChangeHome stockChangeHome;

    @In
    private EntityManager erpEntityManager;

    protected abstract StockChangeItem getSelectInItem();


    protected StockChangeItem editStockInItem;

    protected List<StockChangeItem> storeChangeItems = new ArrayList<StockChangeItem>();

    public StockChangeItem getEditStockInItem() {
        return editStockInItem;
    }

    public void setEditStockInItem(StockChangeItem editStockInItem) {
        this.editStockInItem = editStockInItem;
    }

    private String addItemLastState = "";

    public String getAddItemLastState() {
        return addItemLastState;
    }

    public void setAddItemLastState(String addItemLastState) {
        this.addItemLastState = addItemLastState;
    }

    public void removeItem() {
        storeChangeItems.remove(getSelectInItem());
    }

    @In(required = false)
    private ResCategoryHome resCategoryHome;

    @In(create = true)
    private ResHome resHome;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private ResHelper resHelper;

    @In
    protected RunParam runParam;

    private boolean selectByCategory = false;

    public void resCategorySelected() {
        editStockInItem = null;
        selectByCategory = true;
        resHome.clearInstance();
        storeResHome.clearInstance();
    }

    public void resSelected() {
        editStockInItem = new StockChangeItem(resHome.getInstance(), resHelper.getFormatHistory(resHome.getInstance()), resHelper.getFloatConvertRateHistory(resHome.getInstance()), resHome.getInstance().getResUnitByInDefault());
        editStockInItem.setStockChange(stockChangeHome.getInstance());
        resCategoryHome.setId(resHome.getInstance().getResCategory().getId());
        selectByCategory = false;
    }

    public void storeResSelected() {
        resHome.setId(storeResHome.getInstance().getRes().getId());
        editStockInItem = new StockChangeItem(storeResHome.getInstance(), resHelper.getFormatHistory(resHome.getInstance()), resHelper.getFloatConvertRateHistory(resHome.getInstance()), resHome.getInstance().getResUnitByInDefault());
        editStockInItem.setStockChange(stockChangeHome.getInstance());
        resCategoryHome.setId(storeResHome.getInstance().getRes().getResCategory().getId());
        selectByCategory = false;
    }

    public void resChange() {
        editStockInItem = new StockChangeItem(resHome.getInstance(), resHelper.getFormatHistory(resHome.getInstance()), resHelper.getFloatConvertRateHistory(resHome.getInstance()), resHome.getInstance().getResUnitByInDefault());
        editStockInItem.setStockChange(stockChangeHome.getInstance());
    }


    private String addNewInItemSuccess() {
        if (selectByCategory) {
            resHome.clearInstance();
            storeResHome.clearInstance();
            editStockInItem = null;
        } else if (storeResHome.isIdDefined()) {
            storeResSelected();
        } else if (resHome.isIdDefined()) {
            resSelected();
        }
        addItemLastState = "added";
        return addItemLastState;
    }

    public String addItem() {
        addItemLastState = "";
        if (editStockInItem == null) {
            throw new IllegalArgumentException("editingItem state error");
        }


        storeResHome.setRes(editStockInItem.getRes(), editStockInItem.getFormats(), editStockInItem.getFloatConvertRate());


        if (!storeResHome.isIdDefined() && DataFormat.isEmpty(editStockInItem.getCode())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                    "newSotreResTypedCodePlase");
            editStockInItem.setCode(resHelper.genStoreResCode(editStockInItem.getRes().getCode(), editStockInItem.getFormats(),
                    (editStockInItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) ?
                            editStockInItem.getFloatConvertRate().toString() : null));
            addItemLastState = "code_not_set";
            return addItemLastState;
        } else {
            if (!storeResHome.isIdDefined()) {

                if (!editStockInItem.getCode().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", editStockInItem.getCode(),
                            runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    addItemLastState = "code_not_rule";
                    return addItemLastState;
                }

                if (!erpEntityManager.createQuery("select storeRes from StoreRes storeRes where code = :code")
                        .setParameter("code", editStockInItem.getCode()).getResultList().isEmpty()) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", editStockInItem.getCode());
                    addItemLastState = "code_exists";
                    return addItemLastState;
                }

                storeResHome.getInstance().setCode(editStockInItem.getCode());

            }
            editStockInItem.setStoreRes(storeResHome.getInstance());

            for (StockChangeItem item : storeChangeItems) {
                if (item.isSameItem(editStockInItem)) {
                    item.add(editStockInItem);
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "StoreChangeItemIsExists", editStockInItem.getStoreRes().getCode());

                    return addNewInItemSuccess();
                }
            }
            storeChangeItems.add(editStockInItem);

            return addNewInItemSuccess();

        }
    }

    //-------------------------------


    protected void storeChange(boolean verify) {


        if (verify) {
            stockChangeHome.resStockChange(storeChangeItems);
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
