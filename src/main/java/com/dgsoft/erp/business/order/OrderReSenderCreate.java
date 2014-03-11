package com.dgsoft.erp.business.order;

import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.NeedRes;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.ResUnit;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import javax.faces.event.ValueChangeEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/7/14
 * Time: 7:38 PM
 */
@Name("orderReSenderCreate")
@Scope(ScopeType.CONVERSATION)
public class OrderReSenderCreate {

    private static final String SUPPLEMENT_REASON = "erp.needResReason.supplement";

    @DataModel(value = "reSendOrder")
    private List<OrderItem> reSendOrderItems;

    @DataModelSelection
    private OrderItem selectOrderItem;

    private OrderItem operOrderItem;

    @In
    private OrderHome orderHome;

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private NeedResHome needResHome;

    @In(create = true)
    private OrderItemSplit orderItemSplit;

    @In(create = true)
    private OrderDispatch orderDispatch;

    private boolean dispatched = false;

    public String beginDispatch() {
        wireReSenderItem();
        orderDispatch.init(needResHome.getInstance().getOrderItems());
        dispatched = true;
        return "/business/taskOperator/erp/sale/OrderChangeDispatch.xhtml";
    }

    private void wireReSenderItem(){
        needResHome.getInstance().getOrderItems().clear();
        needResHome.getInstance().getOrderItems().addAll(reSendOrderItems);
    }

    public NeedRes getReSenderNeedRes() {
        if (dispatched){
            needResHome.getInstance().getDispatches().addAll(orderDispatch.getDispatchList(needResHome.getInstance()));
            needResHome.getInstance().setStatus(NeedRes.NeedResStatus.DISPATCHED);
        }else{
            wireReSenderItem();
        }
        return needResHome.getInstance();
    }


    public List<OrderItem> getReSendOrderItems() {
        return reSendOrderItems;
    }

    public BigDecimal getReSenderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : reSendOrderItems) {
            result = result.add(item.getTotalPrice());
        }
        return result;
    }


    public void beginSplitOrderItem() {
        orderItemSplit.beginSplit(reSendOrderItems, selectOrderItem);
    }

    public void subItemOperTypeChangeListener() {
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
    }

    public void init(List<OrderItem> reSendOrderItems) {
        needResHome.clearInstance();

        subItemOperTypeChangeListener();

        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        this.reSendOrderItems = reSendOrderItems;


        for (OrderItem orderItem : reSendOrderItems) {
            orderItem.setNeedRes(needResHome.getInstance());
        }
        dispatched = false;
    }


}
