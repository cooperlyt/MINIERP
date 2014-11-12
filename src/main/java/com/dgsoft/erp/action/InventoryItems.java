package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.InventoryItem;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.AllocationOutGroup;
import com.dgsoft.erp.model.api.StockChangeGroup;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<StoreRes,SeasonStockChangeData> seasonStockChangeDatas;

    private void initItems() {
        if (items == null) {
            items = erpEntityLoader.getEntityManager().createQuery("select item from InventoryItem item left join fetch item.stock stock left join fetch stock.storeRes where item.inventory.id = :inventoryId",InventoryItem.class).
                    setParameter("inventoryId",inventoryHome.getId()).getResultList();

        }
    }

    public List<InventoryItem> getItems(){
        initItems();
        return items;
    }


    private void initSeasonStockChangeDatas(){
        if (seasonStockChangeDatas == null){

            List<StockChangeGroup> datas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockChangeGroup(item.storeRes,item.stockChange.operType,sum(item.count)) from StockChangeItem item where item.stockChange.operDate > :beginDate and item.stockChange.operDate <= :endDate and item.stockChange.store.id = :storeId group by item.storeRes,item.stockChange.operType",StockChangeGroup.class).
                    setParameter("beginDate",inventoryHome.getBeforInventoryDate()).setParameter("endDate",inventoryHome.getInstance().getCheckDate()).setParameter("storeId",inventoryHome.getInstance().getStore().getId()).getResultList();
            Map<StoreRes,Map<StockChange.StoreChangeType,BigDecimal>> dataMap = new HashMap<StoreRes, Map<StockChange.StoreChangeType, BigDecimal>>();

            for(StockChangeGroup data: datas){
                Map<StockChange.StoreChangeType,BigDecimal> countMap = dataMap.get(data.getStoreRes());
                if (countMap == null){
                    countMap = new HashMap<StockChange.StoreChangeType, BigDecimal>();
                }
                countMap.put(data.getType(),data.getMastCount());
            }


            List<AllocationOutGroup> aoDatas = erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.model.api.AllocationOutGroup(item.storeRes,item.stockChange.) from StockChangeItem item")

            seasonStockChangeDatas = new HashMap<StoreRes, SeasonStockChangeData>();
            for(InventoryItem item: items){
                SeasonStockChangeData newGroupData = new SeasonStockChangeData(item);
                seasonStockChangeDatas.put(item.getStock().getStoreRes(),newGroupData);
                Map<StockChange.StoreChangeType,BigDecimal> data = dataMap.get(newGroupData.getStoreRes());
                if (data != null){
                    for(Map.Entry<StockChange.StoreChangeType,BigDecimal> entry: data.entrySet()){
                        newGroupData.putChange(entry.getKey(),entry.getValue());
                    }
                }
            }


        }
    }






    public static class SeasonStockChangeData {


        //private StoreRes storeRes;

        //private StoreResCount beginCount;

        //private StoreResCount diffCount;


        private  InventoryItem inventoryItem;

        private Map<StockChange.StoreChangeType, StoreResCount> changeCount = new HashMap<StockChange.StoreChangeType, StoreResCount>();

        private Map<Store,StoreResCount> allocationOutCounts = new HashMap<Store, StoreResCount>();


        public SeasonStockChangeData(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
        }


        public StoreResCount getDiffCount() {
            return new StoreResCount(getStoreRes(),inventoryItem.getChangeCount());
        }


        public InventoryItem.InventoryItemChangeType getCheckResultType() {
            return inventoryItem.getChangeType();
        }

        public StoreRes getStoreRes() {
            return inventoryItem.getStock().getStoreRes();
        }


        public StoreResCount getBeginCount() {
            return new StoreResCount(getStoreRes(),inventoryItem.getBeforCount());
        }

        public void putAllocationOutChange(Store store,BigDecimal masterCount){
            StoreResCount addCount = new StoreResCount(getStoreRes(),masterCount);
            StoreResCount count = allocationOutCounts.get(store);
            if (count == null){
                allocationOutCounts.put(store,addCount);
            }else{
                count.add(addCount);
            }
        }

        public void putChange(StockChange.StoreChangeType type, BigDecimal masterCount){
            StoreResCount addCount = new StoreResCount(getStoreRes(),masterCount);
            if (!inventoryItem.getChangeType().equals(InventoryItem.InventoryItemChangeType.NO_CHANGE) && (inventoryItem.getStockChangeItem() != null)){
                if ((StockChange.StoreChangeType.STORE_CHECK_ADD.equals(type) &&
                        inventoryItem.getChangeType().equals(InventoryItem.InventoryItemChangeType.INVENTORY_ADD)) ||
                        (StockChange.StoreChangeType.STORE_CHECK_LOSS.equals(type) &&
                                inventoryItem.getChangeType().equals(InventoryItem.InventoryItemChangeType.INVENTORY_LOSS))){
                    addCount.subtract(getDiffCount());
                }
            }

            StoreResCount count = changeCount.get(type);
            if (count == null){
                changeCount.put(type,addCount);
            }else{
                count.add(addCount);
            }
        }

        public StoreResCount getChangeCountByType(StockChange.StoreChangeType type){
            return changeCount.get(type);
        }

        public StoreResCount getChangeCountByType(String type){
            return changeCount.get(StockChange.StoreChangeType.valueOf(type));
        }


        private StoreResCount getTotalCountBy(EnumSet<StockChange.StoreChangeType> types) {
            StoreResCount result = new StoreResCount(getStoreRes(), BigDecimal.ZERO);
            for (StockChange.StoreChangeType type : types) {
                    StoreResCount count = changeCount.get(type);
                    if (count != null)
                        result.add(count);
            }
            return result;
        }

        public StoreResCount getInCount() {
            return getTotalCountBy(StockChange.StoreChangeType.getAllIn());
        }

        public StoreResCount getOutCount() {
            return getTotalCountBy(StockChange.StoreChangeType.getAllOut());
        }



        public StoreResCount getLastCount(){
            StoreResCount result = new StoreResCount(getStoreRes(),getBeginCount().getMasterCount());
            result.add(getInCount());
            result.subtract(getOutCount());
            return result;
        }


        public StoreResCount getResultCount(){
            StoreResCount result = getLastCount();
            switch (getCheckResultType()){

                case NO_CHANGE:
                    break;
                case INVENTORY_ADD:
                    result.add(getDiffCount());
                    break;
                case INVENTORY_LOSS:
                    result.subtract(getDiffCount());
                    break;
                default:
                    throw new IllegalArgumentException("Type not Define");
            }
            return result;
        }
    }

}
