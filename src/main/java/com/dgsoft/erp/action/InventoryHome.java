package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.action.store.StoreResFormatFilter;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/15/13
 * Time: 3:32 PM
 */

@Name("inventoryHome")
public class InventoryHome extends ErpEntityHome<Inventory> {

    @Factory(value = "inventoryTypes", scope = ScopeType.CONVERSATION)
    public Inventory.InventoryType[] getInventoryTypes() {
        return Inventory.InventoryType.values();
    }

    @In(create = true)
    protected StoreResFormatFilter storeResFormatFilter;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private ResHelper resHelper;

    @In
    private Credentials credentials;

    @In(required = false)
    private ResHome resHome;

    @In
    private RunParam runParam;

    private StoreResCountInupt storeResCountInupt;

    private StoreResCountInupt itemCheckCount;

    private StockChange loseStockChange;

    private StockChange addStockChange;

    private PrepareStockChange editingItem;

    private String newItemCode = null;

    private boolean checkLose = false;

    private boolean haveStock = false;

    @In
    private FacesMessages facesMessages;

    private String addItemLastState = "";

    public String getAddItemLastState() {
        return addItemLastState;
    }

    public void setAddItemLastState(String addItemLastState) {
        this.addItemLastState = addItemLastState;
    }

    public StoreResCountInupt getItemCheckCount() {
        return itemCheckCount;
    }

    public void setItemCheckCount(StoreResCountInupt itemCheckCount) {
        this.itemCheckCount = itemCheckCount;
    }

    public StoreResCountInupt getStoreResCountInupt() {
        return storeResCountInupt;
    }

    public void setStoreResCountInupt(StoreResCountInupt storeResCountInupt) {
        this.storeResCountInupt = storeResCountInupt;
    }

    public String getNewItemCode() {
        return newItemCode;
    }

    public void setNewItemCode(String newItemCode) {
        this.newItemCode = newItemCode;
    }

    public boolean isCheckLose() {
        return checkLose;
    }

    public void setCheckLose(boolean checkLose) {
        this.checkLose = checkLose;
    }

    public boolean isHaveStock() {
        return haveStock;
    }

    public void setHaveStock(boolean haveStock) {
        this.haveStock = haveStock;
    }

    @Observer(value = "erp.resLocateSelected", create = false)
    public void generateStoreInItemByRes(Res res) {
        editingItem = new PrepareStockChange();
        addItemLastState = "";
        newItemCode = null;
        storeResCountInupt = new StoreResCountInupt(res, res.getResUnitByInDefault());
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateStoreInItemByStoreRes(StoreRes storeRes) {
        editingItem = new PrepareStockChange();
        addItemLastState = "";
        newItemCode = null;
        storeResCountInupt = new StoreResCountInupt(storeRes.getRes(), storeRes.getRes().getResUnitByInDefault());
        if (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            storeResCountInupt.setFloatConvertRate(storeRes.getFloatConversionRate());
        }
        log.debug("generateStoreInItemByStoreRes complete");
    }

    @Override
    public void initInstance() {
        super.initInstance();
        if (isIdDefined()) {
            loseStockChange = getInstance().getStockChangeLoss();
            addStockChange = getInstance().getStockChangeAdd();

            log.debug("id is define---" + addStockChange);
        }
    }

    @Override
    public Inventory createInstance() {
        return new Inventory(false);
    }


    public void beginCheckItem() {

        itemCheckCount = new StoreResCountInupt(storeResHome.getInstance().getRes(), storeResHome.getInstance().getRes().getResUnitByMasterUnit());


        if (storeResHome.getInstance().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            itemCheckCount.setFloatConvertRate(storeResHome.getInstance().getFloatConversionRate());
        }


        Set<PrepareStockChange> findItems = null;

        if (addStockChange != null) {

            for (PrepareStockChange prepareStockChange : addStockChange.getPrepareStockChanges()) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    itemCheckCount.setCount(prepareStockChange.getCount());
                    checkLose = false;
                    break;
                }
            }
        }


        haveStock = false;
        for (Stock stock : getInstance().getStore().getStocks()) {
            if (stock.getStoreRes().equals(storeResHome.getInstance())) {
                haveStock = true;
                break;
            }
        }

