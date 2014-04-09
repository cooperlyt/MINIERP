package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 09/04/14
 * Time: 16:52
 */
@Scope(ScopeType.CONVERSATION)
public abstract class StoreResEntityItemCreate<E extends StoreResEntity> {


    private E editingItem;

    protected abstract E createInstance(Res res);

    protected abstract E createInstance(StoreRes storeRes);

    @In(create = true)
    private ResHome resHome;
    @In(create = true)
    private StoreResHome storeResHome;
    @In(required = false)
    private ResCategoryHome resCategoryHome;

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
        editingItem =  createInstance(resHome.getInstance());

        resCategoryHome.setId(resHome.getInstance().getResCategory().getId());
        createBy = CreateBy.RES;
    }

    public void storeResSelected() {
        resHome.setId(storeResHome.getInstance().getRes().getId());
        editingItem = createInstance(storeResHome.getInstance());

        resCategoryHome.setId(storeResHome.getInstance().getRes().getResCategory().getId());
        createBy = CreateBy.STORE_RES;
    }

    public void resChange() {
        if ((editingItem == null) || (!editingItem.getRes().equals(resHome.getInstance()))) {
            editingItem = createInstance(resHome.getInstance());
            createBy = CreateBy.RES_CATEGORY;
        }
    }

    public E getEditingItem() {
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

    public enum CreateBy{
       RES_CATEGORY,RES,STORE_RES;
    }
}
