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

    private boolean reSend;

    public boolean isReSend() {
        return reSend;
    }

    public void setReSend(boolean reSend) {
        this.reSend = reSend;
    }

    public BigDecimal getOverlysTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : overlyItems) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getOverlyTotalNeedMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : overlyItems) {
            if (item.getNeedMoney() != null)
                result = result.add(item.getNeedMoney());
        }
        return result;
    }

    public BigDecimal getOwesTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : oweItems) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getOwesTotalNeedMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : oweItems) {
            if (item.getNeedMoney() != null)
                result = result.add(item.getNeedMoney());
        }
        return result;
    }

    public List<OrderItem> getCompleteOrderItems(){
        return orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.COMPLETED));
    }

    public List<OrderItem> getOweItems() {
        return oweItems;
    }

    public List<OrderItem> getOverlyItems() {
        return overlyItems;
    }

    @Override
    protected void initOrderTask() {
        orderHome.calcMoneys();

        overlyItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.WAIT_PRICE));


        oweItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.CREATED));
        for (OweOut oweOut : orderHome.getNoAddOweItems()) {
            OrderItem matchItem = null;
            for (OrderItem orderItem : orderHome.getOrderItemByStatus(
                    EnumSet.of(OrderItem.OrderItemStatus.CREATED,
                            OrderItem.OrderItemStatus.COMPLETED, OrderItem.OrderItemStatus.DISPATCHED))) {
                if (orderItem.getStoreRes().equals(oweOut.getStoreRes())) {
                    matchItem = orderItem;
                    break;
                }
            }

            OrderItem newItem = new OrderItem(oweOut.getStoreRes(), oweOut.getCount(), false,
                    OrderItem.OrderItemStatus.CREATED, false, oweOut.getDescription(), oweOut.getNeedConvertRate());

            if (matchItem != null) {
                newItem.setUseUnit(matchItem.getResUnit());
                newItem.setMoney(matchItem.getMoney());
                newItem.setRebate(matchItem.getRebate());
                newItem.calcMoney();
            }
            if (newItem.getUseUnit() == null) {
                newItem.setUseUnit(newItem.getStoreRes().getRes().getResUnitByOutDefault());
            }

            oweItems.add(newItem);
        }

        //------------------------

        reSend = true;

        orderReSenderCreate.init(oweItems);

        //-----------------------

        orderHome.calcMoneys();

    }

    @Override
    protected String completeOrderTask() {


        orderHome.calcMoneys();

        if (!reSend || oweItems.isEmpty()) {
            orderHome.getInstance().setAllStoreOut(true);
        } else {
            if (!orderReSenderCreate.isReady()) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatchNotComplete");
                return null;
            }


            orderHome.getInstance().getNeedReses().add(orderReSenderCreate.getReSenderNeedRes());
            orderHome.getInstance().setAllStoreOut(false);

        }


        for (OrderItem item : overlyItems) {
            item.setStatus(OrderItem.OrderItemStatus.COMPLETED);
        }

        for (OweOut oweOut : orderHome.getNoAddOweItems()) {
            oweOut.setAdd(true);
        }


        if ("updated".equals(orderHome.update())) {
            return "taskComplete";
        } else {
            return null;
        }

    }

}
