package com.dgsoft.erp.business.order;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.jbpm.TaskDescription;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/13/13
 * Time: 4:32 PM
 */
@Name("orderStoreOut")
public class OrderStoreOut extends OrderTaskHandle {

    public final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    @In
    protected NumberBuilder numberBuilder;

    @In
    private ActionExecuteState actionExecuteState;

    @In
    private org.jboss.seam.security.Credentials credentials;

    @DataModel("orderStoreOutItems")
    private List<OrderStoreOutItem> orderStoreOutItems;

    public List<TotalDataGroup<Res, OrderStoreOutItem, ResCount>> getOutItemGroup() {
        return TotalDataGroup.groupBy(orderStoreOutItems, new ResTotalCount.ResCountGroupStrategy<OrderStoreOutItem>(), new ResTotalCount.FormatCountGroupStrategy<OrderStoreOutItem>());
    }


    @DataModelSelection
    private OrderStoreOutItem selectOutItem;

    private OrderStoreOutItem operOutItem;

    private boolean overlyOut;

    private String memo;

    private Date storeOutDate;

    public Date getStoreOutDate() {
        return storeOutDate;
    }

    public void setStoreOutDate(Date storeOutDate) {
        this.storeOutDate = storeOutDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public OrderStoreOutItem getOperOutItem() {
        return operOutItem;
    }

    public boolean isOverlyOut() {
        return overlyOut;
    }

    public void setOverlyOut(boolean overlyOut) {
        this.overlyOut = overlyOut;
    }

    public void removeItem() {
        if (selectOutItem.getDispatchItem() == null) {
            orderStoreOutItems.remove(selectOutItem);
        }
    }

    public void editOverlay() {
        operOutItem = selectOutItem;
        if (operOutItem.getDispatchItem() == null) {
            overlyOut = true;
        } else {
            overlyOut = false;
        }
        actionExecuteState.clearState();
    }

    public void saveOverlay() {
        if (operOutItem.getMasterCount().compareTo(BigDecimal.ZERO) < 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "sale_store_out_must_less_need", operOutItem.getDispatchItem().getDisplayMasterCount());
            //operOutItem.getOverlyOut().setMasterCount(operOutItem.getDispatchItem().getMasterCount());
            return;
        }
        if (overlyOut) {
            if (operOutItem.getOweOut() != null) {
                operOutItem.getOweOut().setCount(BigDecimal.ZERO);
            }
        } else {
            operOutItem.getOverlayItem().setCount(BigDecimal.ZERO);
        }

        actionExecuteState.actionExecute();
    }


