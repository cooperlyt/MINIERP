package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StockView;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cooper on 11/28/14.
 */
@Name("stockViewSearchList")
@Scope(ScopeType.CONVERSATION)
public class StockViewSearchList extends ErpEntityQuery<StockView> {

    private static final String EJBQL = "select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0";

    private static final String[] RESTRICTIONS = {
            "stock.store.id =  #{stockViewSearchList.storeId}",
            "stock.storeRes.code = #{storeResCondition.storeResCode}",
            "stock.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stock.storeRes.res.id = #{storeResCondition.searchResId}",
            "stock.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stock.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public StockViewSearchList() {
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


    public List<TotalDataGroup<Res,StockView,StockView.StockTotalCount>> resultGroup;

    public List<TotalDataGroup<Res,StockView,StockView.StockTotalCount>> getResultGroup(){
        if (resultGroup == null){
            resultGroup =
                    TotalDataGroup.groupBy(getResultList(), new StockView.ResCountGroupStrategy<StockView>(), new StockView.FormatCountGroupStrategy());
            for(TotalDataGroup<Res,StockView,StockView.StockTotalCount> group: resultGroup){
                TotalDataGroup.sort(group,new Comparator<StockView>() {
                    @Override
                    public int compare(StockView o1, StockView o2) {
                        return o1.getStock().getStoreRes().compareTo(o2.getStock().getStoreRes());
                    }
                });
            }
        }
        return resultGroup;
    }

    @Override
    public void refresh() {
        super.refresh();
        resultGroup = null;
    }

    private List<String> getStoreResIds(List<StoreRes> storeReses) {
        List<String> result = new ArrayList<String>(storeReses.size());
        for (StoreRes storeRes : storeReses) {
            if (storeRes.getId() != null)
                result.add(storeRes.getId());
        }
        return result;
    }

    public List<StockView> searchStockViews(Store store, List<StoreRes> storeReses) {
        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.store.id = :storeId and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeId", store.getId()).setParameter("storeResIds", ids).getResultList();
    }

    public StockView searchStockViews(Store store, StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        List<StockView> result = searchStockViews(store, storeReses);
        if (result.isEmpty()) {
            return null;
        } else
            return result.get(0);
    }

    public List<StockView> searchStockViews(StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        return searchStockViews(storeReses);
    }

    public List<StockView> searchStockViews(List<StoreRes> storeReses) {

        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeResIds", ids).getResultList();
    }


}
