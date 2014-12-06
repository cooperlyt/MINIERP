package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResEntity;
import com.dgsoft.erp.tools.StoreResCondition;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 12/6/14.
 */
@Name("randomInventory")
@Scope(ScopeType.CONVERSATION)
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

    private Map<Stock, InventoryItem> resultMap;

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

    public InventoryItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(InventoryItem editingItem) {
        this.editingItem = editingItem;
    }


    @org.jboss.seam.annotations.Factory("inventoryChangeTypes")
    public Set<InventoryItem.InventoryItemChangeType> getInventoryChangeTypes() {
        return EnumSet.of(InventoryItem.InventoryItemChangeType.INVENTORY_ADD, InventoryItem.InventoryItemChangeType.INVENTORY_LOSS);
    }

    public void remove() {
        for (Stock stock : getResultMap().keySet()) {
            if (stock.getId().equals(selectStockId)) {
                inventoryHome.getInstance().getInventoryItems().remove(getResultMap().get(stock));

                inventoryHome.update();
                resultMap = null;
                return;
            }
        }
    }

    public Map<Stock, InventoryItem> getResultMap() {
        if (resultMap == null) {
            resultMap = new HashMap<Stock, InventoryItem>();
            for (InventoryItem item : inventoryHome.getInstance().getInventoryItems()) {
                resultMap.put(item.getStock(), item);
            }
        }
        return resultMap;
    }


    public static class InventoryTotalData implements TotalDataGroup.GroupTotalData {

        private ResCount stockCount;

        private ResCount inventoryAddCount;

        private ResCount inventoryLossCount;

        public ResCount getStockCount() {
            return stockCount;
        }

        public ResCount getInventoryAddCount() {
            return inventoryAddCount;
        }

        public ResCount getInventoryLossCount() {
            return inventoryLossCount;
        }

        public void put(InventoryItem item) {
            if (stockCount == null) {
                stockCount = item.getStock().getStoreResCount();
            } else {
                stockCount = stockCount.add(item.getStock().getStoreResCount());
            }

            if (InventoryItem.InventoryItemChangeType.INVENTORY_ADD.equals(item.getChangeType())) {
                if (inventoryAddCount == null) {
                    inventoryAddCount = item.getStoreResCount();
                } else {
                    inventoryAddCount = inventoryAddCount.add(item.getStoreResCount());
                }
            } else {
                if (inventoryLossCount == null) {
                    inventoryLossCount = item.getStoreResCount();
                } else {
                    inventoryLossCount = inventoryLossCount.add(item.getStoreResCount());
                }
            }

        }
    }

    private static InventoryTotalData totalAll(Collection<InventoryItem> items) {
        InventoryTotalData result = new InventoryTotalData();
        for (InventoryItem item : items) {
            result.put(item);
        }
        return result;
    }

    public List<TotalDataGroup<Res, InventoryItem, InventoryTotalData>> getResultGroup() {
        return TotalDataGroup.groupBy(getResultMap().values(),
                new TotalGroupStrategy<Res, InventoryItem, InventoryTotalData>() {
                    @Override
                    public Res getKey(InventoryItem item) {
                        return item.getStoreRes().getRes();
                    }

                    @Override
                    public InventoryTotalData totalGroupData(Collection<InventoryItem> datas) {
                        return totalAll(datas);
                    }
                }, new TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, InventoryItem, InventoryTotalData>() {
                    @Override
                    public ResFormatGroupStrategy.StoreResFormatKey getKey(InventoryItem item) {
                        return new ResFormatGroupStrategy.StoreResFormatKey(item.getStoreRes());
                    }

                    @Override
                    public InventoryTotalData totalGroupData(Collection<InventoryItem> datas) {
                        return totalAll(datas);
                    }
                });
    }


    public void resSelected() {
        saveStatus = null;
        if (resHome.isIdDefined()) {
            editingItem = new InventoryItem(resHome.getInstance(), inventoryHome.getInstance());
        } else {
            editingItem = null;
        }
    }


    private void createStock(StoreRes storeRes) {
        Stock stock = new Stock(inventoryHome.getInstance().getStore(), storeRes, BigDecimal.ZERO);
        stock.getInventoryItems().add(editingItem);
        editingItem.setStock(stock);
    }

    private Stock getStockByStoreRes(String storeResId) {
        try {
            return inventoryHome.getEntityManager().createQuery("select stock from Stock stock where stock.store.id = :storeId and stock.storeRes.id = :storeResId", Stock.class).
                    setParameter("storeId", inventoryHome.getInstance().getStore().getId()).
                    setParameter("storeResId", storeResId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private StoreRes getMatchStoreRes() {
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

    public void saveItem() {
        if (editingItem.getStoreRes() != null) {
            switch (resHelper.validStoreResCode(editingItem.getStoreRes())) {

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
            StoreRes storeRes = getMatchStoreRes();
            if (storeRes == null) {
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
            } else {

                Stock stock = getStockByStoreRes(storeRes.getId());
                if (stock == null) {
                    createStock(storeRes);
                    inventoryHome.getInstance().getInventoryItems().add(editingItem);
                } else {
                    InventoryItem old = getResultMap().get(stock);
                    if (old != null) {
                        old.setChangeCount(editingItem.getChangeCount());
                        old.setChangeType(editingItem.getChangeType());
                        editingItem = old;
                    } else {
                        editingItem.setStock(stock);
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
        resHome.clearInstance();
        resSelected();
    }

}
