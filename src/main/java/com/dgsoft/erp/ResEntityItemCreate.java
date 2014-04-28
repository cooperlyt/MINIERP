package com.dgsoft.erp;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.StatusMessage;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 09/04/14
 * Time: 16:52
 */
@Scope(ScopeType.CONVERSATION)
public abstract class ResEntityItemCreate<E extends StoreResEntity> {


    private E editingItem;

    protected abstract E createInstance(Res res);

    protected abstract E createInstance(StoreRes storeRes);

    @In(create = true)
    private ResHome resHome;
    @In(create = true)
    protected StoreResHome storeResHome;
    @In(required = false)
    private ResCategoryHome resCategoryHome;

    private CreateBy createBy;
    @In(create = true)
    private ResLocate resLocate;

    private void setResCategory(ResCategory resCategory){
        if (resCategory.isRoot()){
            resCategoryHome.setId(resCategory.getId());
        }else{
            setResCategory(resCategory.getResCategory());
        }

    }

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
        setResCategory(resHome.getInstance().getResCategory());
        createBy = CreateBy.RES;
    }

    public void storeResSelected() {
        resHome.setId(storeResHome.getInstance().getRes().getId());
        editingItem = createInstance(storeResHome.getInstance());
        setResCategory(storeResHome.getInstance().getRes().getResCategory());
        createBy = CreateBy.STORE_RES;
    }

    public void resChange() {
        if ((editingItem == null) || (!editingItem.getRes().equals(resHome.getInstance()))) {
            editingItem = createInstance(resHome.getInstance());
            createBy = CreateBy.RES_CATEGORY;
        }
    }

    @BypassInterceptors
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
        if (storeResHome.isIdDefined()){
            storeResSelected();
            return;
        }

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
