package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 6/19/14.
 */
@Name("storeChangeTotal")
public class StoreChangeTotal extends ErpEntityQuery<StockChangeItem> {

    private static final String EJBQL = "select item from StockChangeItem item  left join fetch item.storeRes storeRes "  +
            " left join fetch storeRes.res res left join fetch res.unitGroup unitGroup " +
            " left join fetch item.stockChange storeChange left join fetch storeChange.allocationForStoreOuts where item.stockChange.verify = true";


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

    private boolean accountModel = false;

    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        Logging.getLog(getClass()).debug("setStoreId:" + storeId);
        this.storeId = storeId;
    }

    public boolean isAccountModel() {
        return accountModel;
    }

    public void setAccountModel(boolean accountModel) {
        this.accountModel = accountModel;
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
                FormatGroupTotalData result = new FormatGroupTotalData(datas.iterator().next().getStoreRes().getRes());
                for(TotalChangeDataItem item: datas){
                   result.add(item);
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


    public static class FormatGroupTotalData{

        private Res res;

        private Map<StockChange.StoreChangeType,BigDecimal> masterCount;

        private Map<StockChange.StoreChangeType,BigDecimal> auxCount;

        private Map<String,BigDecimal> allocationMasterCount;

        private Map<String,BigDecimal> allocationAuxCount;

        public FormatGroupTotalData(Res res) {
            this.res = res;
            masterCount = new HashMap<StockChange.StoreChangeType, BigDecimal>();
            allocationMasterCount = new HashMap<String, BigDecimal>();
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                auxCount = new HashMap<StockChange.StoreChangeType, BigDecimal>();
                allocationAuxCount = new HashMap<String, BigDecimal>();
            }
        }

        public void add(TotalChangeDataItem item){

            BigDecimal count;
            for(StockChange.StoreChangeType type: item.getChangeCount().keySet()){

                count = masterCount.get(type);

                masterCount.put(type,item.getChangeCount().get(type).getMasterCount().add(count == null ? BigDecimal.ZERO : count));

                if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                    count = auxCount.get(type);
                    auxCount.put(type,item.getChangeCount().get(type).getAuxCount().add(count == null ? BigDecimal.ZERO : count));
                }
            }
            for(String storeId:item.getAllocationOutCount().keySet()){
                count = allocationMasterCount.get(storeId);

                allocationMasterCount.put(storeId,item.getAllocationOutCount().get(storeId).getMasterCount().add(count == null ? BigDecimal.ZERO : count));

                if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                    count = allocationAuxCount.get(storeId);
                    allocationAuxCount.put(storeId,item.getAllocationOutCount().get(storeId).getAuxCount().add(count == null ? BigDecimal.ZERO : count));
                }
            }

        }



        public Res getRes() {
            return res;
        }

        public Map<StockChange.StoreChangeType, BigDecimal> getMasterCount() {
            return masterCount;
        }

        public Map<StockChange.StoreChangeType, BigDecimal> getAuxCount() {
            return auxCount;
        }

        public Map<String, BigDecimal> getAllocationMasterCount() {
            return allocationMasterCount;
        }

        public Map<String, BigDecimal> getAllocationAuxCount() {
            return allocationAuxCount;
        }

        public BigDecimal getInMasterCountTotal(){
            BigDecimal result = BigDecimal.ZERO;
            for(StockChange.StoreChangeType type: masterCount.keySet()){
                if (!type.isOut()){
                    result = result.add(masterCount.get(type));
                }
            }
            return result;
        }


        public BigDecimal getOutMasterCountTotal(){
            BigDecimal result = BigDecimal.ZERO;
            for(StockChange.StoreChangeType type: masterCount.keySet()){
                if (type.isOut()){
                    result = result.add(masterCount.get(type));
                }
            }
            for(BigDecimal count: allocationMasterCount.values()){
                result = result.add(count);
            }

            return result;
        }

        public BigDecimal getInAuxCountTotal(){
            if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
               return null;
            }

            BigDecimal result = BigDecimal.ZERO;
            for(StockChange.StoreChangeType type: auxCount.keySet()){
                if (!type.isOut()){
                    result = result.add(auxCount.get(type));
                }
            }
            return result;
        }


        public BigDecimal getOutAuxCountTotal(){
            if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                return null;
            }
            BigDecimal result = BigDecimal.ZERO;
            for(StockChange.StoreChangeType type: auxCount.keySet()){
                if (type.isOut()){
                    result = result.add(auxCount.get(type));
                }
            }
            for(BigDecimal count: allocationAuxCount.values()){
                result = result.add(count);
            }

            return result;
        }


    }

    public static class TotalChangeDataItem {


        private StockAccount account;

        private StoreRes storeRes;

        public TotalChangeDataItem(StoreRes storeRes) {
            super();
            this.storeRes = storeRes;
        }

        public TotalChangeDataItem(StoreRes storeRes, StockAccount account) {
            this.storeRes = storeRes;
            this.account = account;
        }

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

        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public StockAccount getAccount() {
            return account;
        }

        public StoreResCount getInTotal(){
            StoreResCount result = new StoreResCount(storeRes,BigDecimal.ZERO);
            for (StockChange.StoreChangeType type : changeCount.keySet()){
                if (!type.isOut()){
                    result.add(changeCount.get(type));
                }
            }
            return result;
        }

        public StoreResCount getOutTotal(){
            StoreResCount result = new StoreResCount(storeRes,BigDecimal.ZERO);
            for (StockChange.StoreChangeType type : changeCount.keySet()){
                if (type.isOut()){
                    result.add(changeCount.get(type));
                }
            }
            for(StoreResCount allocationCount: allocationOutCount.values()){
                result.add(allocationCount);
            }

            return result;
        }
    }

}
