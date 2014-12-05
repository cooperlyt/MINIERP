package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.*;
import com.dgsoft.erp.tools.StoreResCondition;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 11/12/14.
 */
@Name("inventoryItems")
@Scope(ScopeType.CONVERSATION)
public class InventoryItems {

    @In
    private InventoryHome inventoryHome;

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    @In(create = true)
    private StoreResCondition storeResCondition;

    @In(create = true)
    private EntityQuery<Store> allStoreList;

    @In(create = true)
    private FacesMessages facesMessages;

    private List<InventoryItem> items;

    private Map<StoreRes, SeasonStockChangeData> seasonStockChangeDatas;

    private boolean showInOutCount = false;

    private boolean hideZero= false;

    @In(create = true)
    private ResCode resCode;

    private SeasonStockChangeData editingItem;

    private boolean newStoreRes;

    private boolean newStock;

    private ResHelper.CodeValid saveStatus;

    public boolean isHideZero() {
        return hideZero;
    }

    public void setHideZero(boolean hideZero) {
        this.hideZero = hideZero;
    }

    public boolean isShowInOutCount() {
        return showInOutCount;
    }

    public void setShowInOutCount(boolean showInOutCount) {
        this.showInOutCount = showInOutCount;
    }

    public ResHelper.CodeValid getSaveStatus() {
        return saveStatus;
    }

    public SeasonStockChangeData getEditingItem() {
        return editingItem;
    }

    public boolean isNewStoreRes() {
        return newStoreRes;
    }

    public void setEditingItemId(String id) {
        if ((id == null) || "".equals(id)) {
            editingItem = null;
            return;
        }
        for (SeasonStockChangeData item : getSeasonStockChangeDatas().values()) {
            if (item.getInventoryItem().getId().equals(id)) {
                editingItem = item;
                return;
            }
        }
        editingItem = null;
    }

    public String getEditingItemId() {
        if (editingItem == null) {
            return null;
        }
        return editingItem.getInventoryItem().getId();
    }

