package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.business.order.OrderItemCreate;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;

/**
 * Created by cooper on 2/8/15.
 */


@Name("priceChangeOrderCreate")
public class PriceChangeOrderCreate {

    private boolean type = true;

    private BigDecimal money;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    private void createNoItemOrder() {
        Logging.getLog(getClass()).debug(money);
        orderHome.getInstance().setTotalRebateMoney(money);
        orderHome.getInstance().setMoney(money);
        orderHome.getInstance().setResMoney(BigDecimal.ZERO);
        orderHome.getInstance().getOrderReduces().add(
                new OrderReduce(orderHome.getInstance().getMemo(), new BigDecimal("-1").multiply(money) , orderHome.getInstance(), OrderReduce.ReduceType.PRICE_CHANGE)
        );

    }


    @In
    private Credentials credentials;

    @In(create = true)
    private OrderHome orderHome;

    @In(create = true)
    private CustomerHome customerHome;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private OrderItemCreate orderItemCreate;

    @In(create = true)
    private StoreResHome storeResHome;

    private String selectStoreResId;

    public String getSelectStoreResId() {
        return selectStoreResId;
    }

    public void setSelectStoreResId(String selectStoreResId) {
        this.selectStoreResId = selectStoreResId;
    }

    public void removeItem() {
        for (OrderItem item : orderHome.getLastNeedRes().getOrderItems()) {
            if (item.getStoreRes().getId().equals(selectStoreResId)) {
                orderHome.getLastNeedRes().getOrderItems().remove(item);
                return;
            }
        }
    }

    private String customerMoney(){
        if (orderHome.getInstance().getMoney().compareTo(BigDecimal.ZERO) != 0){
            AccountOper oper = new AccountOper(AccountOper.AccountOperType.ORDER_PAY,credentials.getUsername(),orderHome.getInstance().getCustomer());
            oper.setCustomerOrder(orderHome.getInstance());
            oper.setDescription(orderHome.getInstance().getMemo());
            oper.setOperDate(orderHome.getInstance().getCreateDate());
            oper.setAccountsReceivable(orderHome.getInstance().getMoney());
            orderHome.getInstance().getAccountOpers().add(oper);
            oper.calcCustomerMoney();
        }

        return orderHome.persist();

    }

    public BigDecimal getTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : orderHome.getLastNeedRes().getOrderItems()) {
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public String complete(){
        orderHome.getInstance().setTotalRebateMoney(BigDecimal.ZERO);
        orderHome.getInstance().setMoney(getTotalMoney());
        orderHome.getInstance().setResMoney(getTotalMoney());
        return customerMoney();
    }

    @In
    private NumberBuilder numberBuilder;

    public String create() {


        orderHome.getInstance().setId(numberBuilder.getDayNumber("order"));
        while (orderHome.getEntityManager().find(CustomerOrder.class, orderHome.getInstance().getId()) != null) {
            orderHome.getInstance().setId(numberBuilder.getDayNumber("order"));
        }

        orderHome.getInstance().setTotalCost(BigDecimal.ZERO);
        orderHome.getInstance().setCustomer(customerHome.getInstance());
        orderHome.getInstance().setPayType(CustomerOrder.OrderPayType.PRICE_CHANGE);
        orderHome.getInstance().setMiddlePayed(false);
        orderHome.getInstance().setOrderEmp(credentials.getUsername());
        orderHome.getInstance().setEarnest(BigDecimal.ZERO);
        orderHome.getInstance().setEarnestFirst(false);
        orderHome.getInstance().setResReceived(true);
        orderHome.getInstance().setAllStoreOut(true);
        orderHome.getInstance().setCanceled(false);
        orderHome.getInstance().setAllShipDate(orderHome.getInstance().getCreateDate());
        orderHome.getInstance().setAdvanceMoney(BigDecimal.ZERO);
        orderHome.getInstance().setPayTag(true);
        orderHome.getInstance().setAccountChange(true);
        if (type) {
            orderHome.getInstance().getNeedReses().add(new NeedRes(orderHome.getInstance(), NeedRes.NeedResType.PRICE_CHANGE));
            return "createItem";
        } else {
            createNoItemOrder();
            return customerMoney();
        }


    }

    public void addOrderItem() {
        OrderItem editingItem = orderItemCreate.getEditingItem();

        if (editingItem == null) {
            throw new IllegalArgumentException("editingItem state error");
        }


        storeResHome.setRes(editingItem.getRes(), editingItem.getFormats(), editingItem.getFloatConvertRate());
        if (!storeResHome.isIdDefined()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                    "storeResNotDefine");
            return;
        }

        for (OrderItem item : orderHome.getLastNeedRes().getOrderItems()) {
            if (item.getStoreRes().getId().equals(storeResHome.getInstance().getId())) {
                item.setTotalMoney(item.getTotalMoney().add(editingItem.getTotalMoney()));
                orderItemCreate.createNext();
                return;
            }
        }

        editingItem.setStoreRes(storeResHome.getInstance());
        editingItem.setMasterCount(BigDecimal.ZERO);
        editingItem.setStatus(OrderItem.OrderItemStatus.COMPLETED);
        editingItem.setOverlyOut(false);
        editingItem.setNeedRes(orderHome.getLastNeedRes());
        editingItem.setResUnit(editingItem.getStoreRes().getRes().getUnitGroup().getMasterUnit());
        editingItem.setMoney(BigDecimal.ZERO);
        editingItem.setPresentation(false);
        editingItem.setSaleCount(BigDecimal.ZERO);
        editingItem.setRebate(new BigDecimal("100"));
        editingItem.setNeedCount(BigDecimal.ZERO);
        orderHome.getLastNeedRes().getOrderItems().add(editingItem);
        orderItemCreate.createNext();


    }
}
