package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.StoreInItem;
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

    @In(required = false)
    protected StoreResFormatFilter storeResFormatFilter;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private Credentials credentials;

    @In
    private NumberBuilder numberBuilder;

    @In(required = false)
    private ResHome resHome;

    @In
    private RunParam runParam;

    private StoreResCountInupt storeResCountInupt;

    private StockChange loseStockChange;

    private StockChange addStockChange;

    private PrepareStockChange editingItem;

    private String newItemCode = null;

    private boolean addStock;

    @In
    private FacesMessages facesMessages;

    private String addItemLastState = "";

    public String getAddItemLastState() {
        return addItemLastState;
    }

    public void setAddItemLastState(String addItemLastState) {
        this.addItemLastState = addItemLastState;
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

    public boolean isAddStock() {
        return addStock;
    }

    public void setAddStock(boolean addStock) {
        this.addStock = addStock;
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
    public Inventory createInstance(){
        return new Inventory(false);
    }

    public void checkLose() {
        addStock = false;
        beginCheckItem();
    }

    public void checkAdd() {
        addStock = true;
        beginCheckItem();
    }

    private void beginCheckItem() {

        storeResCountInupt = new StoreResCountInupt(storeResHome.getInstance().getRes(),storeResHome.getInstance().getRes().getResUnitByMasterUnit());


        if (storeResHome.getInstance().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            storeResCountInupt.setFloatConvertRate(storeResHome.getInstance().getFloatConversionRate());
        }


        Set<PrepareStockChange> findItems = null;

        if (addStock) {
            if (addStockChange != null)
                findItems = addStockChange.getPrepareStockChanges();
        } else {
            if (loseStockChange != null)
                findItems = loseStockChange.getPrepareStockChanges();
        }


        if (findItems != null) {
            for (PrepareStockChange prepareStockChange : findItems) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    storeResCountInupt.setCount(prepareStockChange.getCount());

                    break;
                }
            }

        }
    }


    public void saveCheckItem() {
        boolean haveStroeRes = false;

        Set<PrepareStockChange> findItems = null;

        if (addStock) {
            if (addStockChange != null)
                findItems = addStockChange.getPrepareStockChanges();
        } else {
            if (loseStockChange != null)
                findItems = loseStockChange.getPrepareStockChanges();
        }


        if (findItems != null) {
            for (PrepareStockChange prepareStockChange : findItems) {
                if (prepareStockChange.getStoreRes().equals(storeResHome.getInstance())) {

                    prepareStockChange.setCount(storeResCountInupt.getMasterCount());
                    haveStroeRes = true;
                    break;
                }
            }

        }

        if (!haveStroeRes){


            PrepareStockChange prepareStockChange = new PrepareStockChange();
            StockChange stockChange;
            if (!addStock){
                if (loseStockChange == null){
                    stockChange = new StockChange("L" + getInstance().getId(), getInstance().getStore(),
                            new Date(),credentials.getUsername(),StockChange.StoreChangeType.STORE_CHECK_LOSS,null);
                    stockChange.setInventoryLoss(getInstance());
                    getInstance().setStockChangeLoss(stockChange);
                    loseStockChange = stockChange;
                }else{
                    stockChange = loseStockChange;
                }
                stockChange.setInventoryLoss(getInstance());

            }else{
                if (addStockChange == null){
                    stockChange = new StockChange("A" + getInstance().getId(),getInstance().getStore(),
                            new Date(),credentials.getUsername(),StockChange.StoreChangeType.STORE_CHECK_ADD,null);
                    stockChange.setInventoryAdd(getInstance());
                    getInstance().setStockChangeAdd(stockChange);
                    addStockChange = stockChange;
                }else{
                    stockChange = addStockChange;
                }
                stockChange.setInventoryAdd(getInstance());
            }


            prepareStockChange.setStockChange(stockChange);
            prepareStockChange.setStoreRes(storeResHome.getInstance());
            prepareStockChange.setCount(storeResCountInupt.getMasterCount());
            stockChange.getPrepareStockChanges().add(prepareStockChange);

        }
        update();
        storeResHome.clearInstance();

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
                newItemCode = storeResFormatFilter.getRes().getCode() + "-" +
                        numberBuilder.getNumber("erp.storeResCode." + storeResFormatFilter.getRes().getCode());
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

            if (!storeResHome.isManaged()){
                storeResHome.getInstance().setCode(newItemCode);
            }

            if (addStockChange == null) {
                addStockChange = new StockChange("A_" + getInstance().getId(),
                        getInstance().getStore(), new Date(),
                        credentials.getUsername(),
                        StockChange.StoreChangeType.STORE_CHECK_ADD, null);
                addStockChange.setInventoryAdd(getInstance());
                getInstance().setStockChangeAdd(addStockChange);
                log.debug("instance is add addStockChange");
            }


            editingItem.setCount(storeResCountInupt.getMasterCount());
            editingItem.setStoreRes(storeResHome.getInstance());
            editingItem.setStockChange(addStockChange);
            addStockChange.getPrepareStockChanges().add(editingItem);
        }
        addItemLastState = update();

        resHome.clearInstance();
        storeResHome.clearInstance();
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