    private SeasonStockChangeData createNewItem(StoreRes storeRes) {
        newStock = true;
        facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "AddNewStockWarn");
        Stock stock = new Stock(inventoryHome.getInstance().getStore(), storeRes, BigDecimal.ZERO);
        InventoryItem item = new InventoryItem(BigDecimal.ZERO, BigDecimal.ZERO, inventoryHome.getInstance(), stock);
        stock.getInventoryItems().add(item);
        return new SeasonStockChangeData(item);
    }

    public void beginChangeByCode() {
        resCode.find();
        if (ResCode.CodeStatus.STORERES_DEFINED.equals(resCode.getCodeStatus())) {
            editingItem = getSeasonStockChangeDatas().get(resCode.getStoreRes());
            if(editingItem == null){
                editingItem = createNewItem(resCode.getStoreRes());
            }
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, resCode.getCodeStatus().name());
        }
    }

    public void beginChangeByItem() {
        saveStatus = null;
        newStoreRes = false;
        newStock = false;
    }

    public void beginChange() {
        saveStatus = null;
        if (!storeResCondition.isFullStoreResSearch()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "InvertoryChangeFullStoreResError");
            editingItem = null;
            return;
        }
        List<StoreRes> storeReses = storeResCondition.getMatchStoreReses();
        newStoreRes = storeReses.isEmpty();
        if (newStoreRes) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "AddNewStoreResWarn");
            editingItem = createNewItem(storeResCondition.createStoreResByCondition());
        } else {
            editingItem = getSeasonStockChangeDatas().get(storeReses.get(0));
            if (editingItem == null) {
                editingItem = createNewItem(storeReses.get(0));
            } else {
                newStock = false;
            }
        }
    }

    @Transactional
    public void saveChange() {
        if (newStoreRes) {
            for (InventoryItem item : getItems()) {
                if (item.getStock().getStoreRes().getCode().equals(editingItem.getStoreRes())) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", editingItem.getStoreRes().getCode());
                    saveStatus = ResHelper.CodeValid.CODE_EXISTS;
                    return;
                }
            }

            saveStatus = ResHelper.instance().validStoreResCode(editingItem.getStoreRes());
            switch (saveStatus) {

                case CODE_NOT_RULE:
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", editingItem.getStoreRes().getCode(),
                            RunParam.instance().getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    return;
                case CODE_EXISTS:
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", editingItem.getStoreRes().getCode());
                    return;
            }


        }
        if (newStock) {

            inventoryHome.getInstance().getInventoryItems().add(editingItem.getInventoryItem());
            items.add(editingItem.getInventoryItem());
            seasonStockChangeDatas.put(editingItem.getStoreRes(), editingItem);
            refresh();
        }
        inventoryHome.update();

        saveStatus = ResHelper.CodeValid.CODE_VALID;

    }


    private void initItems() {
        if (items == null) {
            items = erpEntityLoader.getEntityManager().createQuery("select item from InventoryItem item left join fetch item.stock stock left join fetch stock.store left join fetch stock.storeRes storeRes left join fetch storeRes.res res left join fetch res.unitGroup  where item.inventory.id = :inventoryId", InventoryItem.class).
                    setParameter("inventoryId", inventoryHome.getId()).getResultList();
            Logging.getLog(getClass()).debug("init Items count:" + items.size());
        }
    }

    private List<StockChange.StoreChangeType> storeInTypes;

    private List<StockChange.StoreChangeType> storeOutTypes;

    private List<Store> allocationOutStores;

    public StockChange.StoreChangeType getFirstInType() {
        if (getStoreInTypes().isEmpty()) {
            return null;
        } else {
            return getStoreInTypes().get(0);
        }
    }

    public StockChange.StoreChangeType getFirstOutType() {
        if (getStoreOutTypes().isEmpty()) {
            return null;
        } else {
            return getStoreOutTypes().get(0);
        }
    }

    public boolean isFirstStore(String storeId) {
        if (getAllocationOutStores().isEmpty()) {
            return false;
        }
        return getAllocationOutStores().get(0).getId().equals(storeId);
    }

    public List<StockChange.StoreChangeType> getStoreInTypes() {
        if (storeInTypes == null) {
            Set<StockChange.StoreChangeType> result = new HashSet<StockChange.StoreChangeType>();
            for (StockChange.StoreChangeType type : StockChange.StoreChangeType.getAllIn()) {
                for (SeasonStockChangeData data : getSeasonStockChangeDatas().values()) {
                    if (data.typeInChange(type)) {
                        result.add(type);
                        break;
                    }
                }
            }
            storeInTypes = new ArrayList<StockChange.StoreChangeType>(result);
            Collections.sort(storeInTypes);
        }
        return storeInTypes;
    }

    public List<StockChange.StoreChangeType> getStoreOutTypes() {
        if (storeOutTypes == null) {

            Set<StockChange.StoreChangeType> result = new HashSet<StockChange.StoreChangeType>();
            for (StockChange.StoreChangeType type : StockChange.StoreChangeType.getAllOut()) {
                if (!StockChange.StoreChangeType.ALLOCATION_OUT.equals(type))
                    for (SeasonStockChangeData data : getSeasonStockChangeDatas().values()) {
                        if (data.typeInChange(type)) {
                            result.add(type);
                            break;
                        }
                    }
            }
            storeOutTypes = new ArrayList<StockChange.StoreChangeType>(result);
            Collections.sort(storeOutTypes);
        }
        return storeOutTypes;
    }

    public List<Store> getAllocationOutStores() {
        if (allocationOutStores == null) {
            allocationOutStores = new ArrayList<Store>();
            for (Store store : allStoreList.getResultList()) {
                for (SeasonStockChangeData data : getSeasonStockChangeDatas().values()) {
                    if (data.storeInChange(store.getId())) {
                        allocationOutStores.add(store);
                        break;
                    }
                }
            }
            Collections.sort(allocationOutStores);
        }
        return allocationOutStores;
    }


    public List<InventoryItem> getItems() {
        initItems();
        return items;
    }

    protected Map<StoreRes, SeasonStockChangeData> getSeasonStockChangeDatas() {
        initSeasonStockChangeDatas();
        return seasonStockChangeDatas;
    }

    private  List<TotalDataGroup<Res, SeasonStockChangeData, SeasonStockChangeTotalData>> resultGroup;

    public void refresh(){
        resultGroup = null;
    }

    public void reset(){
        storeResCondition.reset();
        refresh();
    }


    public List<TotalDataGroup<Res, SeasonStockChangeData, SeasonStockChangeTotalData>> getResultGroup() {

        if (resultGroup == null) {

            List<SeasonStockChangeData> values = new ArrayList<SeasonStockChangeData>();
            for (SeasonStockChangeData value : getSeasonStockChangeDatas().values()) {
                if (storeResCondition.isMatchStoreRes(value.getStoreRes()) &&
                        ((value.getLastCount().getMasterCount().compareTo(BigDecimal.ZERO) != 0) ||
                                (value.getResultCount().getMasterCount().compareTo(BigDecimal.ZERO) != 0)||
                                (value.getBeginCount().getMasterCount().compareTo(BigDecimal.ZERO) != 0) ||
                                (!value.getChangeEntrySet().isEmpty()))) {
                    if (!hideZero || ((value.getLastCount().getMasterCount().compareTo(BigDecimal.ZERO) != 0) ||
                            (value.getResultCount().getMasterCount().compareTo(BigDecimal.ZERO) != 0)))
                        values.add(value);
                }
            }

            resultGroup = TotalDataGroup.groupBy(values, new TotalGroupStrategy<Res, SeasonStockChangeData, SeasonStockChangeTotalData>() {
                @Override
                public Res getKey(SeasonStockChangeData seasonStockChangeData) {
                    return seasonStockChangeData.getStoreRes().getRes();
                }

                @Override
                public SeasonStockChangeTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                    return totalData(datas);
                }
            }, new TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, SeasonStockChangeData, SeasonStockChangeTotalData>() {
                @Override
                public ResFormatGroupStrategy.StoreResFormatKey getKey(SeasonStockChangeData seasonStockChangeData) {
                    return new ResFormatGroupStrategy.StoreResFormatKey(seasonStockChangeData.getStoreRes());
                }

                @Override
                public SeasonStockChangeTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                    return totalData(datas);
                }
            });
        }

         for(TotalDataGroup<Res, SeasonStockChangeData, SeasonStockChangeTotalData> data: resultGroup){
             TotalDataGroup.sort(data,new Comparator<SeasonStockChangeData>() {
                 @Override
                 public int compare(SeasonStockChangeData o1, SeasonStockChangeData o2) {
                     return o1.getStoreRes().compareTo(o2.getStoreRes());
                 }
             });
         }

        return resultGroup;
    }


    private void initSeasonStockChangeDatas() {
        if (seasonStockChangeDatas == null) {

            List<StockChangeGroup> datas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockChangeGroup(item.storeRes.id,item.stockChange.operType,sum(item.count)) from StockChangeItem item where item.stockChange.operDate > :beginDate and item.stockChange.operDate <= :endDate and item.stockChange.store.id = :storeId and ( ((item.stockChange.operType <> 'STORE_CHECK_ADD') and (item.stockChange.operType <> 'STORE_CHECK_LOSS')) or (item.stockChange.inventory.id <> :thisInventoryId) ) group by item.storeRes.id,item.stockChange.operType", StockChangeGroup.class).
                    setParameter("beginDate", inventoryHome.getBeforInventoryDate()).setParameter("endDate", inventoryHome.getInstance().getCheckDate()).
                    setParameter("storeId", inventoryHome.getInstance().getStore().getId()).setParameter("thisInventoryId", inventoryHome.getInstance().getId()).getResultList();
            Map<String, Map<StockChange.StoreChangeType, BigDecimal>> dataMap = new HashMap<String, Map<StockChange.StoreChangeType, BigDecimal>>();

            for (StockChangeGroup data : datas) {
                Map<StockChange.StoreChangeType, BigDecimal> countMap = dataMap.get(data.getStoreResId());
                if (countMap == null) {
                    countMap = new HashMap<StockChange.StoreChangeType, BigDecimal>();
                    dataMap.put(data.getStoreResId(), countMap);
                }
                countMap.put(data.getType(), data.getMastCount());
            }


            List<AllocationOutGroup> aoDatas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.AllocationOutGroup(item.storeRes.id,item.stockChange.allocation.inStore,sum(item.count)) from StockChangeItem item where item.stockChange.operType = 'ALLOCATION_OUT' and item.stockChange.operDate > :beginDate and item.stockChange.operDate <= :endDate and item.stockChange.allocation.outStore.id = :storeId group by item.storeRes.id,item.stockChange.allocation.inStore", AllocationOutGroup.class).
                    setParameter("beginDate", inventoryHome.getBeforInventoryDate()).setParameter("endDate", inventoryHome.getInstance().getCheckDate()).
                    setParameter("storeId", inventoryHome.getInstance().getStore().getId()).getResultList();
            Map<String, AllocationOutGroup> aoDataMap = new HashMap<String, AllocationOutGroup>();
            for (AllocationOutGroup ao : aoDatas) {
                aoDataMap.put(ao.getStoreResId(), ao);
            }


            seasonStockChangeDatas = new HashMap<StoreRes, SeasonStockChangeData>();
            for (InventoryItem item : getItems()) {
                SeasonStockChangeData newGroupData = new SeasonStockChangeData(item);
                seasonStockChangeDatas.put(item.getStock().getStoreRes(), newGroupData);
                Map<StockChange.StoreChangeType, BigDecimal> data = dataMap.get(newGroupData.getStoreRes().getId());
                if (data != null) {
                    for (Map.Entry<StockChange.StoreChangeType, BigDecimal> entry : data.entrySet()) {
                        newGroupData.putChange(entry.getKey(), new StoreResCount(item.getStock().getStoreRes(), entry.getValue()));
                    }
                }
                AllocationOutGroup aoGroup = aoDataMap.get(newGroupData.getStoreRes().getId());
                if (aoGroup != null) {
                    newGroupData.putAllocationOutChange(aoGroup.getStore(), new StoreResCount(item.getStock().getStoreRes(), aoGroup.getMastCount()));
                }
            }

            Logging.getLog(getClass()).debug("init SeasonStockChangeDatas count:" + seasonStockChangeDatas.size());
        }
    }


    private static SeasonStockChangeTotalData totalData(Collection<SeasonStockChangeData> datas) {
        SeasonStockChangeTotalData result = null;

        for (SeasonStockChangeData data : datas) {
            if (result == null) {
                result = new SeasonStockChangeTotalData(data.getRes(), data);

            }
            result.putAddCount(data.getAddCount());
            result.putBeginCount(data.getBeginCount());
            result.putLossCount(data.getLossCount());
            for (Map.Entry<StockChange.StoreChangeType, ResCount> entry : data.getChangeEntrySet()) {
                result.putChange(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<Store, ResCount> entry : data.getAllocationOutEntrySet()) {
                result.putAllocationOutChange(entry.getKey(), entry.getValue());
            }

        }
        return result;
    }


    public interface SeasonStockChangeDataBase {
        public ResCount getBeginCount();

        public ResCount getAddCount();

        public ResCount getLossCount();

        public Set<Map.Entry<StockChange.StoreChangeType, ResCount>> getChangeEntrySet();

        public Set<Map.Entry<Store, ResCount>> getAllocationOutEntrySet();

        public ResCount getChangeCountByType(StockChange.StoreChangeType type);

        public ResCount getChangeCountByType(String type);

        public ResCount getAllocationOutCountByStoreId(String storeId);

        public boolean typeInChange(StockChange.StoreChangeType type);

        public boolean storeInChange(String storeId);

        public ResCount getInCount();

        public ResCount getOutCount();

        public ResCount getLastCount();

        public ResCount getResultCount();

        public void putAllocationOutChange(Store store, ResCount changeCount);

        public void putChange(StockChange.StoreChangeType type, ResCount changeCount);

    }

    public static abstract class SeasonStockChangeDataCalc implements SeasonStockChangeDataBase {

        public abstract ResCount getBeginCount();

        public abstract ResCount getAddCount();

        public abstract ResCount getLossCount();

        public abstract Res getRes();

        private Map<StockChange.StoreChangeType, ResCount> changeCounts;

        private Map<Store, ResCount> storeAllocationOutCounts;

        private Map<String, Store> storesMap;

        protected SeasonStockChangeDataCalc() {
            changeCounts = new HashMap<StockChange.StoreChangeType, ResCount>();
            storeAllocationOutCounts = new HashMap<Store, ResCount>();
            storesMap = new HashMap<String, Store>();
        }


        public List<Map.Entry<StockChange.StoreChangeType, ResCount>> getChangeEntrySetByTypes(EnumSet<StockChange.StoreChangeType> types) {
            List<Map.Entry<StockChange.StoreChangeType, ResCount>> result =
                    new ArrayList<Map.Entry<StockChange.StoreChangeType, ResCount>>();

            for (Map.Entry<StockChange.StoreChangeType, ResCount> item : getChangeEntrySet()) {
                if (types.contains(item.getKey())) {
                    result.add(item);
                }
            }

            Collections.sort(result, new Comparator<Map.Entry<StockChange.StoreChangeType, ResCount>>() {
                @Override
                public int compare(Map.Entry<StockChange.StoreChangeType, ResCount> o1, Map.Entry<StockChange.StoreChangeType, ResCount> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            return result;
        }

        public Set<Map.Entry<StockChange.StoreChangeType, ResCount>> getChangeEntrySet() {
            return changeCounts.entrySet();
        }

        public Set<Map.Entry<Store, ResCount>> getAllocationOutEntrySet() {
            return storeAllocationOutCounts.entrySet();
        }

        // public abstract Res getRes();

        public ResCount getChangeCountByType(StockChange.StoreChangeType type) {
            return changeCounts.get(type);
        }

        public ResCount getChangeCountByType(String type) {
            return changeCounts.get(StockChange.StoreChangeType.valueOf(type));
        }

        public ResCount getAllocationOutCountByStoreId(String storeId) {
            return storeAllocationOutCounts.get(storesMap.get(storeId));
        }

        public boolean typeInChange(StockChange.StoreChangeType type) {
            return changeCounts.keySet().contains(type);
        }

        public boolean storeInChange(String storeId) {
            return storesMap.keySet().contains(storeId);
        }

        private ResCount getTotalCountBy(EnumSet<StockChange.StoreChangeType> types) {
            ResCount result = ResTotalCount.ZERO(getRes());
            for (StockChange.StoreChangeType type : types) {
                ResCount count = changeCounts.get(type);
                if (count != null) {
                    result = result.add(count);
                }
            }
            return result;
        }

        public ResCount getInCount() {
            return getTotalCountBy(StockChange.StoreChangeType.getAllIn());
        }

        public ResCount getOutCount() {
            return getTotalCountBy(StockChange.StoreChangeType.getAllOut());
        }


        public ResCount getLastCount() {
            ResCount result = getBeginCount();
            result = result.add(getInCount());
            result = result.subtract(getOutCount());
            return result;
        }


        public ResCount getResultCount() {
            ResCount result = getLastCount();
            result = result.add(getAddCount());
            result = result.subtract(getLossCount());
            return result;
        }


        public void putAllocationOutChange(Store store, ResCount changeCount) {
            ResCount count = storeAllocationOutCounts.get(store);
            if (count == null) {
                count = ResTotalCount.ZERO(changeCount.getRes());
            }
            count = count.add(changeCount);

            storeAllocationOutCounts.put(store, count);
            storesMap.put(store.getId(), store);
        }

        public void putChange(StockChange.StoreChangeType type, ResCount changeCount) {

            ResCount count = changeCounts.get(type);
            if (count == null) {
                count = ResTotalCount.ZERO(changeCount.getRes());
            }
            count = count.add(changeCount);

            changeCounts.put(type, count);
        }
    }


    public static class SeasonStockChangeTotalData extends SeasonStockChangeDataCalc implements TotalDataGroup.GroupTotalData {

        private ResCount beginCount;

        private ResCount addCount;

        private ResCount lossCount;

        private SeasonStockChangeData firstData;

        private Res res;

        public SeasonStockChangeTotalData(Res res, SeasonStockChangeData firstData) {
            super();
            beginCount = ResTotalCount.ZERO(res);
            addCount = ResTotalCount.ZERO(res);
            lossCount = ResTotalCount.ZERO(res);
            this.firstData = firstData;
            this.res = res;
        }

        public SeasonStockChangeData getFirstData() {
            return firstData;
        }

        public void setFirstData(SeasonStockChangeData firstData) {
            this.firstData = firstData;
        }

        @Override
        public ResCount getBeginCount() {
            return beginCount;
        }

        public void putBeginCount(ResCount count) {
            this.beginCount = this.beginCount.add(count);
        }

        @Override
        public ResCount getAddCount() {
            return addCount;
        }

        public void putAddCount(ResCount count) {
            this.addCount = this.addCount.add(count);
        }

        @Override
        public ResCount getLossCount() {
            return lossCount;
        }

        @Override
        public Res getRes() {
            return res;
        }

        public void putLossCount(ResCount count) {
            this.lossCount = this.lossCount.add(count);
        }

    }

    public static class SeasonStockChangeData extends StoreResCountEntity implements SeasonStockChangeDataBase {

        private InventoryItem inventoryItem;


        private SeasonStockChangeDataCalc calcHelper;

        public SeasonStockChangeData(InventoryItem inventoryItem) {
            super(inventoryItem.getStock().getStoreRes(), inventoryItem.getStock().getStoreRes().getRes().getResUnitByInDefault());

            calcHelper = new SeasonStockChangeDataCalc() {

                @Override
                public ResCount getBeginCount() {
                    return SeasonStockChangeData.this.getBeginCount();
                }

                @Override
                public ResCount getAddCount() {
                    if (InventoryItem.InventoryItemChangeType.INVENTORY_ADD.equals(getInventoryItem().getChangeType())) {
                        return getDiffCount();
                    } else
                        return ResTotalCount.ZERO(getRes());
                }

                @Override
                public ResCount getLossCount() {
                    if (InventoryItem.InventoryItemChangeType.INVENTORY_LOSS.equals(getInventoryItem().getChangeType())) {
                        return getDiffCount();
                    } else
                        return ResTotalCount.ZERO(getRes());
                }

                @Override
                public Res getRes() {
                    return SeasonStockChangeData.this.getRes();
                }
            };

            this.inventoryItem = inventoryItem;
        }


        public StoreResCount getDiffCount() {
            return new StoreResCount(getStoreRes(), inventoryItem.getChangeCount());
        }


        public InventoryItem.InventoryItemChangeType getCheckResultType() {
            return inventoryItem.getChangeType();
        }


        public List<Map.Entry<StockChange.StoreChangeType, ResCount>> getStoreInTypeCounts() {
            return calcHelper.getChangeEntrySetByTypes(StockChange.StoreChangeType.getAllIn());
        }


        public List<Map.Entry<StockChange.StoreChangeType, ResCount>> getStoreOutTypeCounts() {
            EnumSet<StockChange.StoreChangeType> types = StockChange.StoreChangeType.getAllOut();
            types.remove(StockChange.StoreChangeType.ALLOCATION_OUT);
            return calcHelper.getChangeEntrySetByTypes(types);
        }

        public List<Map.Entry<Store, ResCount>> getAllocationOutCounts() {
            List<Map.Entry<Store, ResCount>> result = new ArrayList<Map.Entry<Store, ResCount>>(calcHelper.getAllocationOutEntrySet());
            Collections.sort(result, new Comparator<Map.Entry<Store, ResCount>>() {
                @Override
                public int compare(Map.Entry<Store, ResCount> o1, Map.Entry<Store, ResCount> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            return result;
        }


        public StockChange.StoreChangeType getFirstInType() {
            if (getStoreInTypeCounts().isEmpty()) {
                return null;
            } else {
                return getStoreInTypeCounts().get(0).getKey();
            }
        }

        public StockChange.StoreChangeType getFirstOutType() {
            if (getStoreOutTypeCounts().isEmpty()) {
                return null;
            } else {
                return getStoreOutTypeCounts().get(0).getKey();
            }
        }

        public boolean isFirstStore(String storeId) {
            if (getAllocationOutCounts().isEmpty()) {
                return false;
            }
            return getAllocationOutCounts().get(0).getKey().getId().equals(storeId);
        }

        @Override
        public BigDecimal getCount() {
            return inventoryItem.getLastCount();
        }

        @Override
        public void setCount(BigDecimal count) {
            inventoryItem.setLastCount(count);
            Logging.getLog(getClass()).debug("count is:" + count + "|lastCount is:" + getLastCount().getMasterCount());
            if (count.compareTo(getLastCount().getMasterCount()) > 0) {
                inventoryItem.setChangeCount(count.subtract(getLastCount().getMasterCount()));
                inventoryItem.setChangeType(InventoryItem.InventoryItemChangeType.INVENTORY_ADD);

            } else if (count.compareTo(getLastCount().getMasterCount()) < 0) {
                inventoryItem.setChangeCount(getLastCount().getMasterCount().subtract(count));
                inventoryItem.setChangeType(InventoryItem.InventoryItemChangeType.INVENTORY_LOSS);
            } else {
                inventoryItem.setChangeCount(BigDecimal.ZERO);
                inventoryItem.setChangeType(InventoryItem.InventoryItemChangeType.NO_CHANGE);
            }

        }

        @Override
        public StoreRes getStoreRes() {
            return inventoryItem.getStock().getStoreRes();
        }

        @Override
        public void setStoreRes(StoreRes storeRes) {
            throw new IllegalArgumentException("cant set");
        }

        @Override
        public StoreResCount getBeginCount() {
            return new StoreResCount(getStoreRes(), inventoryItem.getBeforCount());
        }

        @Override
        public ResCount getAddCount() {
            return calcHelper.getAddCount();
        }

        @Override
        public ResCount getLossCount() {
            return calcHelper.getLossCount();
        }

        @Override
        public Set<Map.Entry<StockChange.StoreChangeType, ResCount>> getChangeEntrySet() {
            return calcHelper.getChangeEntrySet();
        }

        @Override
        public Set<Map.Entry<Store, ResCount>> getAllocationOutEntrySet() {
            return calcHelper.getAllocationOutEntrySet();
        }

        @Override
        public ResCount getChangeCountByType(StockChange.StoreChangeType type) {
            return calcHelper.getChangeCountByType(type);
        }

        @Override
        public ResCount getChangeCountByType(String type) {
            return calcHelper.getChangeCountByType(type);
        }

        @Override
        public ResCount getAllocationOutCountByStoreId(String storeId) {
            return calcHelper.getAllocationOutCountByStoreId(storeId);
        }

        @Override
        public boolean typeInChange(StockChange.StoreChangeType type) {
            return calcHelper.typeInChange(type);
        }

        @Override
        public boolean storeInChange(String storeId) {
            return calcHelper.storeInChange(storeId);
        }

        @Override
        public ResCount getInCount() {
            return calcHelper.getInCount();
        }

        @Override
        public ResCount getOutCount() {
            return calcHelper.getOutCount();
        }

        @Override
        public ResCount getLastCount() {
            return calcHelper.getLastCount();
        }

        @Override
        public ResCount getResultCount() {
            return calcHelper.getResultCount();
        }

        @Override
        public void putAllocationOutChange(Store store, ResCount changeCount) {
            calcHelper.putAllocationOutChange(store, changeCount);
        }

        @Override
        public void putChange(StockChange.StoreChangeType type, ResCount changeCount) {
            calcHelper.putChange(type, changeCount);
        }

        public InventoryItem getInventoryItem() {
            return inventoryItem;
        }


    }

}
