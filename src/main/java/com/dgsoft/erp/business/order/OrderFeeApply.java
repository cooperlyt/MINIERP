package com.dgsoft.erp.business.order;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.action.MiddleManHome;
import com.dgsoft.erp.action.OrderHome;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.OrderFee;
import com.dgsoft.erp.model.OrderItem;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

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

//
//    @In(create = true)
//    private MiddleManHome middleManHome;
//
//    @In(create = true)
//    private OrderFeeItemEdit orderFeeItemEdit;
//
//    @DataModelSelection
//    private OrderItem selectOrderItem;
//
//    @DataModel(value = "orderItemsByFee")
//    public List<OrderItem> getOrderItemsByFee() {
//        return orderHome.getMasterNeedRes().getOrderItemList();
//    }
//
//
//    private OrderHome.ItemMiddleMoneyCalcType itemMiddleMoneyCalcType;
//
//
//    private OrderFee middleManFee;
//
//    //private OrderFee newOrderFee;
//
//    public OrderFee getMiddleManFee() {
//        return middleManFee;
//    }
//
//    public void setMiddleManFee(OrderFee middleManFee) {
//        this.middleManFee = middleManFee;
//    }
//
//    public OrderHome.ItemMiddleMoneyCalcType getItemMiddleMoneyCalcType() {
//        return itemMiddleMoneyCalcType;
//    }
//
//    public void setItemMiddleMoneyCalcType(OrderHome.ItemMiddleMoneyCalcType itemMiddleMoneyCalcType) {
//        this.itemMiddleMoneyCalcType = itemMiddleMoneyCalcType;
//    }
//
//    public BigDecimal getTotalMiddleMoney() {
//        BigDecimal result = BigDecimal.ZERO;
//        for (OrderItem orderItem : getOrderItemsByFee()) {
//            if (orderItem.getMiddleMoney() != null) {
//                result = result.add(orderItem.getMiddleMoney());
//            }
//        }
//        return result;
//    }
//
//    public void middleManIncludeLisntener() {
//        if (orderHome.getInstance().isIncludeMiddleMan()
//                && (orderHome.getInstance().getCustomer().getMiddleMan() != null)) {
//
//            middleManHome.setId(orderHome.getInstance().getCustomer().getMiddleMan().getId());
//            middleManFee = new OrderFee(orderHome.getInstance(), middleManHome.getInstance().getBankNumber(),
//                    middleManHome.getInstance().getBankInfo(),
//                    true, middleManHome.getInstance().getBank(), middleManHome.getInstance().getContact(), false, new Date());
//        }
//        calcMiddleMoney();
//    }
//
//    public void orderItemFeeUnitChangeListener() {
//        if ((selectOrderItem.getMiddleMoneyCalcType() == null) ||
//                !selectOrderItem.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.COUNT_FIX)) {
//            return;
//        }
//
//        if (selectOrderItem.getMiddleUnit() == null) {
//            selectOrderItem.setMiddleMoney(null);
//            selectOrderItem.setMiddleRate(null);
//        } else {
//            calcItemMiddleMoney(selectOrderItem);
//            for (OrderItem orderItem : getOrderItemsByFee()) {
//                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
//                        (orderItem.getMiddleUnit() == null) &&
//                        (orderItem.getStoreRes().getRes().equals(selectOrderItem.getStoreRes().getRes()))) {
//                    orderItem.setMiddleUnit(selectOrderItem.getMiddleUnit());
//                    calcItemMiddleMoney(orderItem);
//                }
//            }
//
//        }
//        calcMiddleMoney();
//    }
//
//    public void orderItemFeeRateChangeListener() {
//
//        if (selectOrderItem.getMiddleRate() == null)
//            return;
//
//        if (selectOrderItem.getMiddleRate().compareTo(BigDecimal.ZERO) == 0) {
//            selectOrderItem.setMiddleMoney(BigDecimal.ZERO);
//        } else {
//            calcItemMiddleMoney(selectOrderItem);
//            for (OrderItem orderItem : getOrderItemsByFee()) {
//                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
//                        (orderItem.getMiddleRate() == null) &&
//                        (orderItem.getStoreRes().getRes().equals(selectOrderItem.getStoreRes().getRes()))) {
//                    orderItem.setMiddleRate(selectOrderItem.getMiddleRate());
//                    calcItemMiddleMoney(orderItem);
//                }
//            }
//
//        }
//        calcMiddleMoney();
//    }
//
//    public void middleMoneyAllItemCalcTypeChangeListener() {
//
//
//        for (OrderItem orderItem : getOrderItemsByFee()) {
//            orderItem.setMiddleRate(null);
//            orderItem.setMiddleMoney(null);
//            if (itemMiddleMoneyCalcType.equals(OrderHome.ItemMiddleMoneyCalcType.ITEM_FIX)) {
//                orderItem.setMiddleMoneyCalcType(OrderItem.MiddleMoneyCalcType.COUNT_FIX);
//
//
//            } else if (itemMiddleMoneyCalcType.equals(OrderHome.ItemMiddleMoneyCalcType.ITEM_RATE)) {
//                orderItem.setMiddleMoneyCalcType(OrderItem.MiddleMoneyCalcType.MONEY_RATE);
//                orderItem.setMiddleUnit(null);
//            } else {
//                orderItem.setMiddleMoneyCalcType(null);
//                orderItem.setMiddleUnit(null);
//            }
//
//        }
//        calcMiddleMoney();
//    }
//
//    public void middleMoneyCalcTypeChangeListener() {
//        orderHome.getInstance().setMiddleMoney(null);
//        orderHome.getInstance().setMiddleRate(null);
//        calcMiddleMoney();
//    }
//
//    public void middleMoneyItemCalcTypeChangeListener() {
//        if (selectOrderItem.getMiddleMoneyCalcType() == null) {
//            selectOrderItem.setMiddleRate(null);
//            selectOrderItem.setMiddleMoney(null);
//            selectOrderItem.setMiddleUnit(null);
//        } else {
//
//            calcItemMiddleMoney(selectOrderItem);
//
//            for (OrderItem orderItem : getOrderItemsByFee()) {
//                if ((!selectOrderItem.getId().equals(orderItem.getId())) &&
//                        (orderItem.getMiddleMoneyCalcType() == null) &&
//                        (orderItem.getStoreRes().equals(selectOrderItem.getStoreRes()))) {
//                    orderItem.setMiddleMoneyCalcType(selectOrderItem.getMiddleMoneyCalcType());
//                    calcItemMiddleMoney(orderItem);
//                }
//            }
//        }
//        calcMiddleMoney();
//    }
//
//
//    private void calcItemMiddleMoney(OrderItem item) {
//        if ((item.getMiddleMoneyCalcType() != null) && (item.getMiddleRate() != null)) {
//            if (item.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.COUNT_FIX) && (item.getMiddleUnit() != null)) {
//                item.setMiddleMoney(item.getCountByResUnit(item.getMiddleUnit()).multiply(item.getMiddleRate()));
//
//            } else if (item.getMiddleMoneyCalcType().equals(OrderItem.MiddleMoneyCalcType.MONEY_RATE)) {
//                item.setMiddleMoney(DataFormat.halfUpCurrency(item.getTotalPrice().multiply(item.getMiddleRate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP))));
//            }
//        }
//
//    }
//
//    public void middleMoneyRateChangeListener() {
//        if ((orderHome.getInstance().getMiddleRate() == null) ||
//                (orderHome.getInstance().getMiddleRate().compareTo(BigDecimal.ZERO) == 0)) {
//            orderHome.getInstance().setMiddleMoney(BigDecimal.ZERO);
//            return;
//        }
//        orderHome.getInstance().setMiddleMoney(
//                orderHome.getInstance().getMoney().multiply(
//                        orderHome.getInstance().getMiddleRate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)));
//        calcMiddleMoney();
//    }
//
//    public void middleMoneyChangeListener() {
//        if ((orderHome.getInstance().getMiddleMoney() == null) ||
//                (orderHome.getInstance().getMiddleMoney().compareTo(BigDecimal.ZERO) == 0)) {
//            orderHome.getInstance().setMiddleRate(BigDecimal.ZERO);
//            return;
//        }
//        orderHome.getInstance().setMiddleRate(
//                orderHome.getInstance().getMiddleMoney().divide(orderHome.getInstance().getMoney(), 20, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal("100")));
//
//        calcMiddleMoney();
//    }
//
//    public void calcMiddleMoney() {
//
//        if (orderHome.getInstance().isIncludeMiddleMan() && (middleManFee != null)) {
//            BigDecimal result = BigDecimal.ZERO;
//
//            if ((itemMiddleMoneyCalcType != null) && !OrderHome.ItemMiddleMoneyCalcType.NOT_CALC.equals(itemMiddleMoneyCalcType)) {
//                result = result.add(getTotalMiddleMoney());
//            }
//
//            if ((orderHome.getInstance().getMiddleMoneyCalcType() != null) &&
//                    !CustomerOrder.MiddleMoneyCalcType.NOT_CALC.equals(orderHome.getInstance().getMiddleMoneyCalcType())
//                    && (orderHome.getInstance().getMiddleMoney() != null)) {
//                result = result.add(orderHome.getInstance().getMiddleMoney());
//            }
//
//            middleManFee.setMoney(result);
//        }
//    }
//
//
//    @Override
//    protected String completeOrderTask() {
//        //orderHome.getInstance().getOrderFees().clear();
//        if (orderHome.getInstance().isIncludeMiddleMan()) {
//
//            calcMiddleMoney();
//
//            if ((middleManFee.getMoney() == null) ||
//                    (BigDecimal.ZERO.compareTo(middleManFee.getMoney()) == 0)) {
//                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "includeMiddleManButMoneyIsZero");
//                return "fail";
//            }
//
//            //orderHome.getInstance().setMiddleTotal(middleManFee.getMoney());
//            orderHome.getInstance().getOrderFees().add(middleManFee);
//
//        }
//
//        for (OrderFee orderFee : orderFeeItemEdit.getOrderFeeList()) {
//            orderFee.setCustomerOrder(orderHome.getInstance());
//            orderHome.getInstance().getOrderFees().add(orderFee);
//        }
//
//        Logging.getLog(this.getClass()).debug("order fee call complete! fee size:" + orderHome.getInstance().getOrderFees().size());
//
//        if ("updated".equals(orderHome.update())) {
//            return "taskComplete";
//        } else
//            return "fail";
//
//
//    }
//
//
//    protected void initOrderTask() {
//        middleManIncludeLisntener();
//
//    }

}
