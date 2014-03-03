package com.dgsoft.erp.total;

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.ProductGroup;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.util.*;

/**
 * Created by cooper on 3/3/14.
 */
@Name("productGroupStoreInTotal")
public class ProductGroupStoreInTotal extends StoreChangeResTotal {


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operType = #{productGroupStoreInTotal.changeType}",
            "stockChangeItem.stockChange.operDate >= #{productGroupStoreInTotal.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{productGroupStoreInTotal.searchDateTo}",
            "stockChangeItem.storeRes.res.id = #{storeResList.resultSearchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResList.resultSearchFloatConvertRate}",
            "stockChangeItem.storeRes in (#{storeResList.filterResultList})"};


    public ProductGroupStoreInTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

    public StockChange.StoreChangeType getChangeType() {
        return StockChange.StoreChangeType.PRODUCE_IN;
    }

    public Map<Date,Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>>> getDayTotalMap(){
        Map<Date,Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>>> result = new HashMap<Date, Map<ProductGroup, StoreResCountGroup<StoreResCountEntity>>>();
        Logging.getLog(getClass()).debug("resultList Count:" + getResultList().size());
        for (StockChangeItem item: getResultList()){
            Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>> psMap = result.get(DataFormat.halfTime(item.getStockChange().getOperDate()));
            if (psMap == null){
                psMap = new HashMap<ProductGroup, StoreResCountGroup<StoreResCountEntity>>();
                result.put(DataFormat.halfTime(item.getStockChange().getOperDate()),psMap);
            }
            StoreResCountGroup<StoreResCountEntity> srcg = psMap.get(item.getStockChange().getProductStoreIn().getProductGroup());
            if (srcg == null){
                srcg = new StoreResCountGroup<StoreResCountEntity>();
                psMap.put(item.getStockChange().getProductStoreIn().getProductGroup(),srcg);
            }
            srcg.put(item);
        }
        Logging.getLog(getClass()).debug("total result Count:" + result.size());
        return result;
    }

//    public List<Map.Entry<Date,List<Map.Entry<ProductGroup,StoreResCountGroup<StoreResCountEntity>>>>> getDayTotalList(){
//        List<Map.Entry<Date,List<Map.Entry<ProductGroup,StoreResCountGroup<StoreResCountEntity>>>>> result = new ArrayList<Map.Entry<Date, List<Map.Entry<ProductGroup, StoreResCountGroup<StoreResCountEntity>>>>>();
//        for ()
//
//
//
//        return null;
//    }
}