package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.OrderItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/7/14
 * Time: 10:14 PM
 */
@Name("orderItemSplit")
@Scope(ScopeType.CONVERSATION)
public class OrderItemSplit {

    @In
    protected FacesMessages facesMessages;

    @In
    private ActionExecuteState actionExecuteState;

    private OrderItem operOrderItem;

    private List<OrderItem> orderItemList;

    private OrderItem newOrderItem;

    public OrderItem getNewOrderItem() {
        return newOrderItem;
    }

    public void setNewOrderItem(OrderItem newOrderItem) {
        this.newOrderItem = newOrderItem;
    }

    public void beginSplit(List<OrderItem> orderItemList, OrderItem orderItem){
        this.orderItemList = orderItemList;
        this.operOrderItem = orderItem;

        actionExecuteState.clearState();
        newOrderItem = operOrderItem.cloneNew();
        newOrderItem.setCount(BigDecimal.ZERO);
    }

    public void splitOrderItem(){
        if ( operOrderItem.getCount().compareTo(newOrderItem.getCount()) <= 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"splitOrderCountMustLess");
            return;
        }


        operOrderItem.setCount(operOrderItem.getCount().subtract(newOrderItem.getCount()));

        orderItemList.add(newOrderItem);
        actionExecuteState.actionExecute();
    }

}
