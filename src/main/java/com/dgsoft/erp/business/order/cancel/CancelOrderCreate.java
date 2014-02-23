package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.BatchOperEntity;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/21/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("cancelOrderCreate")
@Scope(ScopeType.CONVERSATION)
public class CancelOrderCreate {

    @In(create = true)
    private OrderBackHome orderBackHome;

    @In(create = true)
    private BusinessCreate businessCreate;

    @DataModel
    private List<BackOrderItem> backOrderItems;

    @DataModelSelection
    private BackOrderItem selectBackOrderItem;

    @In
    private OrderHome orderHome;

    @In(create = true)
    private BussinessProcessUtils businessProcess;

    private boolean selectAll;

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public void init() {
        orderBackHome.init();
        if (orderHome.getInstance().isAllStoreOut()) {
            orderBackHome.getInstance().setOrderBackType(OrderBack.OrderBackType.PART_ORDER_BACK);

        } else {
            orderBackHome.getInstance().setOrderBackType(OrderBack.OrderBackType.ALL_ORDER_CANCEL);
        }
        initBackOrderItem();
    }

    public void backAllMoney() {
        orderBackHome.getInstance().setMoney(orderHome.getTotalReveiveMoney());
        orderBackHome.getInstance().setSaveMoney(BigDecimal.ZERO);
    }

    public void backNoEarnestMoney() {
        orderBackHome.getInstance().setMoney(orderHome.getTotalReveiveMoney().subtract(orderHome.getReveiveEarnest()));
        orderBackHome.getInstance().setSaveMoney(orderHome.getReveiveEarnest());
    }

    public void calcBackMoney() {
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            orderBackHome.getInstance().setMoney(orderHome.getTotalReveiveMoney().subtract(orderBackHome.getInstance().getSaveMoney()));
        } else {
            orderBackHome.getInstance().setMoney(getResTotalMoney().subtract(orderBackHome.getInstance().getSaveMoney()));
        }
    }

    public BigDecimal getMaxSaveMoney() {
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            return orderHome.getTotalReveiveMoney();
        } else {
            return getResTotalMoney();
        }
    }


    public void wireBackItem() {
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            for (Map.Entry<StoreRes, ResCount> entry : orderHome.allShipStoreReses().entrySet()) {
                orderBackHome.getInstance().getBackItems().add(new BackItem(orderBackHome.getInstance(), entry.getKey(), entry.getValue()));
            }
        } else {
            for (BackOrderItem item : backOrderItems) {
                if (item.isSelected())
                    orderBackHome.getInstance().getBackItems().add(item.backItem);
            }
        }
    }

    @Transactional
    public String cancelOrder() {

        calcBackMoney();
        orderBackHome.getInstance().setCustomerOrder(orderHome.getInstance());
        orderBackHome.getInstance().setCustomer(orderHome.getInstance().getCustomer());
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            if (!orderHome.isAnyOneStoreOut() && !orderHome.isAnyOneMoneyPay()) {
                orderBackHome.getInstance().getCustomerOrder().setCanceled(true);
                return cancelSimpleOrder();
            }

//            ProcessDefinition definition = ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinition();
//            ProcessInstance processInstance = definition==null ?
//                    null : ManagedJbpmContext.instance().getProcessInstanceForUpdate(definition, orderHome.getInstance().getId());
//
//            processInstance.suspend();

            businessProcess.suspendProcess("order",orderHome.getInstance().getId());
        }
        wireBackItem();

        return businessCreate.create();
    }


    @End
    public String cancelSimpleOrder() {

        businessProcess.stopProcess("order", orderHome.getInstance().getId());
        if ("persisted".equals(orderBackHome.persist())) {
            return "/func/erp/sale/CustomerOrder.xhtml";
        } else
            return null;
    }

    public void initBackOrderItem() {
        backOrderItems = new ArrayList<BackOrderItem>();
        selectAll = true;
        for (Map.Entry<StoreRes, ResCount> entry : orderHome.getInstance().getAllShipStoreReses().entrySet()) {
            OrderHome.StoreResPrice orderPrice = orderHome.getFirstPrice(entry.getKey());
            backOrderItems.add(new BackOrderItem(orderBackHome.getInstance(),
                    new OrderHome.StoreResPrice(entry.getKey(), orderPrice.getUnit(), orderPrice.getUnitPrice(),
                            entry.getValue().getCountByResUnit(orderPrice.getUnit()))));
        }
        calcBackMoney();

    }

    public void selectItemListener() {
        for (BackOrderItem item : backOrderItems) {
            if (!item.isSelected()) {
                selectAll = false;
            }
        }
        selectAll = true;
        calcBackMoney();
    }

    public void selectAllListener() {

        for (BackOrderItem item : backOrderItems) {
            item.setSelected(selectAll);
        }
        calcBackMoney();

    }

    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BackOrderItem item : backOrderItems) {
            if (item.isSelected()) {
                result = result.add(item.getBackItem().getTotalMoney());
            }
        }
        return result;
    }


    public static class BackOrderItem extends BatchOperEntity {

        private BackItem backItem;

        private ResCount outCount;

        public BackOrderItem(OrderBack orderBack, OrderHome.StoreResPrice orderItemPrice) {
            this.outCount = orderItemPrice.getResCount();
            backItem = new BackItem(orderBack, orderItemPrice.getStoreRes(), orderItemPrice.getUnit(),
                    orderItemPrice.getCount(), orderItemPrice.getUnitPrice());
            setSelected(true);
        }

        public BackItem getBackItem() {
            return backItem;
        }

        public void setBackItem(BackItem backItem) {
            this.backItem = backItem;
        }

        public ResCount getOutCount() {
            return outCount;
        }

        public void setOutCount(ResCount outCount) {
            this.outCount = outCount;
        }

        public BigDecimal getMaxOutCount(){
           return outCount.getCountByResUnit(backItem.getResUnit());
        }
    }
}