        if (!haveStock) {
            checkLose = false;
        } else if ((findItems == null) && (loseStockChange != null)) {
            for (PrepareStockChange prepareStockChange : loseStockChange.getPrepareStockChanges()) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    itemCheckCount.setCount(prepareStockChange.getCount());
                    checkLose = true;
                    break;
                }
            }
        }
        lastState = "";

    }


    private void removePrepareStockChange(StockChange stockChange) {
        for (PrepareStockChange prepareStockChange : stockChange.getPrepareStockChanges()) {
            if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                stockChange.getPrepareStockChanges().remove(prepareStockChange);
                break;
            }
        }
    }

    private Date getStoreChangeDate() {
        if (getInstance().getCheckedDate() != null) {
            return getInstance().getCheckedDate();
        } else if (getInstance().getCheckDate() != null) {
            return getInstance().getCheckDate();
        } else
            return getInstance().getApplyDate();
    }

    public void saveCheckItem() {


        Set<PrepareStockChange> findItems = null;

        if (checkLose) {
            if (addStockChange != null)
                removePrepareStockChange(addStockChange);
            if (loseStockChange != null)
                findItems = loseStockChange.getPrepareStockChanges();

        } else {
            if (loseStockChange != null)
                removePrepareStockChange(loseStockChange);
            if (addStockChange != null)
                findItems = addStockChange.getPrepareStockChanges();
        }

        boolean haveStroeRes = false;

        if (findItems != null) {
            for (PrepareStockChange prepareStockChange : findItems) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    prepareStockChange.setCount(itemCheckCount.getMasterCount());
                    haveStroeRes = true;
                    break;
                }
            }

        }

        if (!haveStroeRes) {


            PrepareStockChange prepareStockChange = new PrepareStockChange();
            StockChange stockChange;
            if (checkLose) {
                if (loseStockChange == null) {
                    stockChange = new StockChange("L" + getInstance().getId(), getInstance().getStore(),
                            getStoreChangeDate(), credentials.getUsername(), StockChange.StoreChangeType.STORE_CHECK_LOSS, null, false);
                    getInstance().setStockChangeLoss(stockChange);
                    loseStockChange = stockChange;
                } else {
                    stockChange = loseStockChange;
                }
                stockChange.setInventoryLoss(getInstance());

            } else {
                if (addStockChange == null) {
                    stockChange = new StockChange("A" + getInstance().getId(), getInstance().getStore(),
                            getStoreChangeDate(), credentials.getUsername(), StockChange.StoreChangeType.STORE_CHECK_ADD, null, false);
                    getInstance().setStockChangeAdd(stockChange);
                    addStockChange = stockChange;
                } else {
                    stockChange = addStockChange;
                }
                stockChange.setInventoryAdd(getInstance());
            }


            prepareStockChange.setStockChange(stockChange);
            prepareStockChange.setStoreRes(storeResHome.getInstance());
            prepareStockChange.setCount(itemCheckCount.getMasterCount());
            stockChange.getPrepareStockChanges().add(prepareStockChange);

        }
        update();
        storeResHome.clearInstance();
        resHome.clearInstance();
    }


    public String addItem() {
        addItemLastState = "";
        if ((editingItem == null)) {
            throw new IllegalArgumentException("editingItem state error");
        }


        storeResHome.setRes(storeResFormatFilter.getRes(), storeResFormatFilter.getResFormatList(), storeResCountInupt.getFloatConvertRate());

        boolean haveStroeRes = false;
        if (addStockChange != null) {
            for (PrepareStockChange prepareStockChange : addStockChange.getPrepareStockChanges()) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    prepareStockChange.setCount(prepareStockChange.getCount().add(storeResCountInupt.getMasterCount()));
                    haveStroeRes = true;
                    break;
                }
            }

        }


        if (!haveStroeRes) {

            if (!storeResHome.isManaged() && (newItemCode == null)) {

                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                        "newSotreResTypedCodePlase");
                newItemCode = resHelper.genStoreResCode(storeResHome.getInstance());

                addItemLastState = "code_not_set";
                return addItemLastState;
            }

            if (!storeResHome.isManaged() && (newItemCode != null) &&
                    (!newItemCode.matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME)))) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                        "storeResCodeNotRule", newItemCode,
                        runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                addItemLastState = "code_not_rule";
                return addItemLastState;
            }

            if (!storeResHome.isManaged() && (newItemCode != null) &&
                    (!getEntityManager().createQuery("select storeRes from StoreRes storeRes where code = :code")
                            .setParameter("code", newItemCode).getResultList().isEmpty())) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                        "storeResCodeExists", newItemCode);

                addItemLastState = "code_exists";
                return addItemLastState;
            }

            if (!storeResHome.isManaged()) {
                storeResHome.getInstance().setCode(newItemCode);
            }

            if (addStockChange == null) {
                addStockChange = new StockChange("A_" + getInstance().getId(),
                        getInstance().getStore(), getStoreChangeDate(),
                        credentials.getUsername(),
                        StockChange.StoreChangeType.STORE_CHECK_ADD, null, false);
                addStockChange.setInventoryAdd(getInstance());
                getInstance().setStockChangeAdd(addStockChange);
                log.debug("instance is add addStockChange");
            }


            editingItem.setCount(storeResCountInupt.getMasterCount());
            editingItem.setStoreRes(storeResHome.getInstance());
            editingItem.setStockChange(addStockChange);
            addStockChange.getPrepareStockChanges().add(editingItem);
        }

        if (loseStockChange != null)
            removePrepareStockChange(loseStockChange);


        update();

        //resHome.clearInstance();
        //storeResHome.clearInstance();
        if (storeResHome.isIdDefined()) {
            storeResFormatFilter.selectedStoreRes(storeResHome.getInstance());
            generateStoreInItemByStoreRes(storeResHome.getInstance());
        } else if (resHome.isIdDefined()) {
            storeResFormatFilter.selectedRes(resHome.getInstance());
            generateStoreInItemByRes(resHome.getInstance());
        }
        addItemLastState = lastState;
        return addItemLastState;
    }


    //定期盘点: 月度盘点 年度大盘
    //不定期盘点:

    //流程 1.盘点计划 (盘点计划联络单, 盘点时间 , 仓库停止作业时间, 帐务冻结时间 , 初盘时间, 复盘时间 ,查核时间 ,人员安排及分工,相关部门配合 注意事项,详细计划) ->

    // 2.初盘 ()

    // 3.复盘()

    //4.查核 ()

    //5.盘点总结及报告()
    //盘点票  原材料库存卡


    //错误原因 物料储位错误 物料标示SKU(库存量单位)错误 物料混装
}