    @Override
    protected String completeOrderTask() {

        dispatchHome.getInstance().setStockChange(
                new StockChange(orderHome.getInstance().getId() + "-" + numberBuilder.getNumber("storeInCode"), dispatchHome.getInstance().getStore(), storeOutDate,
                        credentials.getUsername(), StockChange.StoreChangeType.SELL_OUT, memo, true)
        );

        for (OrderStoreOutItem item : orderStoreOutItems) {
            //TODO noConvertRate Unit


            if (item.getCount().compareTo(BigDecimal.ZERO) > 0) {

                if ((item.getOweOut() != null) && (item.getOweOut().getCount().compareTo(BigDecimal.ZERO) > 0)) {
                    dispatchHome.getInstance().getOweOuts().add(item.getOweOut());
                    item.getDispatchItem().setCount(item.getDispatchItem().getCount().subtract(item.getOweOut().getCount()));

                }

                if (item.getOverlayItem().getCount().compareTo(BigDecimal.ZERO) > 0) {
                    dispatchHome.getInstance().getOrderItems().add(item.getOverlayItem());
                }


            } else if (item.getDispatchItem() != null) {
                dispatchHome.getInstance().getOrderItems().remove(item.getDispatchItem());
                item.getDispatchItem().setDispatch(null);
                item.getDispatchItem().setStatus(OrderItem.OrderItemStatus.CREATED);
            }


            if (item.getStoreRes().getRes().isBatchMgr()) {
                //TODO UseSelectBatch
                //TODO StoreArea Count subtract
            }
        }

        Map<StoreRes,Stock> createStocks = new HashMap<StoreRes, Stock>();

        for (OrderItem item : dispatchHome.getInstance().getOrderItems()) {

            Stock stock = item.getStoreRes().getStock(dispatchHome.getInstance().getStore());

            if (stock == null) {
                stock = createStocks.get(item.getStoreRes());
                if (stock == null) {
                    stock = new Stock(dispatchHome.getInstance().getStore(), item.getStoreRes(), BigDecimal.ZERO);
                    createStocks.put(item.getStoreRes(), stock);
                }
            }

            StockChangeItem stockChangeItem = new StockChangeItem(dispatchHome.getInstance().getStockChange(),
                    stock, item.getMasterCount());


            dispatchHome.getInstance().getStockChange().getStockChangeItems()
                    .add(stockChangeItem);
            stock.setCount(stock.getCount().subtract(item.getMasterCount()));

            item.calcMoney();
            if (item.isOverlyOut()) {
                item.setStatus(OrderItem.OrderItemStatus.WAIT_PRICE);
            } else {
                item.setStatus(OrderItem.OrderItemStatus.COMPLETED);
            }


        }


        dispatchHome.getInstance().setStoreOut(true);

        if (dispatchHome.getInstance().getDeliveryType().equals(Dispatch.DeliveryType.CUSTOMER_SELF)) {
            dispatchHome.getInstance().setSendTime(storeOutDate);
            dispatchHome.getInstance().setFare(BigDecimal.ZERO);
            dispatchHome.getInstance().getNeedRes().getCustomerOrder().setResReceived(true);
        }


        boolean needResComplete = true;
        for (Dispatch dispatch : dispatchHome.getInstance().getNeedRes().getDispatches()) {
            if (!dispatch.isStoreOut()) {
                needResComplete = false;
                break;
            }
        }

        if (needResComplete) {
            dispatchHome.getInstance().getNeedRes().setStatus(NeedRes.NeedResStatus.OUTED);
        }


        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return null;
        }
    }

    @Override
    protected void initOrderTask() {

        String storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }

        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            if (needRes.getStatus().equals(NeedRes.NeedResStatus.DISPATCHED)) {
                for (Dispatch dispatch : needRes.getDispatches()) {
                    if (dispatch.getStore().getId().equals(storeId) &&
                            !dispatch.isStoreOut()) {
                        dispatchHome.setId(dispatch.getId());
                        orderStoreOutItems = new ArrayList<OrderStoreOutItem>();
                        for (OrderItem dispatchItem : dispatchHome.getInstance().getOrderItems()) {
                            //addDispatchItem(dispatchItem);
                            orderStoreOutItems.add(new OrderStoreOutItem(dispatchItem));
                        }

                    }
                }
            }
        }
    }


    @In(create = true)
    private OrderItemCreate orderItemCreate;

    @In(create = true)
    private StoreResHome storeResHome;


    public void addNewOverlyOut() {

        OrderItem newOverlyOut = orderItemCreate.getEditingItem();

        if (newOverlyOut == null) {
            throw new IllegalArgumentException("editingItem state error");
        }


        storeResHome.setRes(newOverlyOut.getRes(), newOverlyOut.getFormats(), newOverlyOut.getFloatConvertRate());


        if (!storeResHome.isIdDefined()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "storeResNotDefine");
            return;
        }

        for (OrderStoreOutItem item : orderStoreOutItems) {
            if (item.getStoreRes().equals(storeResHome.getInstance())) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                        "sale_store_out_item_exists");
                return;
            }
        }

        newOverlyOut.setStoreRes(storeResHome.getInstance());
        newOverlyOut.setOverlyOut(true);
        newOverlyOut.setStatus(OrderItem.OrderItemStatus.DISPATCHED);
        newOverlyOut.setResUnit(storeResHome.getInstance().getRes().getResUnitByOutDefault());
        newOverlyOut.setNeedRes(dispatchHome.getInstance().getNeedRes());
        newOverlyOut.setDispatch(dispatchHome.getInstance());
        newOverlyOut.setPresentation(false);
        newOverlyOut.setRebate(new BigDecimal("100"));
        if (newOverlyOut.getRes().getUnitGroup().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            newOverlyOut.setNeedConvertRate(newOverlyOut.getStoreRes().getFloatConversionRate());
        }


        orderStoreOutItems.add(new OrderStoreOutItem(newOverlyOut));
        orderItemCreate.clear();
    }

    public class OrderStoreOutItem extends StoreResCountEntity implements Serializable {

        private OrderItem dispatchItem;

        private OrderItem overlayItem;

        private OweOut oweOut;

//        public boolean isHaveOverlyOut() {
//            return overlyOut.getMasterCount().compareTo(BigDecimal.ZERO) > 0;
//        }


        public OrderItem getDispatchItem() {
            return dispatchItem;
        }

        public OrderItem getOverlayItem() {
            return overlayItem;
        }

        public OweOut getOweOut() {
            return oweOut;
        }

        public OrderStoreOutItem(OrderItem orderItem) {

            if (orderItem.isOverlyOut()) {
                this.overlayItem = orderItem;
            } else {
                this.dispatchItem = orderItem;
                oweOut = new OweOut(orderItem.getDispatch(), orderItem.getStoreRes(),
                        BigDecimal.ZERO, orderItem.getMemo(), orderItem.getNeedConvertRate());
                overlayItem = new OrderItem(orderItem.getDispatch(), orderItem.getStoreRes());
            }

        }

        public Stock getStock() {
            return getStoreRes().getStock(
                    (dispatchItem == null) ? overlayItem.getDispatch().getStore() : dispatchItem.getDispatch().getStore());
        }

        public boolean isEnough() {
            Stock stock = getStock();
            if (stock == null) {
                return false;
            } else
                return getStock().getCount().compareTo(getMasterCount()) >= 0;
        }


        public StoreResCount getDisparity() {
            Stock stock = getStock();
            if (stock == null) {
                return new StoreResCount(getStoreRes(), getMasterCount());
            } else
                return new StoreResCount(getStoreRes(), getMasterCount().subtract(getStock().getCount()));
        }

        @Override
        public BigDecimal getCount() {
            BigDecimal masterCount;
            if (dispatchItem != null) {
                masterCount = dispatchItem.getCount();
            } else {
                masterCount = BigDecimal.ZERO;
            }
            if ((oweOut != null) && (oweOut.getCount().compareTo(BigDecimal.ZERO) > 0)) {
                masterCount = masterCount.subtract(oweOut.getMasterCount());
            }

            if (overlayItem.getCount().compareTo(BigDecimal.ZERO) > 0) {
                masterCount = masterCount.add(overlayItem.getCount());
            }

            return masterCount;
        }

        @Override
        public void setCount(BigDecimal count) {
            throw new IllegalArgumentException("this is readonly");
        }

        @Override
        public StoreRes getStoreRes() {
            if (dispatchItem == null) {
                return overlayItem.getStoreRes();
            } else
                return dispatchItem.getStoreRes();
        }

        @Override
        public void setStoreRes(StoreRes storeRes) {
            throw new IllegalArgumentException("this is readonly");
        }

        public void reset() {
            if (dispatchItem != null) {
                oweOut.setCount(BigDecimal.ZERO);
                overlayItem.setCount(BigDecimal.ZERO);
            }
        }
    }


}
