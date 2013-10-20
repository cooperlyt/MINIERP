package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/4/13
 * Time: 10:39 AM
 */
@Name("storeInAction")
@Scope(ScopeType.CONVERSATION)
public class StoreInAction  {

//    @In(create = true)
//    private StoreAreaHome storeAreaHome;
//
//    @In(required = false)
//    private StoreResFormatFilter storeResFormatFilter;
//
//    @In(required = false)
//    private ResLocateHome resLocateHome;
//
//    @In(create = true)
//    private StoreResHome storeResHome;
//
//    @In
//    private FacesMessages facesMessages;
//
//    @In
//    private Credentials credentials;
//
//    @In
//    private RunParam runParam;
//
//    @In
//    private NumberBuilder numberBuilder;
//
//    private Store selectStore;
//
//    private BigDecimal resCount;
//
//    private Date storeInDate;
//
//    private String memo;
//
//    @DataModel(value = "storeInItems")
//    private List<StoreInItem> storeInItems = new ArrayList<StoreInItem>();
//
//    @DataModelSelection
//    private StoreInItem selectedStoreInItem;
//
//    public Store getSelectStore() {
//        return selectStore;
//    }
//
//    public void setSelectStore(Store selectStore) {
//        this.selectStore = selectStore;
//        if ((selectStore != null) && (selectStore.getStoreAreas().size() == 1)) {
//            StoreArea area = selectStore.getStoreAreas().iterator().next();
//            if (area.getStoreAreas().isEmpty()) {
//                storeAreaHome.setId(area.getId());
//                return;
//            }
//        }
//        storeAreaHome.clearInstance();
//    }
//
//    public BigDecimal getResCount() {
//        return resCount;
//    }
//
//    public void setResCount(BigDecimal resCount) {
//        this.resCount = resCount;
//    }
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//    public Date getStoreInDate() {
//        return storeInDate;
//    }
//
//    public void setStoreInDate(Date storeInDate) {
//        this.storeInDate = storeInDate;
//    }
//
//    @Override
//    public void clearInstance(){
//        super.clearInstance();
//        selectStore = null;
//        storeInItems.clear();
//        memo = null;
//        storeInDate = null;
//        resCount = null;
//    }
//
//    public void addItem() {
//        if (!storeAreaHome.isIdDefined()) {
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "pleaseSelectStoreArea");
//            return;
//        }
//
//        StoreInItem newItem = new StoreInItem(storeAreaHome.getInstance(), storeResFormatFilter.getRes(),
//                storeResFormatFilter.getResFormatList(), resCount);
//
//        for (StoreInItem storeInItem : storeInItems) {
//            if (storeInItem.sameItem(newItem)) {
//                storeInItem.setCount(storeInItem.getCount().add(resCount));
//                newItem = null;
//                break;
//            }
//        }
//
//        if (newItem != null) {
//            storeInItems.add(newItem);
//        }
//
//        resCount = new BigDecimal(0);
//        resLocateHome.clearInstance();
//    }
//
//    public void removeItem() {
//        storeInItems.remove(selectedStoreInItem);
//    }
//
//    public boolean isIdAvailable(String newId) {
//        return getEntityManager().createQuery("select si from StoreIn si where si.id = ?1").setParameter(1, newId).getResultList().size() == 0;
//    }
//
////    @End
////    @Transactional
////    public String storeIn() {
////        if (isIdAvailable(getInstance().getId())) {
////
////            getInstance().setStockChange(new StockChange(selectStore, storeInDate, credentials.getUsername(), StockChange.StoreChangeType.STORE_IN, memo));
////            for (StoreInItem storeInItem : storeInItems) {
////                storeResHome.setRes(storeInItem.getRes(), storeInItem.getFormats());
////                StockChangeItem stockChangeItem = new StockChangeItem(getInstance().getStockChange(), storeResHome.getInstance(),storeInItem.getCount() , false);
////                if (storeResHome.isIdDefined()) {
////                    List<Stock> inventories = getEntityManager().createQuery("select inventory from Stock inventory where inventory.storeRes.id = :storeResId and inventory.storeArea.id= :storeAreaId")
////                            .setParameter("storeResId", storeResHome.getInstance().getId())
////                            .setParameter("storeAreaId", storeInItem.getStoreArea().getId()).getResultList();
////                    if (!inventories.isEmpty()) {
////                        if (inventories.size() > 1) {
////                            log.warn("StoreIn inventory Repeat StoreRes ID:" + storeResHome.getInstance().getId());
////                        }
////                        stockChangeItem.setStock(inventories.get(0));
////                        stockChangeItem.setBefortCount(stockChangeItem.getStock().getCount());
////                        stockChangeItem.getStock().setCount(stockChangeItem.getStock().getCount().add(storeInItem.getCount()));
////                        stockChangeItem.setAfterCount(stockChangeItem.getStock().getCount());
////                    }
////                }
////                if (stockChangeItem.getStock() == null){
////                    stockChangeItem.setStock(new Stock(storeResHome.getInstance(), storeInItem.getStoreArea(), storeInItem.getCount()));
////                    stockChangeItem.setBefortCount(new BigDecimal(0));
////                    stockChangeItem.setAfterCount(storeInItem.getCount());
////                }
////
////                getInstance().getStockChange().getStockChangeItems().add(stockChangeItem);
////            }
////            persist();
////            clearInstance();
////            return getLastState();
////        } else {
////            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeInIDIsExists", getInstance().getId());
////            return null;
////        }
////
////    }
//
//    @Begin(flushMode = FlushModeType.MANUAL)
//    public void beginStoreIn() {
//        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
//            getInstance().setId(numberBuilder.getDateNumber("storeInCode"));
//        }
//    }
//
//    public static class StoreInItem {
//
//        private StoreArea storeArea;
//
//        private Res res;
//
//        private List<Format> formats;
//
//        private BigDecimal count;
//
//        public StoreInItem(StoreArea storeArea, Res res, List<Format> formats, BigDecimal count) {
//            this.storeArea = storeArea;
//            this.res = res;
//            this.formats = formats;
//            this.count = count;
//        }
//
//        public StoreArea getStoreArea() {
//            return storeArea;
//        }
//
//        public void setStoreArea(StoreArea storeArea) {
//            this.storeArea = storeArea;
//        }
//
//        public Res getRes() {
//            return res;
//        }
//
//        public void setRes(Res res) {
//            this.res = res;
//        }
//
//        public List<Format> getFormats() {
//            return formats;
//        }
//
//        public BigDecimal getCount() {
//            return count;
//        }
//
//        public void setCount(BigDecimal count) {
//            this.count = count;
//        }
//
//        public boolean sameItem(StoreInItem storeInItem) {
//            return (storeArea.getId().equals(storeInItem.getStoreArea().getId()) &&
//                    res.getId().equals(storeInItem.getRes().getId()) &&
//                    ResHelper.sameFormat(storeInItem.getFormats(), formats)
//            );
//        }
//    }

}
