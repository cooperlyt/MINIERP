package com.dgsoft.erp.total;

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.erp.action.StoreResList;
import com.dgsoft.erp.model.ProductGroup;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

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

    @In(create = true)
    private StoreResList storeResList;


    public ProductGroupStoreInTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

    public StockChange.StoreChangeType getChangeType() {
        return StockChange.StoreChangeType.PRODUCE_IN;
    }

    public Map<Date,Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>>> getDayTotalMap(){
        Map<Date,Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>>> result = new HashMap<Date, Map<ProductGroup, StoreResCountGroup<StoreResCountEntity>>>();
        for (StockChangeItem item: getResultList()){
            Map<ProductGroup,StoreResCountGroup<StoreResCountEntity>> psMap = result.get(DataFormat.halfTime(item.getStockChange().getOperDate()));
            if (psMap == null){
                psMap = new HashMap<ProductGroup, StoreResCountGroup<StoreResCountEntity>>();
            }
            StoreResCountGroup<StoreResCountEntity> srcg = psMap.get(item.getStockChange().getProductStoreIn().getProductGroup());
            if (srcg == null){
                srcg = new StoreResCountGroup<StoreResCountEntity>();
            }
            srcg.put(item);
        }
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