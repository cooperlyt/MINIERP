package com.dgsoft.erp.business.order;

import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.MiddleManHome;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.OrderFee;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/16/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("orderFee")
public class OrderFeeApply extends OrderTaskHandle {

    public enum ItemMiddleMoneyCalcType {
        NOT_CALC, ITEM_FIX, ITEM_RATE, CROSS_CALC;
    }

    @In(create = true)
    private MiddleManHome middleManHome;

    @In(create = true)
    private OrderFeeItemEdit orderFeeItemEdit;

    @DataModelSelection
    private OrderItem selectOrderItem;

    @DataModel(value = "orderItemsByFee")
    public List<OrderItem> getOrderItemsByFee() {
        return orderHome.getMasterNeedRes().getOrderItemList();
    }

    @Factory(value = "itemMiddleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public ItemMiddleMoneyCalcType[] getItemMiddleMoneyCalcTypes() {
        return ItemMiddleMoneyCalcType.values();
    }

    private ItemMiddleMoneyCalcType itemMiddleMoneyCalcType;


    private OrderFee middleManFee;

    //private OrderFee newOrderFee;

    public OrderFee getMiddleManFee() {
        return middleManFee;
    }

    public void setMiddleManFee(OrderFee middleManFee) {
        this.middleManFee = middleManFee;
    }

    public ItemMiddleMoneyCalcType getItemMiddleMoneyCalcType() {
        return itemMiddleMoneyCalcType;
    }

    public void setItemMiddleMoneyCalcType(ItemMiddleMoneyCalcType itemMiddleMoneyCalcType) {
        this.itemMiddleMoneyCalcType = itemMiddleMoneyCalcType;
    }

