package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import javax.faces.event.ValueChangeEvent;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/4/14
 * Time: 2:36 PM
 */
@Name("orderChange")
public class OrderChange extends OrderTaskHandle {

    @In(create = true)
    private OrderReSenderCreate orderReSenderCreate;

    @DataModel(value = "newOrderItems")
    private List<OrderItem> newOrderItems;

    @DataModelSelection
    private OrderItem selectOrderItem;

    @In(create = true)
    private OrderItemSplit orderItemSplit;

    private List<OverlyOut> addOverlyOutItems;

    private List<OverlyOut> subOverlyOutItems;


    private BigDecimal orderRebate;

    private BigDecimal orderTotalMoney;

    private boolean reSend;

    public boolean isReSend() {
        return reSend;
    }

    public void setReSend(boolean reSend) {
        this.reSend = reSend;
    }

    public BigDecimal getOrderTotalMoney() {
        return orderTotalMoney;
    }

    public void setOrderTotalMoney(BigDecimal orderTotalMoney) {
        this.orderTotalMoney = orderTotalMoney;
    }

    public BigDecimal getOrderRebate() {
        return orderRebate;
    }

    public void setOrderRebate(BigDecimal orderRebate) {
        this.orderRebate = orderRebate;
    }

    public BigDecimal getNewItemTotalMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item: newOrderItems){
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getOrderResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;

        for (NeedRes needRes: orderHome.getInstance().getNeedReses()){
            if (!needRes.getId().equals(orderHome.getLastNeedRes().getId())){
                for(OrderItem orderItem: needRes.getOrderItems()){
                    result = result.add(orderItem.getTotalMoney());
                }
            }
        }


        for (OrderItem item : newOrderItems) {
            result = result.add(item.getTotalMoney());
        }

        if (reSend){
            for (OrderItem item: orderReSenderCreate.getReSendOrderItems()){
                result = result.add(item.getTotalMoney());
            }
        }
        return result;
    }

    public void calcByRate() {
        orderTotalMoney = BigDecimalFormat.halfUpCurrency(getOrderResTotalMoney().multiply(
                orderRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void calcByOrderTotalMoney() {
        orderRebate = orderTotalMoney.divide(getOrderResTotalMoney(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
    }

    public void itemUnitChange(ValueChangeEvent event) {
        ResUnit oldUnit = (ResUnit) event.getOldValue();
        ResUnit newUnit = (ResUnit) event.getNewValue();
        if (!oldUnit.getId().equals(newUnit.getId())){
            selectOrderItem.generateResCount();
            selectOrderItem.setCount(selectOrderItem.getStoreResCount().
                    getCountByResUnit(newUnit));
        }
    }

    public void beginSplitOrderItem(){
        orderItemSplit.beginSplit(newOrderItems,selectOrderItem);
    }

    public List<OverlyOut> getSubOverlyOutItems() {
        return subOverlyOutItems;
    }

    public void setSubOverlyOutItems(List<OverlyOut> subOverlyOutItems) {
        this.subOverlyOutItems = subOverlyOutItems;
    }

    public List<OverlyOut> getAddOverlyOutItems() {
        return addOverlyOutItems;
    }

    public void setAddOverlyOutItems(List<OverlyOut> addOverlyOutItems) {
        this.addOverlyOutItems = addOverlyOutItems;
    }

    @Override
    protected String initOrderTask() {

        //----- match new OrderItems
        Map<StoreRes, ResCount> storeOutItems = new HashMap<StoreRes, ResCount>();
        newOrderItems = new ArrayList<OrderItem>();

        List<StoreRes> matchItems = new ArrayList<StoreRes>();
        for (Dispatch dispatch : orderHome.getLastNeedRes().getDispatches()) {

            for (StockChangeItem item : dispatch.getStockChange().getStockChangeItemList()) {
                StoreRes storeRes = item.getStoreRes();
                ResCount count = storeOutItems.get(storeRes);
                if (count == null) {
                    storeOutItems.put(storeRes, item.getResCount());
                } else {
                    count.add(item.getResCount());
                }

            }
        }

        List<OrderItem> oldMatchOrderItems = new ArrayList<OrderItem>();
        List<OrderItem> oldOrderItem = new ArrayList<OrderItem>(orderHome.getLastNeedRes().getOrderItems());

        for (Map.Entry<StoreRes, ResCount> entry : storeOutItems.entrySet()) {
            oldMatchOrderItems.clear();
            ResCount count = entry.getKey().getResCount(BigDecimal.ZERO);
            for (OrderItem item : oldOrderItem) {
                if (item.isStoreResItem()) {
                    if (item.getStoreRes().equals(entry.getKey())){
                        count.add(item.getStoreResCount());
                        oldMatchOrderItems.add(item);
                    }
                } else {
                    //TODO res item
                }
            }
            if (count.getMasterCount().compareTo(entry.getValue().getMasterCount()) == 0) {
                matchItems.add(entry.getKey());

                for (OrderItem oldItem : oldMatchOrderItems) {
                    newOrderItems.add(oldItem.cloneNew());
                }
                oldOrderItem.removeAll(oldMatchOrderItems);
            }


        }

        for (StoreRes storeRes : matchItems) {
            storeOutItems.remove(storeRes);
        }

        matchItems.clear();
        for (Map.Entry<StoreRes, ResCount> entry : storeOutItems.entrySet()) {

            OrderItem matchItem = null;
            int orderContainCount = 0;
            for (OrderItem item : oldOrderItem) {
                if (item.isStoreResItem()) {
                    if (entry.getKey().equals(item.getStoreRes())) {
                        orderContainCount++;
                        if (orderContainCount == 1) {
                            matchItem = item;
                        } else {
                            matchItem = null;
                            break;
                        }
                    }
                } else {
                    //TODO res item
                }

            }
            if (matchItem != null) {
                matchItems.add(matchItem.getStoreRes());
                OrderItem newItem = matchItem.cloneNew();
                newItem.setCount(entry.getValue().getCountByResUnit(newItem.getMoneyUnit()));
                newOrderItems.add(newItem);
            }
            oldOrderItem.remove(matchItem);
        }

        for (StoreRes storeRes : matchItems) {
            storeOutItems.remove(storeRes);
        }

        for (Map.Entry<StoreRes, ResCount> entry : storeOutItems.entrySet()) {
            newOrderItems.add(new OrderItem(orderHome.getLastNeedRes(), entry.getKey(), BigDecimal.ZERO, entry.getKey().getRes().getResUnitByOutDefault(),
                    entry.getValue().getCountByResUnit(entry.getKey().getRes().getResUnitByOutDefault()), BigDecimal.ZERO, new BigDecimal("100")));
        }


        //-----------------------

        addOverlyOutItems = new ArrayList<OverlyOut>();
        subOverlyOutItems = new ArrayList<OverlyOut>();

        for (Dispatch dispatch : orderHome.getLastNeedRes().getDispatches()) {
            for (OverlyOut overlyOut : dispatch.getOverlyOuts()) {
                if (overlyOut.isAdd()) {
                    addOverlyOutItems.add(overlyOut);
                } else {
                    subOverlyOutItems.add(overlyOut);
                }
            }
        }

        //------------------------

        reSend = true;
        List<OrderItem> reSenderOrderItems = new ArrayList<OrderItem>();

        for(OverlyOut overlyOut: subOverlyOutItems){
            OrderItem matchOrderItem = null;
            int matchCount = 0;
            for(OrderItem orderItem: orderHome.getLastNeedRes().getOrderItems()){
                if (orderItem.isStoreResItem()) {
                    if (overlyOut.getStoreRes().equals(orderItem.getStoreRes())){
                        matchCount ++;
                        if (matchCount == 1){
                            matchOrderItem = orderItem;
                        }else{
                            matchOrderItem = null;
                            break;
                        }
                    }
                }else{
                    //TODO other
                }

            }

            OrderItem newItem = new OrderItem();
            newItem.setStoreRes(overlyOut.getStoreRes());
            newItem.setCost(BigDecimal.ZERO);
            newItem.setStoreResItem(true);


            if (matchOrderItem != null){
                newItem.setMoneyUnit(matchOrderItem.getMoneyUnit());
                newItem.setRebate(matchOrderItem.getRebate());
                newItem.setMoney(matchOrderItem.getMoney());
            }else{
                newItem.setMoneyUnit(overlyOut.getStoreRes().getRes().getResUnitByOutDefault());
                newItem.setRebate(new BigDecimal("100"));
                newItem.setMoney(BigDecimal.ZERO);
            }
            newItem.setCount(overlyOut.getResCount().getCountByResUnit(newItem.getMoneyUnit()));
            reSenderOrderItems.add(newItem);
        }

        orderReSenderCreate.init(reSenderOrderItems);

        //-----------------------

        orderRebate = orderHome.getInstance().getTotalRebate();
        calcByRate();
        return "success";

    }

    @Override
    protected String completeOrderTask() {

        orderHome.getLastNeedRes().getOrderItems().clear();
        orderHome.getLastNeedRes().getOrderItems().addAll(ResHelper.unionSeamOrderItem(newOrderItems));

        orderHome.getInstance().setTotalRebate(orderRebate);
        orderHome.getInstance().setMoney(orderTotalMoney);



        if (!reSend || subOverlyOutItems.isEmpty()) {
            orderHome.getInstance().setAllStoreOut(true);
        }else{
            orderHome.getInstance().getNeedReses().add(orderReSenderCreate.getReSenderNeedRes());
            orderHome.getInstance().setAllStoreOut(false);
        }

        if ("updated".equals(orderHome.update())){
            return "taskComplete";
        }else{
            return null;
        }

    }

}
