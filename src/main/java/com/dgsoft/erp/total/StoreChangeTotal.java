package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created by cooper on 6/19/14.
 */
@Name("storeChangeTotal")
public class StoreChangeTotal extends ErpEntityQuery<StockChangeItem> {

    private static final String EJBQL = "select item from StockChangeItem item  left join fetch item.storeRes storeRes "  +
            "          left join fetch storeRes.res res left join fetch res.unitGroup unitGroup where item.stockChange.verify = true";


    private static final String[] RESTRICTIONS = {
            "item.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "item.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "item.stockChange.store.id = #{storeChangeTotal.storeId}",
            "item.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "item.storeRes.res.id = #{storeResCondition.searchResId}",
            "item.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "item.storeRes.id in (#{storeResCondition.matchStoreResIds})"};


    public StoreChangeTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    private Map<String,TotalChangeDataItem> totalResult;

    private void initTotalResult(){
        if (totalResult == null){
            getResultList();
            totalResult = new HashMap<String, TotalChangeDataItem>();
            for(StockChangeItem item : getResultList()){
                TotalChangeDataItem data = totalResult.get(item.getStoreRes().getId());
                if (data == null){
                    data = new TotalChangeDataItem(item.getStoreRes());
                    totalResult.put(data.getStoreRes().getId(),data);
                }

                if (item.getStockChange().getOperType().equals(StockChange.StoreChangeType.ALLOCATION_OUT)){
                    data.putChange(item.getStockChange().getAllocationForStoreOut().getInStore().getId(),item);
                }else{
                    data.putChange(item.getStockChange().getOperType(),item);
                }
            }
        }
    }

    public List<TotalChangeDataItem> getTotalResultList(){
        if (isAnyParameterDirty()){
            refresh();
        }
        initTotalResult();
        List<TotalChangeDataItem> result = new ArrayList<TotalChangeDataItem>(totalResult.values());
        Collections.sort(result,new Comparator<TotalChangeDataItem>() {
            @Override
            public int compare(TotalChangeDataItem o1, TotalChangeDataItem o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });
        return result;
    }


    public List<TotalDataGroup<SameFormatResGroupStrategy.StoreResFormatKey,TotalChangeDataItem>> getTotalResultGroup(){
        if (isAnyParameterDirty()){
            refresh();
        }
        initTotalResult();

        return TotalDataGroup.groupBy(totalResult.values(), new TotalGroupStrategy<SameFormatResGroupStrategy.StoreResFormatKey, TotalChangeDataItem>() {
            @Override
            public SameFormatResGroupStrategy.StoreResFormatKey getKey(TotalChangeDataItem totalChangeDataItem) {
                return new SameFormatResGroupStrategy.StoreResFormatKey(totalChangeDataItem.getStoreRes());
            }

            @Override
            public Object totalGroupData(Collection<TotalChangeDataItem> datas) {
                TotalChangeData result = new TotalChangeData();
                for(TotalChangeDataItem item: datas){
                    for(StockChange.StoreChangeType type: item.getChangeCount().keySet()){
                        result.putChange(type, item.getChangeCount().get(type));
                    }
                    for(String storeId:item.getAllocationOutCount().keySet()){
                        result.putChange(storeId,item.getAllocationOutCount().get(storeId));
                    }
                }
                return result;
            }
        });

    }

    @Override
    public void refresh(){
        super.refresh();
        totalResult = null;
    }

    public static class TotalChangeData{

        private Map<StockChange.StoreChangeType, StoreResCount> changeCount = new HashMap<StockChange.StoreChangeType, StoreResCount>();

        private Map<String,StoreResCount> allocationOutCount = new HashMap<String, StoreResCount>();

        public  void putChange(StockChange.StoreChangeType type, StoreResCountEntity count){
            StoreResCount result = changeCount.get(type);
            if (result == null){
                result = new StoreResCount(count.getStoreRes(),count.getCount());
                changeCount.put(type,result);
            }else{
                result.add(count);
            }

        }
        public  void putChange(String storeId, StoreResCountEntity count){

            StoreResCount result = allocationOutCount.get(storeId);
            if (result == null){
                result = new StoreResCount(count.getStoreRes(),count.getCount());
                allocationOutCount.put(storeId,result);
            }else {
                result.add(count);
            }
        }

        public Map<StockChange.StoreChangeType, StoreResCount> getChangeCount() {
            return changeCount;
        }

        public void setChangeCount(Map<StockChange.StoreChangeType, StoreResCount> changeCount) {
            this.changeCount = changeCount;
        }

        public Map<String, StoreResCount> getAllocationOutCount() {
            return allocationOutCount;
        }

        public void setAllocationOutCount(Map<String, StoreResCount> allocationOutCount) {
            this.allocationOutCount = allocationOutCount;
        }




    }


    public static class TotalChangeDataItem extends TotalChangeData{

        private StoreRes storeRes;

        public TotalChangeDataItem(StoreRes storeRes) {
            super();
            this.storeRes = storeRes;
        }


        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
        }


    }

}
