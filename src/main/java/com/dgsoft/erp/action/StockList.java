package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.tools.StoreResPropertyTreeNode;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/8/13
 * Time: 2:35 PM
 */
@Name("stockList")
@Scope(ScopeType.CONVERSATION)
public class StockList extends ErpEntityQuery<Stock> {

    private static final String EJBQL = "select stock from Stock stock";

    private static final String[] RESTRICTIONS = {
            "stock.store.id =  #{stockList.storeId}",
            "stock.storeRes.id = #{stockList.storeResId}",
            "stock.storeRes.res.id = #{stockList.resId}",
            "stock.storeRes.res.resCategory.id = #{stockList.resCategoryId}"};

    public StockList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    @Logger
    protected Log log;

    private String storeId;

    private String resCategoryId;

    private String resId;

    private String storeResId;

    private String selectedTitle;

    private StoreRes selectedStoreRes;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getResCategoryId() {
        return resCategoryId;
    }

    public void setResCategoryId(String resCategoryId) {
        this.resCategoryId = resCategoryId;
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
        if (selected instanceof ResCategory) {
            resCategoryId = ((ResCategory) selected).getId();
            selectedTitle = ((ResCategory) selected).getName();
        } else if (selected instanceof Res) {
            resId = ((Res) selected).getId();
            selectedTitle = ((Res) selected).getName();
        } else if (selected instanceof StoreResPropertyTreeNode.StoreResTreeNode) {
            storeResId = ((StoreResPropertyTreeNode.StoreResTreeNode) selected).getStoreRes().getId();
            selectedStoreRes = ((StoreResPropertyTreeNode.StoreResTreeNode) selected).getStoreRes();
        }
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

        return super.getResultList();
    }


    private Number totalAuxCount;

    public Number getTotalAuxCount() {
        return totalAuxCount;
    }
}
