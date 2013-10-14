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
public class StoreOutAction extends ErpEntityHome<StoreOut> {

    @In(create = true)
    private InventoryList inventoryList;

    @In
    private FacesMessages facesMessages;

    @In
    private org.jboss.seam.security.Credentials credentials;

    @In
    private RunParam runParam;

    @In
    private NumberBuilder numberBuilder;

    @In
    private ResHelper resHelper;


    @DataModel(value = "storeOutItems")
    private List<StoreOutItem> storeOutItems = new ArrayList<StoreOutItem>();

    @DataModelSelection
    private StoreOutItem selectStoreOutItem;

    private String selectInventoryId;

    private Store selectStore;

    private Date storeOutDate;

    private String memo;

    private boolean groupByRes = true;

    public Store getSelectStore() {
        return selectStore;
    }

    public void setSelectStore(Store selectStore) {
        this.selectStore = selectStore;
    }


    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getStoreOutDate() {
        return storeOutDate;
    }

    public void setStoreOutDate(Date storeOutDate) {
        this.storeOutDate = storeOutDate;
    }


    public String getSelectInventoryId() {
        return selectInventoryId;
    }

    public void setSelectInventoryId(String selectInventoryId) {
        this.selectInventoryId = selectInventoryId;
    }

    public boolean isGroupByRes() {
        return groupByRes;
    }

    public void setGroupByRes(boolean groupByRes) {
        this.groupByRes = groupByRes;
    }

    public List<StoreOutItemGroup> getStoreOutItemGroups() {
        List<StoreOutItemGroup> result = new ArrayList<StoreOutItemGroup>();
        if (groupByRes) {
            Map<Res, List<StoreOutItem>> resGroup = new HashMap<Res, List<StoreOutItem>>();
            for (StoreOutItem storeOutItem : storeOutItems) {
                List<StoreOutItem> temp = resGroup.get(storeOutItem.getInventory().getStoreRes().getRes());
                if (temp == null) {
                    temp = new ArrayList<StoreOutItem>();
                    resGroup.put(storeOutItem.getInventory().getStoreRes().getRes(), temp);
                }
                temp.add(storeOutItem);
            }
            for (Res res : resGroup.keySet()) {
                result.add(new StoreOutItemGroup(res.getName() + "(" + res.getCode() + ")", resGroup.get(res)));
            }
        } else {
            Map<StoreRes, List<StoreOutItem>> storeResGroup = new HashMap<StoreRes, List<StoreOutItem>>();
            for (StoreOutItem storeOutItem : storeOutItems) {
                List<StoreOutItem> temp = storeResGroup.get(storeOutItem.getInventory().getStoreRes());
                if (temp == null) {
                    temp = new ArrayList<StoreOutItem>();
                    storeResGroup.put(storeOutItem.getInventory().getStoreRes(), temp);
                }
                temp.add(storeOutItem);
            }
            for (StoreRes storeRes : storeResGroup.keySet()) {
                result.add(new StoreOutItemGroup(resHelper.generateStoreResTitle(storeRes), storeResGroup.get(storeRes)));
            }

        }
        return result;
    }


    @Observer("erp.resLocateSelected")
    public void resSelectedListener() {
        inventoryList.first();
    }

    @Override
    public void clearInstance() {
        super.clearInstance();
        selectStore = null;
        storeOutItems.clear();
        memo = null;
        storeOutDate = null;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void beginStoreOut() {
        inventoryList.setStore(selectStore);
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreOutCode")) {
            getInstance().setId(numberBuilder.getDateNumber("storeOutCode"));
        }
    }

    public boolean isIdAvailable(String newId) {
        return getEntityManager().createQuery("select so from StoreOut so where so.id = ?1").setParameter(1, newId).getResultList().size() == 0;
    }

    @End
    @Transactional
    public String storeOut() {
        if (isIdAvailable(getInstance().getId())) {


            getInstance().setStoreChange(new StoreChange(selectStore, storeOutDate, credentials.getUsername(), StoreChange.StoreChangeType.STORE_IN, memo));
            for (StoreOutItem storeInItem : storeOutItems) {

                StoreChangeItem storeChangeItem = new StoreChangeItem(getInstance().getStoreChange(), storeInItem.getInventory(), storeInItem.getCount(),true);


                getInstance().getStoreChange().getStoreChangeItems().add(storeChangeItem);

                storeInItem.getInventory().setCount(storeChangeItem.getAfterCount());
            }

            persist();
            clearInstance();
            return getLastState();
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeOutIDIsExists", getInstance().getId());
            return null;
        }
    }

    public void addItem() {
        Inventory inventory = getEntityManager().find(Inventory.class, selectInventoryId);
        for (StoreOutItem outItem : storeOutItems) {
            if (outItem.sameInventory(inventory)) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "inventoryInStoreOut", resHelper.generateStoreResTitle(outItem.getInventory().getStoreRes()));
                return;
            }
        }

        storeOutItems.add(new StoreOutItem(inventory));

    }

    public void removeItem() {
        storeOutItems.remove(selectStoreOutItem);
    }


    public boolean inventoryInOrder(String inventoryId){
         for (StoreOutItem item: storeOutItems){
             if (item.getInventory().getId().equals(inventoryId)){
                 return true;
             }
         }
        return false;
    }


    public static class StoreOutItemGroup {

        private String title;

        private List<StoreOutItem> items;


        public StoreOutItemGroup(String title, List<StoreOutItem> items) {
            this.title = title;
            this.items = items;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<StoreOutItem> getItems() {
            return items;
        }

        public void setItems(List<StoreOutItem> items) {
            this.items = items;
        }

        public int getTotalCount() {
            int result = 0;
            for (StoreOutItem storeOutItem : items) {
                result += storeOutItem.getCount().toBigInteger().intValue();
            }
            return result;
        }
    }

    public static class StoreOutItem {

        private BigDecimal count;

        private Inventory inventory;

        public StoreOutItem(Inventory inventory) {
            this.count = new BigDecimal(0);
            this.inventory = inventory;
        }

        public BigDecimal getCount() {
            return count;
        }

        public void setCount(BigDecimal count) {
            this.count = count;
        }

        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        public boolean sameInventory(Inventory other) {
            if (other == null || inventory == null) {
                return false;
            }
            return other.getId().equals(inventory.getId());
        }
    }

}
