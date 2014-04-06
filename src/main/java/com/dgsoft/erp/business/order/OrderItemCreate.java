package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created by cooper on 4/6/14.
 */
@Name("orderItemCreate")
@Scope(ScopeType.CONVERSATION)
public class OrderItemCreate {

    public enum CreateBy{
       RES_CATEGORY,RES,STORE_RES;
    }

    private OrderItem editingItem;

    @In(create = true)
    private ResHome resHome;

    @In(create = true)
    private StoreResHome storeResHome;

    @In(required = false)
    private ResCategoryHome resCategoryHome;

    @In
    private ResHelper resHelper;

    private CreateBy createBy;

    @In(create = true)
    private ResLocate resLocate;

    public void locateByCode() {

        switch (resLocate.locateByCode(StockChange.StoreChangeType.SELL_OUT)) {

            case NOT_FOUND:
                break;
            case FOUND_STORERES:
                storeResSelected();
                break;
            case FOUND_RES:
                resSelected();
                break;
        }
    }

    public void resCategorySelected() {
        editingItem = null;
        resHome.clearInstance();
        storeResHome.clearInstance();
        createBy = CreateBy.RES_CATEGORY;
    }

    public void resSelected() {
        editingItem = new OrderItem(resHome.getInstance(),
                resHelper.getFormatHistory(resHome.getInstance()),
                resHelper.getFloatConvertRateHistory(resHome.getInstance()),
                resHome.getInstance().getResUnitByOutDefault());

        resCategoryHome.setId(resHome.getInstance().getResCategory().getId());
        createBy = CreateBy.RES;
    }

    public void storeResSelected() {
        resHome.setId(storeResHome.getInstance().getRes().getId());
        editingItem = new OrderItem(storeResHome.getInstance(),
                resHelper.getFormatHistory(resHome.getInstance()),
                resHelper.getFloatConvertRateHistory(resHome.getInstance()),
                resHome.getInstance().getResUnitByOutDefault());

        resCategoryHome.setId(storeResHome.getInstance().getRes().getResCategory().getId());
        createBy = CreateBy.STORE_RES;
    }

    public void resChange() {
        if ((editingItem == null) || (!editingItem.getRes().equals(resHome.getInstance()))) {
            editingItem = new OrderItem(resHome.getInstance(),
                    resHelper.getFormatHistory(resHome.getInstance()),
                    resHelper.getFloatConvertRateHistory(resHome.getInstance()),
                    resHome.getInstance().getResUnitByOutDefault());
            createBy = CreateBy.RES_CATEGORY;
        }
    }

    public OrderItem getEditingItem() {
        return editingItem;
    }

    public void clear() {
        editingItem = null;
        resHome.clearInstance();
        storeResHome.clearInstance();
        resCategoryHome.clearInstance();
    }

    public void createNext() {
        switch (createBy){

            case RES_CATEGORY:
                resCategorySelected();
                break;
            case RES:
                resSelected();
                break;
            case STORE_RES:
                storeResSelected();
                break;
        }

    }
}
