package com.dgsoft.erp.action.store;

import com.dgsoft.erp.action.OtherStoreChangeHome;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreChange;
import org.eclipse.birt.chart.extension.render.Stock;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("otherStockOut")
@Scope(ScopeType.CONVERSATION)
public class OtherStockOut extends StoreOutAction {

    @In
    private OtherStoreChangeHome otherStoreChangeHome;

    @Override
    protected String storeOut() {
        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setStoreChange(otherStoreChangeHome.getInstance());
        otherStoreChangeHome.getInstance().setStockChange(stockChangeHome.getReadyInstance());

        if ("persisted".equals(otherStoreChangeHome.persist())){
            return "OtherStockChangeComplete";
        }else{
            return null;
        }

    }

    @Override
    protected String beginStoreOut() {
        return "BeginOtherStockOut";
    }

    public String complete(){
        return super.storeChange(true);
    }

    @DataModel(value = "otherStockOutItems")
    public List<StockChangeItem> getStoreOutItems(){
        return storeOutItems;
    }

    @DataModelSelection
    private StockChangeItem stockChangeItem;

    @Override
    protected StockChangeItem getSelectOutItem() {
        return stockChangeItem;
    }

    public String cancel() {
        storeOutItems.clear();
        stockChangeHome.clearInstance();
        return "OtherStockChangeCancel";
    }
}
