package com.dgsoft.erp.business.order;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;

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

//    @DataModel(value = "newOrderItems")
//    private List<OrderItem> newOrderItems;
//
//    @DataModelSelection
//    private OrderItem selectOrderItem;

    private List<OrderItem> overlyItems;

    private List<OrderItem> oweItems;

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

    public BigDecimal getNewItemTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : oweItems) {
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getOrderResTotalMoney() {
        orderHome.calcTotalResMoney();
        BigDecimal result = orderHome.getInstance().getResMoney();
        if (reSend) {
            result = result.add(getNewItemTotalMoney());
        }
        return result;
    }

    private boolean rebateUseMoney = true;

    public boolean isRebateUseMoney() {
        return rebateUseMoney;
    }

    public void setRebateUseMoney(boolean rebateUseMoney) {
        this.rebateUseMoney = rebateUseMoney;
    }

    private BigDecimal totalRebate;

    public BigDecimal getTotalRebate() {
        return totalRebate;
    }

    public void setTotalRebate(BigDecimal totalRebate) {
        this.totalRebate = totalRebate;
    }

    private BigDecimal totalRebateMoney;

    public BigDecimal getTotalRebateMoney() {
        return totalRebateMoney;
    }

    public void setTotalRebateMoney(BigDecimal totalRebateMoney) {
        this.totalRebateMoney = totalRebateMoney;
    }

    public void calcByRate() {

        if (rebateUseMoney) {
            orderTotalMoney = DataFormat.halfUpCurrency(getOrderResTotalMoney().subtract(totalRebateMoney));
        } else
            orderTotalMoney = DataFormat.halfUpCurrency(getOrderResTotalMoney().multiply(
                    totalRebate.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void calcByOrderTotalMoney() {

        totalRebateMoney = getOrderResTotalMoney().subtract(orderTotalMoney);

        totalRebate = orderTotalMoney.divide(getOrderResTotalMoney(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
    }

    public List<OrderItem> getOweItems() {
        return oweItems;
    }

    public List<OrderItem> getOverlyItems() {
        return overlyItems;
    }

    @Override
    protected void initOrderTask() {

        overlyItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.WAIT_PRICE));



        oweItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.CREATED));
        for (OweOut oweOut: orderHome.getNoAddOweItems()){
            OrderItem matchItem = null;
            for(OrderItem orderItem: orderHome.getOrderItemByStatus(
                    EnumSet.of(OrderItem.OrderItemStatus.CREATED,
                            OrderItem.OrderItemStatus.COMPLETED, OrderItem.OrderItemStatus.DISPATCHED))){
                if (orderItem.getStoreRes().equals(oweOut.getStoreRes())){
                    matchItem = orderItem;
                    break;
                }
            }

            OrderItem newItem = new OrderItem(oweOut.getStoreRes(),oweOut.getCount(),false,
                    OrderItem.OrderItemStatus.CREATED,false,oweOut.getDescription(),oweOut.getNeedConvertRate());

            if (matchItem != null){
                newItem.setUseUnit(matchItem.getResUnit());
                newItem.setMoney(matchItem.getMoney());
                newItem.setRebate(matchItem.getRebate());
                newItem.calcMoney();
            }
            if (newItem.getUseUnit() == null){
                newItem.setUseUnit(newItem.getStoreRes().getRes().getResUnitByOutDefault());
            }

            oweItems.add(newItem);
        }

        //------------------------

        reSend = true;

        orderReSenderCreate.init(oweItems);

        //-----------------------

        totalRebate = orderHome.getInstance().getTotalRebate();
        totalRebateMoney = orderHome.getInstance().getTotalRebateMoney();
        calcByRate();

    }

    @Override
    protected String completeOrderTask() {



        calcByOrderTotalMoney();
        orderHome.getInstance().setTotalRebateMoney(totalRebateMoney);
        orderHome.getInstance().setMoney(orderTotalMoney);
        orderHome.getInstance().setResMoney(getOrderResTotalMoney());

        if (!reSend || oweItems.isEmpty()) {
            orderHome.getInstance().setAllStoreOut(true);
        } else {
            if (!orderReSenderCreate.isReady()){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"dispatchNotComplete");
                return null;
            }


            orderHome.getInstance().getNeedReses().add(orderReSenderCreate.getReSenderNeedRes());
            orderHome.getInstance().setAllStoreOut(false);

        }


        for (OrderItem item: overlyItems){
            item.setStatus(OrderItem.OrderItemStatus.COMPLETED);
        }

        for (OweOut oweOut: orderHome.getNoAddOweItems()){
            oweOut.setAdd(true);
        }


        if ("updated".equals(orderHome.update())) {
            return "taskComplete";
        } else {
            return null;
        }

    }

}
