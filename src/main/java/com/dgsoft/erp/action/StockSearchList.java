package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StockView;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cooper on 5/13/14.
 */
@Name("stockSearchList")
@Scope(ScopeType.CONVERSATION)
public class StockSearchList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select stock from Stock stock left join fetch stock.storeRes storeRes " +
            "left join fetch storeRes.res res left join fetch res.unitGroup where stock.count > 0 ";

    private static final String[] RESTRICTIONS = {
            "stock.store.id =  #{stockSearchList.storeId}",
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

    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    private StoreResCountGroup<Stock> totalResult;

    private void initTotalResult() {
        if ((getMaxResults() != null) || (totalResult == null)) {
            setMaxResults(null);
            totalResult = new StoreResCountGroup<Stock>(getResultList());
        }
    }

    public StoreResCountGroup<Stock> getTotalResult() {
        initTotalResult();
        return totalResult;
    }

    @Override
    public void refresh() {
        super.refresh();
        totalResult = null;
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
