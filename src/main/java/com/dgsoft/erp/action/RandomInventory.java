package com.dgsoft.erp.action;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResEntity;
import com.dgsoft.erp.tools.StoreResCondition;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 12/6/14.
 */
@Name("randomInventory")
public class RandomInventory {

    @In
    private InventoryHome inventoryHome;

    @In(create = true)
    private StoreResCondition storeResCondition;

    @In(create = true)
    private ResHome resHome;

    private boolean dir;

    private StoreResCount storeResCount;

    private boolean newStoreRes;

    private boolean newStock;

    private InventoryItem editingItem;

    private Map<Stock,InventoryItem> resultMap;

    @In(create = true)
    private FacesMessages facesMessages;

    @In(create = true)
    private ResHelper resHelper;

    private String selectStockId;

    public String getSelectStockId() {
        return selectStockId;
    }

    public void setSelectStockId(String selectStockId) {
        this.selectStockId = selectStockId;
    }

    public void remove(){
        for(Stock stock: getResultMap().keySet()){
            if (stock.getId().equals(selectStockId)){
                inventoryHome.getInstance().getInventoryItems().remove(getResultMap().get(stock));
                inventoryHome.update();
                return;
            }
        }
    }

    public Map<Stock, InventoryItem> getResultMap() {
        if(resultMap == null){
            resultMap = new HashMap<Stock, InventoryItem>();
            for(InventoryItem item: inventoryHome.getInstance().getInventoryItems()){
                resultMap.put(item.getStock(),item);
            }
        }
        return resultMap;
    }


    public void resSelected(){
        saveStatus = null;
        if (resHome.isIdDefined()){
            editingItem = new InventoryItem(resHome.getInstance(),inventoryHome.getInstance());
        }else{
            editingItem = null;
        }
    }


    private void createStock(StoreRes storeRes) {
        Stock stock = new Stock(inventoryHome.getInstance().getStore(), storeRes, BigDecimal.ZERO);
        stock.getInventoryItems().add(editingItem);
        editingItem.setStock(stock);
    }

    private Stock getStockByStoreRes(String storeResId){
        try {
            return inventoryHome.getEntityManager().createQuery("select stock from Stock stock where stock.store.id = :storeId and stock.storeRes.id = :storeResId", Stock.class).
                    setParameter("storeId", inventoryHome.getInstance().getStore().getId()).
                    setParameter("storeResId", storeResId).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    private StoreRes getMatchStoreRes(){
        for (StoreRes storeRes : editingItem.getRes().getStoreReses()) {

            if (resHelper.matchFormat(editingItem.getFormats(), storeRes) &&
                    (!editingItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                            (editingItem.getFloatConvertRate() == null) ||
                            (editingItem.getFloatConvertRate().compareTo(storeRes.getFloatConversionRate()) == 0))) {
                return storeRes;

            }
        }
        return null;
    }

    private String saveStatus;

    public String getSaveStatus() {
        return saveStatus;
    }

    public void saveItem(){
        StoreRes storeRes = getMatchStoreRes();
        if (storeRes == null){
            storeRes = new StoreRes();
            storeRes.setEnable(true);
            storeRes.setRes(editingItem.getRes());
            if (UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(
                    editingItem.getRes().getUnitGroup().getType()))
                storeRes.setFloatConversionRate(editingItem.getFloatConvertRate());

            for (Format format : editingItem.getFormats()) {
                storeRes.getFormats().add(new Format(storeRes, format.getFormatDefine(), format.getFormatValue()));
            }
            storeRes.setCode(resHelper.genStoreResCode(storeRes));
            saveStatus = "newStoreRes";
            createStock(storeRes);
            return;
        }else{
            if (!inventoryHome.getEntityManager().contains(storeRes)){
                switch (resHelper.validStoreResCode(storeRes)){

                    case CODE_NOT_RULE:
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                                "storeResCodeNotRule", editingItem.getStoreRes().getCode(),
                                RunParam.instance().getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                        return;
                    case CODE_EXISTS:
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                                "storeResCodeExists", editingItem.getStoreRes().getCode());
                        return;
                    case CODE_VALID:
                        inventoryHome.getInstance().getInventoryItems().add(editingItem);
                        break;
                }

            }else {
                Stock stock = getStockByStoreRes(storeRes.getId());
                if (stock == null) {
                    createStock(storeRes);
                    inventoryHome.getInstance().getInventoryItems().add(editingItem);
                } else {
                    InventoryItem old = getResultMap().get(stock);
                    if (old == null) {
                        old.setChangeCount(editingItem.getChangeCount());
                        old.setChangeType(editingItem.getChangeType());
                    } else {
                        inventoryHome.getInstance().getInventoryItems().add(editingItem);
                    }
                }
            }
        }
        editingItem.setBeforCount(editingItem.getStock().getCount());
        editingItem.setLastCount(BigDecimal.ZERO);
        saveStatus = null;
        inventoryHome.update();
        resultMap = null;
    }

}
