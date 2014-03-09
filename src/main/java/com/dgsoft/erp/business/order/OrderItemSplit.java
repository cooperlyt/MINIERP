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

    private StoreResCountInupt splitCountInput;

    public StoreResCountInupt getSplitCountInput() {
        return splitCountInput;
    }

    public void beginSplit(List<OrderItem> orderItemList, OrderItem orderItem){
        this.orderItemList = orderItemList;
        this.operOrderItem = orderItem;

        actionExecuteState.clearState();
        splitCountInput = new StoreResCountInupt(operOrderItem.getStoreRes());
    }

    public void splitOrderItem(){
        if ( operOrderItem.getCount().compareTo(splitCountInput.getCountByResUnit(operOrderItem.getResUnit())) <= 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"splitOrderCountMustLess");
            return;
        }

        OrderItem newItem = operOrderItem.cloneNew();
        newItem.setCount(splitCountInput.getCountByResUnit(newItem.getResUnit()));
        operOrderItem.setCount(operOrderItem.getCount().subtract(newItem.getCount()));

        orderItemList.add(newItem);
        actionExecuteState.actionExecute();
    }

}
