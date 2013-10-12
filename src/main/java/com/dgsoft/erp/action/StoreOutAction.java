package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.*;
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

    @In
    private InventoryList inventoryList;

    @In
    private FacesMessages facesMessages;

    @In
    private org.jboss.seam.security.Credentials credentials;

    @In
    private RunParam runParam;

    @In
    private NumberBuilder numberBuilder;

    private List<StoreOutItem> storeOutItems = new ArrayList<StoreOutItem>();

    private String selectInventoryId;

    private Store selectStore;

    private BigDecimal outCount;

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

    public BigDecimal getOutCount() {
        return outCount;
    }

    public void setOutCount(BigDecimal outCount) {
        this.outCount = outCount;
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
                result.add(new StoreOutItemGroup(storeRes.getTitle(), storeResGroup.get(storeRes)));
            }

        }
        return result;
    }

    @Override
    public void clearInstance() {
        super.clearInstance();
        selectStore = null;
        storeOutItems.clear();
        memo = null;
        storeOutDate = null;
        outCount = null;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void beginStoreIn() {
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
    public String StoreOut() {
        if (isIdAvailable(getInstance().getId())) {


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
        boolean added = false;
        for (StoreOutItem outItem : storeOutItems) {
            if (outItem.sameInventory(inventory)) {
                outItem.setCount(outItem.getCount().add(outCount));
                added = true;
            }
        }
        if (!added) {
            storeOutItems.add(new StoreOutItem(outCount, inventory));
        }
        outCount = new BigDecimal(0);
    }

    public void removeItem() {
        for (StoreOutItem outItem : storeOutItems) {
            if (selectInventoryId.equals(outItem.getInventory().getId())) {
                storeOutItems.remove(outItem);
                return;
            }
        }
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

        public int getTotalCount(){
            int result = 0;
            for (StoreOutItem storeOutItem: items){
                result += storeOutItem.getCount().toBigInteger().intValue();
            }
            return result;
        }
    }

    public static class StoreOutItem {

        private BigDecimal count;

        private Inventory inventory;

        public StoreOutItem(BigDecimal count, Inventory inventory) {
            this.count = count;
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
