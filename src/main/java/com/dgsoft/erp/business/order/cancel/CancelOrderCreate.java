package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.BatchOperEntity;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

    //@DataModel
    //private List<BackOrderItem> backOrderItems;

    private Map<BackItem, BatchOperData<StoreResCount>> backOrderItems;

    @In
    private FacesMessages facesMessages;

    @In
    private OrderHome orderHome;

    @In(create = true)
    private BussinessProcessUtils businessProcess;

    @In(create = true)
    private ResBackDispatch resBackDispatch;

    private boolean selectAll;

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public List<>

    @Create
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


    public List<BackItem> genBackItem() {
        List<BackItem> result = new ArrayList<BackItem>();

        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            for (Map.Entry<StoreRes, ResCount> entry : orderHome.allShipStoreReses().entrySet()) {
                result.add(new BackItem(orderBackHome.getInstance(), entry.getKey(), entry.getValue()));
            }
        } else {
            for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
                if (item.getValue().isSelected())
                    result.add(item.getKey());
            }
        }
        return result;
    }

    public boolean isCanDispatch() {
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.ALL_ORDER_CANCEL)) {
            return !orderHome.allShipStoreReses().isEmpty();
        } else {
            for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
                if (item.getValue().isSelected() && (item.getKey().getMasterCount().compareTo(BigDecimal.ZERO) > 0))
                    return true;
            }
            return false;
        }
    }

    public String dispatchCancelOrder() {
        resBackDispatch.init(genBackItem());
        return "/business/startPrepare/erp/sale/BackStoreResDispatch.xhtml";
    }

    public boolean isCanCancel() {
        if (orderBackHome.getInstance().getOrderBackType().equals(OrderBack.OrderBackType.PART_ORDER_BACK)) {

            for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
                if (item.getValue().isSelected() && (item.getKey().getMasterCount().compareTo(item.getValue().getData().getMasterCount()) > 0)) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderBack_backCountLessOrderCountError",
                            item.getKey().getStoreRes().getCode(), item.getKey().getDisplayMasterCount());
                    return false;
                }
            }

        }
        return true;
    }

    @Transactional
    public String dispatchAndCancelOrder() {
        if (!isCanCancel()) return null;

        if (!resBackDispatch.isComplete()) {

            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatchNotComplete");
            return null;
        }
        orderBackHome.getInstance().setDispatched(true);
        orderBackHome.getInstance().getProductBackStoreIn().clear();
        orderBackHome.getInstance().getProductBackStoreIn().addAll(resBackDispatch.getResBackDispatcheds(orderBackHome.getInstance()));
        return cancelOrder();
    }

    @Transactional
    public String cancelOrder() {
        if (!isCanCancel()) return null;
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

            businessProcess.suspendProcess("order", orderHome.getInstance().getId());
        }

        orderBackHome.getInstance().getBackItems().clear();
        orderBackHome.getInstance().getBackItems().addAll(genBackItem());

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
        backOrderItems = new HashMap<BackItem, BatchOperData<StoreResCount>>();
        selectAll = true;
        for (Map.Entry<StoreRes, ResCount> entry : orderHome.getInstance().getAllShipStoreReses().entrySet()) {


            for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
                for (OrderItem orderItem : needRes.getOrderItems()) {
                    if (orderItem.getStoreRes().equals(entry.getKey())) {
                        backOrderItems.put(new BackItem(orderBackHome.getInstance(), entry.getKey(), orderItem.getMoneyUnit(),
                                entry.getValue().getCountByResUnit(orderItem.getMoneyUnit()),
                                orderItem.getRebateUnitPrice()),
                                new BatchOperData<StoreResCount>(new StoreResCount(entry.getKey(), entry.getValue().getMasterCount()), true));
                        break;
                    }
                }
            }
        }
        calcBackMoney();

    }

    public void selectItemListener() {

        for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
            if (!item.getValue().isSelected()){
                selectAll = false;
                calcBackMoney();
                return;
            }
        }
        selectAll = true;
        calcBackMoney();
    }

    public void selectAllListener() {

        for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
            item.getValue().setSelected(selectAll);
        }

        calcBackMoney();

    }

    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (Map.Entry<BackItem, BatchOperData<StoreResCount>> item : backOrderItems.entrySet()) {
            if (item.getValue().isSelected()){
                result = result.add(item.getKey().getTotalPrice());
            }

        }
        return result;
    }

}
