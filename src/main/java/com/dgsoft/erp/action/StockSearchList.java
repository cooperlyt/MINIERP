package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StockView;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 5/13/14.
 */
@Name("stockSearchList")
@Scope(ScopeType.CONVERSATION)
public class StockSearchList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select stock from Stock stock left join fetch stock.storeRes storeRes " +
            "left join fetch storeRes.res res left join fetch res.unitGroup left join fetch stock.store where stock.count != 0 ";

    private static final String[] RESTRICTIONS = {
            "stock.store.id in (#{stockSearchList.storeIds})",
            "stock.storeRes.code = #{storeResCondition.storeResCode}",
            "stock.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stock.storeRes.res.id = #{storeResCondition.searchResId}",
            "stock.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stock.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public StockSearchList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    @In(create = true)
    private StoreCredentials storeCredentials;


    public String getStoreId() {
        for(BatchOperData<Store> store: getSelectStores()){
            if (store.isSelected()){
                return store.getData().getId();
            }
        }
        return null;
    }

    public void setStoreId(String storeId) {
        for(BatchOperData<Store> store: getSelectStores()){
            store.setSelected(store.getData().getId().equals(storeId));
        }
    }

    private StoreResCountGroup<Stock> totalResult;
    @Deprecated //use StockViewSearchList
    private void initTotalResult() {
        if ((getMaxResults() != null) || (totalResult == null)) {
            setMaxResults(null);
            totalResult = new StoreResCountGroup<Stock>(getResultList());
        }
    }
    @Deprecated //use StockViewSearchList
    public StoreResCountGroup<Stock> getTotalResult() {
        initTotalResult();
        return totalResult;
    }


    public List<String> getStoreIds() {
        List<String> result = new ArrayList<String>();
        for(BatchOperData<Store> store: getSelectStores()){
            if (store.isSelected()){
                result.add(store.getData().getId());
            }
        }
        if (result.isEmpty()){
            result = storeCredentials.getSearchViewStoreIds();
        }

        return result;
    }

    public void setStoreIds(List<String> storeIds) {
        for(BatchOperData<Store> store: getSelectStores()){
            store.setSelected(storeIds.contains(store.getData().getId()));
        }
    }

    public List<BatchOperData<Store>> selectStores;

    public List<BatchOperData<Store>> getSelectStores() {
        if(selectStores == null){
            selectStores = new ArrayList<BatchOperData<Store>>();
            for(Store store: storeCredentials.getViewStores()){
                selectStores.add(new BatchOperData<Store>(store,true));
            }
        }
        return selectStores;
    }


    @Override
    public void refresh() {
        super.refresh();
        totalResult = null;
    }
    @Deprecated //use StockViewSearchList
    private List<String> getStoreResIds(List<StoreRes> storeReses) {
        List<String> result = new ArrayList<String>(storeReses.size());
        for (StoreRes storeRes : storeReses) {
            if (storeRes.getId() != null)
                result.add(storeRes.getId());
        }
        return result;
    }
    @Deprecated //use StockViewSearchList
    public List<StockView> searchStockViews(Store store, List<StoreRes> storeReses) {
        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.store.id = :storeId and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeId", store.getId()).setParameter("storeResIds", ids).getResultList();
    }
    @Deprecated //use StockViewSearchList
    public StockView searchStockViews(Store store, StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        List<StockView> result = searchStockViews(store, storeReses);
        if (result.isEmpty()) {
            return null;
        } else
            return result.get(0);
    }

    @Deprecated //use StockViewSearchList
    public List<StockView> searchStockViews(StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        return searchStockViews(storeReses);
    }

    @Deprecated //use StockViewSearchList
    public List<StockView> searchStockViews(List<StoreRes> storeReses) {

        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeResIds", ids).getResultList();
    }



}
