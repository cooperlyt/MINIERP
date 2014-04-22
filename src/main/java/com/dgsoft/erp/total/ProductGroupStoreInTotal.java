package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.ProductGroup;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Logging;

import java.util.*;

/**
 * Created by cooper on 3/3/14.
 */
@Name("productGroupStoreInTotal")
@Scope(ScopeType.CONVERSATION)
public class ProductGroupStoreInTotal extends StoreChangeResTotal {

    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem  " +
            "left join fetch stockChangeItem.stockChange stockChange " +
            "left join fetch stockChange.productStoreIns productStoreIns" +
            "left join fetch stockChangeItem.storeRes " +
            "where stockChangeItem.stockChange.verify = true and stockChangeItem.stockChange.operType = 'PRODUCE_IN'";

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{productGroupStoreInTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{productGroupStoreInTotal.searchDateArea.searchDateTo}",
            "stockChangeItem.storeRes.res.id = #{storeResList.resultSearchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResList.resultSearchFloatConvertRate}",
            "stockChangeItem.storeRes in (#{storeResList.filterResultList})"};


    public ProductGroupStoreInTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

    public Map<Date,Map<ProductGroup,StoreResCountTotalGroup>> getDayTotalMap(){
        Map<Date,Map<ProductGroup,StoreResCountTotalGroup>> result = new HashMap<Date, Map<ProductGroup, StoreResCountTotalGroup>>();
        Logging.getLog(getClass()).debug("resultList Count:" + getResultList().size());
        for (StockChangeItem item: getResultList()){
            Map<ProductGroup,StoreResCountTotalGroup> psMap = result.get(DataFormat.halfTime(item.getStockChange().getOperDate()));
            if (psMap == null){
                psMap = new HashMap<ProductGroup, StoreResCountTotalGroup>();
                result.put(DataFormat.halfTime(item.getStockChange().getOperDate()),psMap);
            }
            StoreResCountTotalGroup srcg = psMap.get(item.getStockChange().getProductStoreIn().getProductGroup());
            if (srcg == null){
                srcg = new StoreResCountTotalGroup();
                psMap.put(item.getStockChange().getProductStoreIn().getProductGroup(),srcg);
            }
            srcg.put(item);
        }
        Logging.getLog(getClass()).debug("total result Count:" + result.size());

        return result;
    }

//    public List<Map.Entry<Date,List<Map.Entry<ProductGroup,StoreResCountTotalGroup<StoreResCountEntity>>>>> getDayTotalList(){
//        List<Map.Entry<Date,List<Map.Entry<ProductGroup,StoreResCountTotalGroup<StoreResCountEntity>>>>> result = new ArrayList<Map.Entry<Date, List<Map.Entry<ProductGroup, StoreResCountTotalGroup<StoreResCountEntity>>>>>();
//        for ()
//
//
//
//        return null;
//    }
}