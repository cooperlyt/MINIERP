package com.dgsoft.erp.business.order;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.OrderHome;
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

    private static final String SUPPLEMENT_REASON = "erp.needResReason.supplement";

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private OrderDispatch orderDispatch;

    @In(create = true)
    private NeedResHome needResHome;


//    @In(create = true)
//    private OrderReSenderCreate orderReSenderCreate;

//    @DataModel(value = "newOrderItems")
//    private List<OrderItem> newOrderItems;
//
//    @DataModelSelection
//    private OrderItem selectOrderItem;

    private List<OrderItem> overlyItems;

    private List<OrderItem> oweItems;

    public String beginDispatch() {
        orderDispatch.init(needResHome.getInstance());
        dispatched = true;
        return "/business/taskOperator/erp/sale/OrderChangeDispatch.xhtml";
    }

    public String dispatchBack() {
        dispatched = false;
        return "/business/taskOperator/erp/sale/OrderChange.xhtml";
    }

    private boolean dispatched = false;

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

    public BigDecimal getOverlyTotalNeedMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : overlyItems) {
            if (item.getNeedMoney() != null)
                result = result.add(item.getNeedMoney());
        }
        return result;
    }

    public List<OrderItem> getCompleteOrderItems() {
        return orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.COMPLETED));
    }

    public List<OrderItem> getOverlyItems() {
        return overlyItems;
    }

    private void initReSend() {

        reSend = !oweItems.isEmpty();

        if (reSend) {
            needResHome.clearInstance();
            needResHome.getInstance().setStatus(NeedRes.NeedResStatus.CREATED);
            needResHome.getInstance().setCustomerOrder(orderHome.getInstance());
            needResHome.getInstance().setType(NeedRes.NeedResType.SUPPLEMENT_SEND);
            needResHome.getInstance().setReason(SUPPLEMENT_REASON);
            needResHome.getInstance().setFareByCustomer(orderHome.getLastNeedRes().isFareByCustomer());
            needResHome.getInstance().setPostCode(orderHome.getLastNeedRes().getPostCode());
            needResHome.getInstance().setAddress(orderHome.getLastNeedRes().getAddress());
            needResHome.getInstance().setReceivePerson(orderHome.getLastNeedRes().getReceivePerson());
            needResHome.getInstance().setReceiveTel(orderHome.getLastNeedRes().getReceiveTel());
            needResHome.getInstance().setCreateDate(new Date());

            needResHome.getOrderNeedItems().addAll(oweItems);

            for (OrderItem orderItem : oweItems) {
                orderItem.setNeedRes(needResHome.getInstance());
            }
            dispatched = false;
        }
        reSendChangeListener();

    }


    public void reSendChangeListener() {
        if (reSend) {
            orderHome.getInstance().getNeedReses().add(needResHome.getInstance());
        } else {
            orderHome.getInstance().getNeedReses().remove(needResHome.getInstance());
        }
        if (!orderHome.getInstance().isMoneyComplete())
            orderHome.calcMoneys();
    }

    @Override
    protected void initOrderTask() {
        customerHome.setId(orderHome.getInstance().getCustomer().getId());

        overlyItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.WAIT_PRICE));



        oweItems = orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.CREATED));

        if (!oweItems.isEmpty())
            for (NeedRes needRes: orderHome.getInstance().getNeedReses()){
                needRes.getOrderItems().removeAll(oweItems);
            }

        for (OweOut oweOut : orderHome.getNoAddOweItems()) {
            OrderItem matchItem = null;
            for (OrderItem orderItem : orderHome.getOrderItemByStatus(
                    EnumSet.of(OrderItem.OrderItemStatus.CREATED,
                            OrderItem.OrderItemStatus.COMPLETED, OrderItem.OrderItemStatus.DISPATCHED)
            )) {
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

        initReSend();
    }

    @Override
    protected String completeOrderTask() {

        if (!reSend || oweItems.isEmpty()) {
            orderHome.getInstance().setAllStoreOut(true);
            orderHome.getInstance().getNeedReses().remove(needResHome.getInstance());
            for (OrderItem item : oweItems) {
                item.setStatus(OrderItem.OrderItemStatus.CREATED);
                item.setDispatch(null);
            }
        } else {
            if (dispatched) {
                if (!orderDispatch.isDispatchComplete()) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatchNotComplete");
                    return null;
                }
                orderDispatch.wire();
                needResHome.getInstance().setStatus(NeedRes.NeedResStatus.DISPATCHED);
            }
            orderHome.getInstance().setAllStoreOut(false);
        }


        if (!orderHome.getInstance().isMoneyComplete())
            orderHome.calcMoneys();

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
