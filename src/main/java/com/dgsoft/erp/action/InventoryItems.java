package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.AllocationOutGroup;
import com.dgsoft.erp.model.api.StockChangeGroup;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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

    private List<InventoryItem> items;

    private Map<StoreRes, SeasonStockChangeData> seasonStockChangeDatas;

    private void initItems() {
        if (items == null) {
            items = erpEntityLoader.getEntityManager().createQuery("select item from InventoryItem item left join fetch item.stock stock left join fetch stock.storeRes where item.inventory.id = :inventoryId", InventoryItem.class).
                    setParameter("inventoryId", inventoryHome.getId()).getResultList();

        }
    }

    public List<InventoryItem> getItems() {
        initItems();
        return items;
    }

    protected Map<StoreRes, SeasonStockChangeData> getSeasonStockChangeDatas() {
        initSeasonStockChangeDatas();
        return seasonStockChangeDatas;
    }


    public List<TotalDataGroup<Res, SeasonStockChangeData>> getResultGroup() {
        return TotalDataGroup.groupBy(getSeasonStockChangeDatas().values(), new TotalGroupStrategy<Res, SeasonStockChangeData,SeasonStockChangeTotalData>() {
            @Override
            public Res getKey(SeasonStockChangeData seasonStockChangeData) {
                return seasonStockChangeData.getStoreRes().getRes();
            }

            @Override
            public SeasonStockChangeTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                SeasonStockChangeTotalData result = null;

                for(SeasonStockChangeData data: datas){
                    if (result == null){
                        result = new SeasonStockChangeTotalData(data.getRes());
                    }
                    result.putAddCount(data.getAddCount());
                    result.putBeginCount(data.getBeginCount());
                    result.putLossCount(data.getLossCount());
                }
                return result;
            }
        }, new TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, SeasonStockChangeData,SeasonStockChangeTotalData>() {
            @Override
            public ResFormatGroupStrategy.StoreResFormatKey getKey(SeasonStockChangeData seasonStockChangeData) {
                return new ResFormatGroupStrategy.StoreResFormatKey(seasonStockChangeData.getStoreRes());
            }

            @Override
            public SeasonStockChangeTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                SeasonStockChangeTotalData result = null;

                for(SeasonStockChangeData data: datas){
                    if (result == null){
                        result = new SeasonStockChangeTotalData(data.getRes());
                    }
                    result.putAddCount(data.getAddCount());
                    result.putBeginCount(data.getBeginCount());
                    result.putLossCount(data.getLossCount());
                }
                return result;
            }
        });
    }


    private void initSeasonStockChangeDatas() {
        if (seasonStockChangeDatas == null) {

            List<StockChangeGroup> datas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockChangeGroup(item.storeRes,item.stockChange.operType,sum(item.count)) from StockChangeItem item where item.stockChange.operDate > :beginDate and item.stockChange.operDate <= :endDate and item.stockChange.store.id = :storeId and ( ((item.stockChange.operType <> 'STORE_CHECK_ADD') and (item.stockChange.operType <> 'STORE_CHECK_LOSS')) or (item.stockChange.inventory.id <> :thisInventoryId) ) group by item.storeRes,item.stockChange.operType", StockChangeGroup.class).
                    setParameter("beginDate", inventoryHome.getBeforInventoryDate()).setParameter("endDate", inventoryHome.getInstance().getCheckDate()).
                    setParameter("storeId", inventoryHome.getInstance().getStore().getId()).setParameter("thisInventoryId", inventoryHome.getInstance().getId()).getResultList();
            Map<StoreRes, Map<StockChange.StoreChangeType, BigDecimal>> dataMap = new HashMap<StoreRes, Map<StockChange.StoreChangeType, BigDecimal>>();

            for (StockChangeGroup data : datas) {
                Map<StockChange.StoreChangeType, BigDecimal> countMap = dataMap.get(data.getStoreRes());
                if (countMap == null) {
                    countMap = new HashMap<StockChange.StoreChangeType, BigDecimal>();
                }
                countMap.put(data.getType(), data.getMastCount());
            }


            List<AllocationOutGroup> aoDatas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.AllocationOutGroup(item.storeRes,item.stockChange.allocation.inStore,sum(item.count)) from StockChangeItem item where item.stockChange.operType = 'ALLOCATION_OUT' and item.stockChange.operDate > :beginDate and item.stockChange.operDate <= :endDate and item.stockChange.allocation.outStore.id = :storeId group by item.storeRes,item.stockChange.allocation.inStore", AllocationOutGroup.class).
                    setParameter("beginDate", inventoryHome.getBeforInventoryDate()).setParameter("endDate", inventoryHome.getInstance().getCheckDate()).setParameter("storeId", inventoryHome.getInstance().getStore().getId()).getResultList();
            Map<StoreRes, AllocationOutGroup> aoDataMap = new HashMap<StoreRes, AllocationOutGroup>();
            for (AllocationOutGroup ao : aoDatas) {
                aoDataMap.put(ao.getStoreRes(), ao);
            }


            seasonStockChangeDatas = new HashMap<StoreRes, SeasonStockChangeData>();
            for (InventoryItem item : getItems()) {
                SeasonStockChangeData newGroupData = new SeasonStockChangeData(item);
                seasonStockChangeDatas.put(item.getStock().getStoreRes(), newGroupData);
                Map<StockChange.StoreChangeType, BigDecimal> data = dataMap.get(newGroupData.getStoreRes());
                if (data != null) {
                    for (Map.Entry<StockChange.StoreChangeType, BigDecimal> entry : data.entrySet()) {
                        newGroupData.putChange(entry.getKey(), new StoreResCount(item.getStock().getStoreRes(),entry.getValue()));
                    }
                }
                AllocationOutGroup aoGroup = aoDataMap.get(newGroupData.getStoreRes());
                if (aoGroup != null) {
                    newGroupData.putAllocationOutChange(aoGroup.getStore(), new StoreResCount(item.getStock().getStoreRes(),aoGroup.getMastCount()));
                }
            }


        }
    }


    public static abstract class SeasonStockChangeDataBase {

        public abstract ResCount getBeginCount();

        public abstract ResCount getAddCount();

        public abstract ResCount getLossCount();

        private Map<StockChange.StoreChangeType, ResCount> changeCounts;

        private Map<Store, ResCount> allocationOutCounts;

        public abstract Res getRes();


        public ResCount getChangeCountByType(StockChange.StoreChangeType type) {
            return changeCounts.get(type);
        }

        public ResCount getChangeCountByType(String type) {
            return changeCounts.get(StockChange.StoreChangeType.valueOf(type));
        }


        private ResCount getTotalCountBy(EnumSet<StockChange.StoreChangeType> types) {
            ResCount result = ResTotalCount.ZERO(getRes());
            for (StockChange.StoreChangeType type : types) {
                ResCount count = changeCounts.get(type);
                if (count != null)
                    result = result.add(count);
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
            ResCount result = ResTotalCount.ZERO(getRes());
            result = result.add(getBeginCount());
            result.add(getInCount());
            result.subtract(getOutCount());
            return result;
        }


        public ResCount getResultCount() {
            ResCount result = getLastCount();
            result = result.add(getAddCount());
            result = result.subtract(getLossCount());
            return result;
        }


        public void putAllocationOutChange(Store store, ResCount changeCount) {
            ResCount count = allocationOutCounts.get(store);
            if (count == null) {
                count = changeCount;
            } else {
                count = count.add(changeCount);
            }
            allocationOutCounts.put(store, count);
        }

        public void putChange(StockChange.StoreChangeType type, ResCount changeCount) {

            ResCount count = changeCounts.get(type);
            if (count == null) {
                count = changeCount;
            } else {
                count = count.add(changeCount);
            }
            changeCounts.put(type, count);
        }
    }


    public static class SeasonStockChangeTotalData extends SeasonStockChangeDataBase {

        private Res res;

        private ResCount beginCount;

        private ResCount addCount;

        private ResCount lossCount;

        public SeasonStockChangeTotalData(Res res) {
            this.res = res;
            beginCount = ResTotalCount.ZERO(res);
            addCount = ResTotalCount.ZERO(res);
            lossCount = ResTotalCount.ZERO(res);

        }

        @Override
        public ResCount getBeginCount() {
            return beginCount;
        }

        public void putBeginCount(ResCount count){
            this.beginCount = this.beginCount.add(count);
        }

        @Override
        public ResCount getAddCount() {
            return addCount;
        }

        public void putAddCount(ResCount count){
            this.addCount = this.addCount.add(count);
        }

        @Override
        public ResCount getLossCount() {
            return lossCount;
        }

        public void putLossCount(ResCount count){
            this.lossCount = this.lossCount.add(count);
        }

        @Override
        public Res getRes() {
            return res;
        }

    }

    public static class SeasonStockChangeData extends SeasonStockChangeDataBase {

        private InventoryItem inventoryItem;


        public SeasonStockChangeData(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
        }


        public StoreResCount getDiffCount() {
            return new StoreResCount(getStoreRes(), inventoryItem.getChangeCount());
        }


        public InventoryItem.InventoryItemChangeType getCheckResultType() {
            return inventoryItem.getChangeType();
        }

        public StoreRes getStoreRes() {
            return inventoryItem.getStock().getStoreRes();
        }


        public StoreResCount getBeginCount() {
            return new StoreResCount(getStoreRes(), inventoryItem.getBeforCount());
        }

        @Override
        public ResCount getAddCount() {
            if (InventoryItem.InventoryItemChangeType.INVENTORY_ADD.equals(inventoryItem.getChangeType())) {
                return getDiffCount();
            } else
                return ResTotalCount.ZERO(getRes());
        }

        @Override
        public ResCount getLossCount() {
            if (InventoryItem.InventoryItemChangeType.INVENTORY_LOSS.equals(inventoryItem.getChangeType())) {
                return getDiffCount();
            } else
                return ResTotalCount.ZERO(getRes());
        }


        @Override
        public Res getRes() {
            return getStoreRes().getRes();
        }


    }

}
