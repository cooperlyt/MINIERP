package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.tools.StoreResPropertyTreeNode;
import com.dgsoft.erp.total.StoreResCountUnionStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/8/13
 * Time: 2:35 PM
 */
@Name("stockList")
@Scope(ScopeType.CONVERSATION)
@Deprecated
public class StockList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select stock from Stock stock";

    private static final String[] RESTRICTIONS = {
            "stock.store.id =  #{stockList.searchStoreId}",
            "stock.storeRes.id = #{stockList.storeResId}",
            "stock.storeRes.res.id = #{stockList.resId}",
            "stock.storeRes.res.resCategory.id in (#{stockList.searchResCategoryIds})"};

    public StockList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    @Logger
    protected Log log;

    @In(create = true)
    private ResCategoryHome resCategoryHome;

    private String storeId;

    //private List<String> resCategoryIds;
    private String resCategoryId;

    private String resId;

    private String storeResId;

    private String selectedTitle;

    private StoreRes selectedStoreRes;

    private Map<FormatDefine, Format> filterFormats;

    @In
    private Identity identity;

    public String getSearchStoreId(){
        String result = getStoreId();
        if (result == null){
            if (!(identity.hasRole("erp.storage.manager") ||
                    identity.hasRole("erp.finance.cashier") ||
                    identity.hasRole("erp.sale.manager") ||
                    identity.hasRole("erp.sale.saler"))){
               result = "noSelectStore";
            }
        }
        return result;
    }

    public List<String> getSearchResCategoryIds(){
        if (resCategoryId == null){
            return null;
        }
        resCategoryHome.setId(resCategoryId);
        return resCategoryHome.getMatchIds();
    }

    public String getResCategoryId() {
        return resCategoryId;
    }

    public void setResCategoryId(String resCategoryId) {
        this.resCategoryId = resCategoryId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getStoreResId() {
        return storeResId;
    }

    public void setStoreResId(String storeResId) {
        this.storeResId = storeResId;
    }

    public String getSelectedTitle() {
        return selectedTitle;
    }

    public void setSelectedTitle(String selectedTitle) {
        this.selectedTitle = selectedTitle;
    }

    public StoreRes getSelectedStoreRes() {
        return selectedStoreRes;
    }

    public void treeSectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);

        selectChanged(tree.getRowData());

        tree.setRowKey(storedKey);
    }

    private void selectChanged(Object selected) {
        resCategoryId = null;
        resId = null;
        storeResId = null;
        selectedTitle = null;
        selectedStoreRes = null;
        filterFormats = null;
        setMaxResults(25);
        if (selected instanceof ResCategory) {

            resCategoryId = ((ResCategory) selected).getId();
            selectedTitle = ((ResCategory) selected).getName();
        } else if (selected instanceof Res) {
            resId = ((Res) selected).getId();
            selectedTitle = ((Res) selected).getName();
            setMaxResults(null);
        } else if (selected instanceof StoreResPropertyTreeNode){
            Res res = ((StoreResPropertyTreeNode)selected).getResParent();
            resId = res.getId();
            selectedTitle = res.getName();
            filterFormats = ((StoreResPropertyTreeNode)selected).getFormats();
            setMaxResults(null);
        } else if (selected instanceof StoreResPropertyTreeNode.StoreResTreeNode) {
            storeResId = ((StoreResPropertyTreeNode.StoreResTreeNode) selected).getStoreRes().getId();
            selectedStoreRes = ((StoreResPropertyTreeNode.StoreResTreeNode) selected).getStoreRes();
            setMaxResults(null);
        }
    }

    @Override
    public void refresh(){
        super.refresh();
        storeGroupResult = null;
        resGroupResult = null;
    }

    private TotalDataGroup<?,Stock,?> storeGroupResult = null;

    private void initStoreGroupResult(){
        if (storeGroupResult == null){
            storeGroupResult = TotalDataGroup.allGroupBy(getResultList(),new TotalGroupStrategy<Store, Stock,ResCount>() {
                @Override
                public Store getKey(Stock stock) {
                    return stock.getStore();
                }

                @Override
                public ResCount totalGroupData(Collection<Stock> datas) {
                    return null;
                }
            },new ResTotalCount.ResCountGroupStrategy<Stock>());

            TotalDataGroup.sort(storeGroupResult, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStoreRes().getId().compareTo(o2.getStoreRes().getId());
                }
            });

        }
    }

    public TotalDataGroup<?,Stock,?> getStoreGroupResult(){
        if (isAnyParameterDirty()) {
            refresh();
        }
        initStoreGroupResult();
        return storeGroupResult;
    }

    private  TotalDataGroup<?,Stock,?> resGroupResult = null;

    private void initResGroupResult(){
        if (resGroupResult == null){
            resGroupResult = TotalDataGroup.allGroupBy(getResultList(),new ResTotalCount.ResCountGroupStrategy<Stock>());
            TotalDataGroup.unionData(resGroupResult, new StoreResCountUnionStrategy<Stock>());
            TotalDataGroup.sort(resGroupResult,new Comparator<StoreResCountEntity>() {
                @Override
                public int compare(StoreResCountEntity o1, StoreResCountEntity o2) {
                    return o1.getStoreRes().getId().compareTo(o2.getStoreRes().getId());
                }
            });
        }
    }

    public TotalDataGroup<?,Stock,?> getResGroupResult(){
        if (isAnyParameterDirty()) {
            refresh();
        }
        initResGroupResult();
        return resGroupResult;
    }


    @Override
    public List<Stock> getResultList() {
        if (isAnyParameterDirty()) {


            if (storeResId != null) {
                String sql = "select sum(stock.count * stock.storeRes.floatConversionRate) from Stock stock where stock.storeRes.res.unitGroup.type = :unitType and stock.storeRes.id = :storeResId";
                if (storeId != null) {
                    sql += " and stock.store.id = :storeId";
                }
                Query query = getEntityManager().createQuery(sql).setParameter("unitType", UnitGroup.UnitGroupType.FLOAT_CONVERT)
                        .setParameter("storeResId", storeResId);
                if (storeId != null) {
                    query = query.setParameter("storeId", storeId);
                }
                totalAuxCount = (Number) query.getSingleResult();

            } else if (resId != null) {
                BigDecimal result = BigDecimal.ZERO;
                String sql = "select sum(stock.count * stock.storeRes.floatConversionRate) from Stock stock where stock.storeRes.res.unitGroup.type = :unitType and stock.storeRes.res.id = :resId";
                if (storeId != null) {
                    sql += " and stock.store.id = :storeId";
                }
                sql += " group by stock.storeRes.res.id";
                Query query = getEntityManager().createQuery(sql).setParameter("unitType", UnitGroup.UnitGroupType.FLOAT_CONVERT)
                        .setParameter("resId",resId);
                if (storeId != null) {
                    query = query.setParameter("storeId",storeId);
                }


                List sumResults = query.getResultList();
                for (Object obj : sumResults) {
                    result = result.add((BigDecimal) obj);
                }
                totalAuxCount = result;


            }

        }


        if (filterFormats == null){
            return super.getResultList();
        }else{
            List<Stock> result = new ArrayList<Stock>();
            for (Stock stock: super.getResultList()){
                if (ResHelper.instance().matchFormat(filterFormats.values(),stock.getStoreRes())){
                    result.add(stock);
                }
            }
            return result;
        }
    }


    private Number totalAuxCount;

    public Number getTotalAuxCount() {
        return totalAuxCount;
    }
}