    public BigDecimal getTotalMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem orderItem : getOrderItemsByFee()) {
            if (orderItem.getMiddleMoney() != null) {
                result = result.add(orderItem.getMiddleMoney());
            }
        }
        return result;
    }

    public void middleManIncludeLisntener() {
        if (orderHome.getInstance().isIncludeMiddleMan()
                && (orderHome.getInstance().getCustomer().getMiddleMan() != null)) {

            middleManHome.setId(orderHome.getInstance().getCustomer().getMiddleMan().getId());
            middleManFee = new OrderFee(orderHome.getInstance(), middleManHome.getInstance().getBankNumber(),
                    middleManHome.getInstance().getBankInfo(),
                    true, middleManHome.getInstance().getBank(), middleManHome.getInstance().getContact(), false, new Date());
        }
    }

    public void orderItemFeeUnitChangeListener() {
        if ((selectOrderItem.getMiddleMoneyCalcType() == null) ||
                !selectOrderItem.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.COUNT_FIX)) {
            return;
        }

        if (selectOrderItem.getMiddleUnit() == null) {
            selectOrderItem.setMiddleMoney(null);
            selectOrderItem.setMiddleRate(null);
        } else {
            calcItemMiddleMoney(selectOrderItem);
            for (OrderItem orderItem : getOrderItemsByFee()) {
                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
                        (orderItem.getMiddleUnit() == null) &&
                        (orderItem.getUseRes().equals(selectOrderItem.getUseRes()))) {
                    orderItem.setMiddleUnit(selectOrderItem.getMiddleUnit());
                    calcItemMiddleMoney(orderItem);
                }
            }

        }
    }

    public void orderItemFeeRateChangeListener() {


        if (selectOrderItem.getMiddleRate().compareTo(BigDecimal.ZERO) == 0) {
            selectOrderItem.setMiddleMoney(BigDecimal.ZERO);
        } else {
            calcItemMiddleMoney(selectOrderItem);
            for (OrderItem orderItem : getOrderItemsByFee()) {
                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
                        (orderItem.getMiddleRate() == null) &&
                        (orderItem.getUseRes().equals(selectOrderItem.getUseRes()))) {
                    orderItem.setMiddleRate(selectOrderItem.getMiddleRate());
                    calcItemMiddleMoney(orderItem);
                }
            }

        }
    }

    public void middleMoneyAllItemCalcTypeChangeListener() {


        for (OrderItem orderItem : getOrderItemsByFee()) {
            orderItem.setMiddleRate(null);
            orderItem.setMiddleMoney(null);
            if (itemMiddleMoneyCalcType.equals(ItemMiddleMoneyCalcType.ITEM_FIX)) {
                orderItem.setMiddleMoneyCalcType(OrderItem.MiddleMoneyCalcType.COUNT_FIX);
                if (!orderItem.isStoreResItem() && !orderItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                    orderItem.setMiddleUnit(orderItem.getMoneyUnit());
                }

            } else if (itemMiddleMoneyCalcType.equals(ItemMiddleMoneyCalcType.ITEM_RATE)) {
                orderItem.setMiddleMoneyCalcType(OrderItem.MiddleMoneyCalcType.MONEY_RATE);
                orderItem.setMiddleUnit(null);
            } else {
                orderItem.setMiddleMoneyCalcType(null);
                orderItem.setMiddleUnit(null);
            }

        }
    }

    public void middleMoneyCalcTypeChangeListener() {
        orderHome.getInstance().setMiddleMoney(null);
        orderHome.getInstance().setMiddleRate(null);
    }

    public void middleMoneyItemCalcTypeChangeListener() {
        if (selectOrderItem.getMiddleMoneyCalcType() == null) {
            selectOrderItem.setMiddleRate(null);
            selectOrderItem.setMiddleMoney(null);
            selectOrderItem.setMiddleUnit(null);
        } else {
            if (selectOrderItem.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.COUNT_FIX) &&
                    !selectOrderItem.isStoreResItem() && !selectOrderItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                selectOrderItem.setMiddleUnit(selectOrderItem.getMoneyUnit());
            }
            calcItemMiddleMoney(selectOrderItem);

            for (OrderItem orderItem : getOrderItemsByFee()) {
                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
                        (orderItem.getMiddleMoneyCalcType() == null) &&
                        (orderItem.getUseRes().equals(selectOrderItem.getUseRes()))) {
                    orderItem.setMiddleMoneyCalcType(selectOrderItem.getMiddleMoneyCalcType());
                    calcItemMiddleMoney(orderItem);
                }
            }
        }
    }


    private void calcItemMiddleMoney(OrderItem item) {
        if ((item.getMiddleMoneyCalcType() != null) && (item.getMiddleRate() != null)) {
            if (item.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.COUNT_FIX) && (item.getMiddleUnit() != null)) {
                if (item.isStoreResItem() || item.getRes().getUnitGroup().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                    item.setMiddleMoney(item.getStoreResCount().getCountByResUnit(item.getMiddleUnit()).multiply(item.getMiddleRate()));
                } else {
                    item.setMiddleMoney(item.getCount().multiply(item.getMiddleRate()));
                }
            } else if (item.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.MONEY_RATE)) {
                item.setMiddleMoney(BigDecimalFormat.halfUpCurrency(item.getTotalMoney().multiply(item.getMiddleRate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP))));
            }
        }
    }

    public void middleMoneyRateChangeListener() {
        if ((orderHome.getInstance().getMiddleRate() == null) ||
                (orderHome.getInstance().getMiddleRate().compareTo(BigDecimal.ZERO) == 0)) {
            orderHome.getInstance().setMiddleMoney(BigDecimal.ZERO);
            return;
        }
        orderHome.getInstance().setMiddleMoney(
                orderHome.getInstance().getMoney().multiply(
                        orderHome.getInstance().getMiddleRate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
    }

    public void middleMoneyChangeListener() {
        if ((orderHome.getInstance().getMiddleMoney() == null) ||
                (orderHome.getInstance().getMiddleMoney().compareTo(BigDecimal.ZERO) == 0)) {
            orderHome.getInstance().setMiddleRate(BigDecimal.ZERO);
            return;
        }
        orderHome.getInstance().setMiddleRate(
                orderHome.getInstance().getMiddleMoney().divide(orderHome.getInstance().getMoney(), 20, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal("100")));
    }


    protected String completeOrderTask() {
        //orderHome.getInstance().getOrderFees().clear();
        if (orderHome.getInstance().isIncludeMiddleMan()) {

            orderHome.getInstance().setMiddleTotal(BigDecimal.ZERO);
            if (!itemMiddleMoneyCalcType.equals(ItemMiddleMoneyCalcType.NOT_CALC)) {
                orderHome.getInstance().setMiddleTotal(getTotalMiddleMoney());
            }

            if (!CustomerOrder.MiddleMoneyCalcType.NOT_CALC.equals(orderHome.getInstance().getMiddleMoneyCalcType())
                    && (orderHome.getInstance().getMiddleMoney() != null)) {
                orderHome.getInstance().setMiddleTotal(
                        orderHome.getInstance().getMiddleTotal().add(orderHome.getInstance().getMiddleMoney()));
            }


            if ((orderHome.getInstance().getMiddleTotal() == null) ||
                    (BigDecimal.ZERO.compareTo(orderHome.getInstance().getMiddleTotal()) == 0)) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "includeMiddleManButMoneyIsZero");
                return "fail";
            }

            middleManFee.setMoney(orderHome.getInstance().getMiddleTotal());
            orderHome.getInstance().getOrderFees().add(middleManFee);

        }

        for (OrderFee orderFee : orderFeeItemEdit.getOrderFeeList()) {
            orderFee.setCustomerOrder(orderHome.getInstance());
            orderHome.getInstance().getOrderFees().add(orderFee);
        }


        //TODO move to Last task
        if (!orderHome.isHavePayFee()) {
            //TODO if   ORDER_OVERDRAFT_COMPLETE,
            orderHome.getInstance().setState(CustomerOrder.OrderState.ORDER_COMPLETE);
        }

        if ("updated".equals(orderHome.update())) {
            return "taskComplete";
        } else
            return "fail";


    }


    protected String initOrderTask() {
        middleManIncludeLisntener();

        return "success";
    }

}
