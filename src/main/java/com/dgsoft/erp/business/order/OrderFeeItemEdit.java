package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.model.OrderFee;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/17/13
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("orderFeeItemEdit")
@Scope(ScopeType.CONVERSATION)
public class OrderFeeItemEdit {


    @In(create = true)
    private ActionExecuteState actionExecuteState;

    @DataModelSelection
    private OrderFee selectedOrderFee;

    @DataModel(value="editOrderFee")
    private List<OrderFee> orderFeeList = new ArrayList<OrderFee>();

    private OrderFee editingOrderFee;

    private boolean editingNewFee;



    public void createNewOrderFee(){
        editingNewFee = true;
        editingOrderFee = new OrderFee(false,false,new Date());
        actionExecuteState.clearState();
    }

    public void editOrderFee(){
        editingNewFee = false;
        editingOrderFee = selectedOrderFee;
        actionExecuteState.clearState();
    }

    public void removeOrderFee(){
        orderFeeList.remove(selectedOrderFee);
    }

    public void addNewOrderFee(){


        orderFeeList.add(editingOrderFee);

        selectedOrderFee = null;
        actionExecuteState.actionExecute();
    }

    public BigDecimal getTotalFeeMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderFee fee: orderFeeList){
            result = result.add(fee.getMoney());
        }
        return result;
    }



    public OrderFee getEditingOrderFee() {
        return editingOrderFee;
    }

    public void setEditingOrderFee(OrderFee editingOrderFee) {
        this.editingOrderFee = editingOrderFee;
    }

    public boolean isEditingNewFee() {
        return editingNewFee;
    }

    public void setEditingNewFee(boolean editingNewFee) {
        this.editingNewFee = editingNewFee;
    }

    public List<OrderFee> getOrderFeeList() {
        return orderFeeList;
    }

}
