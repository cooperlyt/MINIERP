package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/9/13
 * Time: 8:56 AM
 */
@Name("storeOutAction")
public class StoreOutAction  {

//    @In(create = true)
//    private StockList stockList;
//
//    @In
//    private FacesMessages facesMessages;
//
//    @In
//    private org.jboss.seam.security.Credentials credentials;
//
//    @In
//    private RunParam runParam;
//
//    @In
//    private NumberBuilder numberBuilder;
//
//    @In
//    private ResHelper resHelper;
//
//
//    @DataModel(value = "storeOutItems")
//    private List<StoreOutItem> storeOutItems = new ArrayList<StoreOutItem>();
//
//    @DataModelSelection
//    private StoreOutItem selectStoreOutItem;
//
//    private String selectInventoryId;
//
//    private Store selectStore;
//
//    private Date storeOutDate;
//
//    private String memo;
//
//    private boolean groupByRes = true;
//
//    public Store getSelectStore() {
//        return selectStore;
//    }
//
//    public void setSelectStore(Store selectStore) {
//        this.selectStore = selectStore;
//    }
//
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//    public Date getStoreOutDate() {
//        return storeOutDate;
//    }
//
//    public void setStoreOutDate(Date storeOutDate) {
//        this.storeOutDate = storeOutDate;
//    }
//
//
//    public String getSelectInventoryId() {
//        return selectInventoryId;
//    }
//
//    public void setSelectInventoryId(String selectInventoryId) {
//        this.selectInventoryId = selectInventoryId;
//    }
//
//    public boolean isGroupByRes() {
//        return groupByRes;
//    }
//
//    public void setGroupByRes(boolean groupByRes) {
//        this.groupByRes = groupByRes;
//    }
//

//
//
//    @Observer("erp.resLocateSelected")
//    public void resSelectedListener() {
//        stockList.first();
//    }
//
//    @Override
//    public void clearInstance() {
//        super.clearInstance();
//        selectStore = null;
//        storeOutItems.clear();
//        memo = null;
//        storeOutDate = null;
//    }
//
//    @Begin(flushMode = FlushModeType.MANUAL)
//    public void beginStoreOut() {
//        stockList.setStore(selectStore);
//        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
//            getInstance().setId(numberBuilder.getDateNumber("storeOutCode"));
//        }
//    }
//
//    public boolean isIdAvailable(String newId) {
//        return getEntityManager().createQuery("select so from StoreOut so where so.id = ?1").setParameter(1, newId).getResultList().size() == 0;
//    }
//
//    @End
//    @Transactional
//    public String storeOut() {
//        if (isIdAvailable(getInstance().getId())) {
//
//
//            getInstance().setStockChange(new StockChange(selectStore, storeOutDate, credentials.getUsername(), StockChange.StoreChangeReason.STORE_IN, memo));
//            for (StoreOutItem storeInItem : storeOutItems) {
//
//                StockChangeItem stockChangeItem = new StockChangeItem(getInstance().getStockChange(), storeInItem.getStock(), storeInItem.getCount(),true);
//
//
//                getInstance().getStockChange().getStockChangeItems().add(stockChangeItem);
//
//                storeInItem.getStock().setCount(stockChangeItem.getAfterCount());
//            }
//
//            persist();
//            clearInstance();
//            return getLastState();
//        } else {
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeOutIDIsExists", getInstance().getId());
//            return null;
//        }
//    }
//
//    public void addItem() {
//        Stock stock = getEntityManager().find(Stock.class, selectInventoryId);
//        for (StoreOutItem outItem : storeOutItems) {
//            if (outItem.sameInventory(stock)) {
//                facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "inventoryInStoreOut", resHelper.generateStoreResTitle(outItem.getStock().getStoreRes()));
//                return;
//            }
//        }
//
//        storeOutItems.add(new StoreOutItem(stock));
//
//    }
//
//    public void removeItem() {
//        storeOutItems.remove(selectStoreOutItem);
//    }
//
//
//    public boolean inventoryInOrder(String inventoryId){
//         for (StoreOutItem item: storeOutItems){
//             if (item.getStock().getId().equals(inventoryId)){
//                 return true;
//             }
//         }
//        return false;
//    }
//


}
