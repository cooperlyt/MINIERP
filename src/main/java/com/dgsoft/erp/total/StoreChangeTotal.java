package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.InventoryItemList;
import com.dgsoft.erp.action.InventoryItems;
import com.dgsoft.erp.action.StockStoreResList;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.AllocationOutGroup;
import com.dgsoft.erp.model.api.StockChangeGroup;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Logging;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 6/19/14.
 */
@Name("storeChangeTotal")
public class StoreChangeTotal extends ErpEntityQuery<StockChangeGroup> {

    private static final String EJBQL = "select new com.dgsoft.erp.model.api.StockChangeGroup(item.storeRes.id,item.stockChange.operType,sum(item.count)) from StockChangeItem item";


    private static final String[] RESTRICTIONS = {
            "item.stockChange.operDate > #{storeChangeTotal.dateFrom}",
            "item.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "item.stockChange.store.id = #{stockStoreResList.storeId}",
            "item.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "item.storeRes.res.id = #{storeResCondition.searchResId}",
            "item.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "item.storeRes.id in (#{storeResCondition.matchStoreResIds})"};


    @In(create = true)
    private StockStoreResList stockStoreResList;


    @In(create = true)
    private InventoryItemList inventoryItemList;

    @In(create = true)
    private EntityQuery<Store> allStoreList;

    @In(create = true)
    private StoreAllocationTotal storeAllocationTotal;

    private List<StockChange.StoreChangeType> storeInTypes;

    private List<StockChange.StoreChangeType> storeOutTypes;

    private List<Store> allocationOutStores;

    private boolean showInOutCount = true;

    private boolean hideZero = false;

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

    public List<StockChange.StoreChangeType> getStoreInTypes() {
        if (storeInTypes == null) {
            Set<StockChange.StoreChangeType> result = new HashSet<StockChange.StoreChangeType>();
            for (StockChange.StoreChangeType type : StockChange.StoreChangeType.getAllIn()) {
                for (SeasonStockChangeData data : getTotalResult()) {
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
                    for (SeasonStockChangeData data : getTotalResult()) {
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
                for (SeasonStockChangeData data : getTotalResult()) {
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

    public StoreChangeTotal() {
        super();
        setEjbql(EJBQL);
        setGroupBy("item.storeRes.id,item.stockChange.operType");
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private Inventory lastInventory;

    public Inventory getLastInventory() {
        if (lastInventory == null) {
            try {
                lastInventory = getEntityManager().createQuery("select inventory from Inventory inventory where inventory.checkDate = :checkDate and inventory.store.id = :storeId", Inventory.class)
                        .setParameter("checkDate", getDateFrom()).setParameter("storeId", getStoreId()).getSingleResult();
            } catch (NoResultException e) {
                Logging.getLog(getClass()).debug("lastInventory not found");
                lastInventory = new Inventory();
            }
        }
        return lastInventory;
    }

    private Date dateFrom;

    public Date getDateFrom() {
        if (dateFrom == null) {
            try {
                dateFrom = getEntityManager().createQuery("select max(inventory.checkDate) from Inventory inventory where (inventory.type = 'YEAR_INVENTORY' or inventory.type = 'MONTH_INVENTORY') and inventory.store.id = :storeId", Date.class).
                        setParameter("storeId", getStoreId()).getSingleResult();
            } catch (NoResultException e) {
                dateFrom = new Date(0);
            }
        }
        return dateFrom;

    }

    public List<TotalDataGroup<Res, SeasonStockChangeData, SeasonStockTotalData>> getResultGroup() {



        List<TotalDataGroup<Res, SeasonStockChangeData, SeasonStockTotalData>> resultGroup = TotalDataGroup.groupBy(getTotalResult(), new TotalGroupStrategy<Res, SeasonStockChangeData, SeasonStockTotalData>() {
            @Override
            public Res getKey(SeasonStockChangeData seasonStockChangeData) {
                return seasonStockChangeData.getStoreRes().getRes();
            }

            @Override
            public SeasonStockTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                return totalData(datas);
            }
        }, new TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, SeasonStockChangeData, SeasonStockTotalData>() {
            @Override
            public ResFormatGroupStrategy.StoreResFormatKey getKey(SeasonStockChangeData seasonStockChangeData) {
                return new ResFormatGroupStrategy.StoreResFormatKey(seasonStockChangeData.getStoreRes());
            }

            @Override
            public SeasonStockTotalData totalGroupData(Collection<SeasonStockChangeData> datas) {
                return totalData(datas);
            }
        });


        for (TotalDataGroup<Res, SeasonStockChangeData, SeasonStockTotalData> data : resultGroup) {
            TotalDataGroup.sort(data, new Comparator<SeasonStockChangeData>() {
                @Override
                public int compare(SeasonStockChangeData o1, SeasonStockChangeData o2) {
                    return o1.getStoreRes().compareTo(o2.getStoreRes());
                }
            });
        }

        return resultGroup;
    }

    private static SeasonStockTotalData totalData(Collection<SeasonStockChangeData> datas) {
        SeasonStockTotalData result = null;

        for (SeasonStockChangeData data : datas) {
            if (result == null) {
                result = new SeasonStockTotalData(data.getRes());

            }

            result.putBeginCount(data.getBeginCount());

            for (Map.Entry<StockChange.StoreChangeType, ResCount> entry : data.getChangeEntrySet()) {
                result.putChange(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<Store, ResCount> entry : data.getAllocationOutEntrySet()) {
                result.putAllocationOutChange(entry.getKey(), entry.getValue());
            }

        }
        return result;
    }

    private List<SeasonStockChangeData> totalResult;

    private List<SeasonStockChangeData> getTotalResult() {
        if (totalResult == null) {

            Map<String, SeasonStockChangeData> resultMap = new HashMap<String, SeasonStockChangeData>();
            Map<StoreRes, InventoryItem> inventoryItemMap = null;

            if (getLastInventory().getId() != null) {
                inventoryItemList.setInventoryId(getLastInventory().getId());

                inventoryItemMap = inventoryItemList.getStoreResMap();
            }


            Logging.getLog(getClass()).debug("stock Count:" + stockStoreResList.getResultList().size() + "-" + stockStoreResList.getStoreId());
            for (Stock stock : stockStoreResList.getResultList()) {
                InventoryItem item = null;
                if (inventoryItemMap != null) {
                    item = inventoryItemMap.get(stock.getStoreRes());
                }
                if (item == null) {
                    resultMap.put(stock.getStoreRes().getId(), new SeasonStockChangeData(stock));
                } else {
                    resultMap.put(stock.getStoreRes().getId(), new SeasonStockChangeData(item));
                }

            }


            for (StockChangeGroup data : getResultList()) {
                SeasonStockChangeData stcd = resultMap.get(data.getStoreResId());
                stcd.putChange(data.getType(), new StoreResCount(stcd.getStoreRes(), data.getMastCount()));
            }

            for (AllocationOutGroup aog : storeAllocationTotal.getResultList()) {
                SeasonStockChangeData stcd = resultMap.get(aog.getStoreResId());
                stcd.putAllocationOutChange(aog.getStore(), new StoreResCount(stcd.getStoreRes(), aog.getMastCount()));

            }

            totalResult = new ArrayList<SeasonStockChangeData>(resultMap.values());
        }
        return totalResult;
    }


    private String getStoreId() {
        return stockStoreResList.getStoreId();
    }


    @Override
    public void refresh() {
        super.refresh();
        totalResult = null;
        lastInventory = null;
        dateFrom = null;
    }


    public static class SeasonStockTotalData extends InventoryItems.SeasonStockChangeDataCalc implements TotalDataGroup.GroupTotalData {

        private Res res;

        private ResCount beginCount;

        public SeasonStockTotalData(Res res) {
            this.res = res;
            beginCount = ResTotalCount.ZERO(res);
        }

        public void putBeginCount(ResCount resCount) {
            beginCount = beginCount.add(resCount);
        }

        @Override
        public ResCount getBeginCount() {
            return beginCount;
        }

        @Override
        public ResCount getAddCount() {
            return ResTotalCount.ZERO(res);
        }

        @Override
        public ResCount getLossCount() {
            return ResTotalCount.ZERO(res);
        }

        @Override
        public Res getRes() {
            return res;
        }
    }

    public static class SeasonStockChangeData extends InventoryItems.SeasonStockChangeDataCalc {

        private Stock stock;

        private InventoryItem inventoryItem;

        public SeasonStockChangeData(Stock stock) {
            this.stock = stock;
        }

        public SeasonStockChangeData(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
            this.stock = inventoryItem.getStock();
        }

        @Override
        public ResCount getBeginCount() {
            if (inventoryItem == null) {
                return ResTotalCount.ZERO(getRes());
            } else {
                return new StoreResCount(inventoryItem.getStoreRes(), inventoryItem.getLastCount());
            }

        }

        @Override
        public ResCount getAddCount() {
            return ResTotalCount.ZERO(getRes());
        }

        @Override
        public ResCount getLossCount() {
            return ResTotalCount.ZERO(getRes());
        }

        @Override
        public Res getRes() {
            return stock.getRes();
        }

        public StoreRes getStoreRes() {
            return stock.getStoreRes();
        }
    }
}
