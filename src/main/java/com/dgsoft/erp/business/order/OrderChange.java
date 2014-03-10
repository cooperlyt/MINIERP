package com.dgsoft.erp.business.order;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.store.StoreChangeItem;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import javax.faces.event.ValueChangeEvent;
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
            result = result.add(item.getTotalPrice());
        }
        return result;
    }

    public BigDecimal getOrderResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;

        for (NeedRes needRes: orderHome.getInstance().getNeedReses()){
            if (!needRes.getId().equals(orderHome.getLastNeedRes().getId())){
                for(OrderItem orderItem: needRes.getOrderItems()){
                    result = result.add(orderItem.getTotalPrice());
                }
            }
        }


        for (OrderItem item : newOrderItems) {
            result = result.add(item.getTotalPrice());
        }

        if (reSend){
            for (OrderItem item: orderReSenderCreate.getReSendOrderItems()){
                result = result.add(item.getTotalPrice());
            }
        }
        return result;
    }

    public void calcByRate() {
        orderTotalMoney = DataFormat.halfUpCurrency(getOrderResTotalMoney().multiply(
                orderRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void calcByOrderTotalMoney() {
        orderRebate = orderTotalMoney.divide(getOrderResTotalMoney(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
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
    protected void initOrderTask() {

        //----- match new OrderItems
        StoreResCountGroup storeOutItems = new StoreResCountGroup();
        newOrderItems = new ArrayList<OrderItem>();

        //List<StoreRes> matchItems = new ArrayList<StoreRes>();
        for (Dispatch dispatch : orderHome.getLastNeedRes().getDispatches()) {
            storeOutItems.putAll(dispatch.getStockChange().getStockChangeItemList());
        }

        for (StoreResCount item: storeOutItems.getStoreResCountList()){
            OrderItem oldOrderItem  = orderHome.getFirstResOrderItem(item.getStoreRes());
            if (oldOrderItem != null){
                newOrderItems.add(new OrderItem(orderHome.getLastNeedRes(), item.getStoreRes(), oldOrderItem.getResUnit(),
                        item.getMasterCount(), oldOrderItem.getMoney(), oldOrderItem.getRebate(),""));
            }else{
                newOrderItems.add(new OrderItem(orderHome.getLastNeedRes(), item.getStoreRes(), item.getStoreRes().getRes().getResUnitByOutDefault(),
                        item.getMasterCount(), BigDecimal.ZERO, new BigDecimal("100"),""));
            }
        }



//        List<OrderItem> oldMatchOrderItems = new ArrayList<OrderItem>();
//        List<OrderItem> oldOrderItem = new ArrayList<OrderItem>(orderHome.getLastNeedRes().getOrderItems());
//
//        for (Map.Entry<StoreRes, StoreResCountEntity> entry : storeOutItems.entrySet()) {
//            oldMatchOrderItems.clear();
//            StoreResCountEntity count = new StoreResCount(entry.getKey(),BigDecimal.ZERO);
//            for (OrderItem item : oldOrderItem) {
//
//                    if (item.getStoreRes().equals(entry.getKey())){
//                        count.add(item);
//                        oldMatchOrderItems.add(item);
//                    }
//
//            }
//            if (count.getMasterCount().compareTo(entry.getValue().getMasterCount()) == 0) {
//                matchItems.add(entry.getKey());
//
//                for (OrderItem oldItem : oldMatchOrderItems) {
//                    newOrderItems.add(oldItem.cloneNew());
//                }
//                oldOrderItem.removeAll(oldMatchOrderItems);
//            }
//
//
//        }
//
//        for (StoreRes storeRes : matchItems) {
//            storeOutItems.remove(storeRes);
//        }
//
//        matchItems.clear();
//        for (Map.Entry<StoreRes, StoreResCountEntity> entry : storeOutItems.entrySet()) {
//
//            OrderItem matchItem = null;
//            int orderContainCount = 0;
//            for (OrderItem item : oldOrderItem) {
//
//                    if (entry.getKey().equals(item.getStoreRes())) {
//                        orderContainCount++;
//                        if (orderContainCount == 1) {
//                            matchItem = item;
//                        } else {
//                            matchItem = null;
//                            break;
//                        }
//                    }
//
//
//            }
//            if (matchItem != null) {
//                matchItems.add(matchItem.getStoreRes());
//                OrderItem newItem = matchItem.cloneNew();
//                newItem.setCount(entry.getValue().getMasterCount());
//                newOrderItems.add(newItem);
//            }
//            oldOrderItem.remove(matchItem);
//        }
//
//        for (StoreRes storeRes : matchItems) {
//            storeOutItems.remove(storeRes);
//        }

//        for (Map.Entry<StoreRes, StoreResCountEntity> entry : storeOutItems.entrySet()) {
//            newOrderItems.add(new OrderItem(orderHome.getLastNeedRes(), entry.getKey(), entry.getKey().getRes().getResUnitByOutDefault(),
//                    entry.getValue().getMasterCount(), BigDecimal.ZERO, new BigDecimal("100"),""));
//        }


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
            OrderItem oldOrderItem  = orderHome.getFirstResOrderItem(overlyOut.getStoreRes());


            OrderItem newItem = new OrderItem();
            newItem.setStoreRes(overlyOut.getStoreRes());



            if (oldOrderItem != null){
                newItem.setResUnit(oldOrderItem.getResUnit());
                newItem.setRebate(oldOrderItem.getRebate());
                newItem.setMoney(oldOrderItem.getMoney());
            }else{
                newItem.setResUnit(overlyOut.getStoreRes().getRes().getResUnitByOutDefault());
                newItem.setRebate(new BigDecimal("100"));
                newItem.setMoney(BigDecimal.ZERO);
            }
            newItem.setCount(overlyOut.getMasterCount());
            reSenderOrderItems.add(newItem);
        }

        orderReSenderCreate.init(reSenderOrderItems);

        //-----------------------

        orderRebate = orderHome.getInstance().getTotalRebate();
        calcByRate();

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
