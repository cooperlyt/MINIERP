package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 1/12/14.
 */
@Name("middleManRewardCalc")
@Scope(ScopeType.CONVERSATION)
public class MiddleManRewardCalc extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea left join fetch customer.middleMan";

    private static final String[] RESTRICTIONS = {
            "lower(customerOrder.id) like lower(concat('%',#{middleManRewardCalc.orderId},'%'))",
            "customerOrder.customer.middleMan.id = #{middleManHome.instance.id}",
            "lower(customerOrder.customer.name) like lower(concat('%',#{middleManRewardCalc.customerName},'%'))",
            "customerOrder.createDate >= #{middleManRewardCalc.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{middleManRewardCalc.searchDateArea.searchDateTo}",
            "customerOrder.canceled = #{middleManRewardCalc.canceled}",
            "customerOrder.allStoreOut = #{middleManRewardCalc.allStoreOut}",
            "customerOrder.resReceived = #{middleManRewardCalc.customerConfirm}",
            "customerOrder.middlePayed = #{middleManRewardCalc.containPayed}"};


    public MiddleManRewardCalc() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    private Boolean containPayed = false;

    private Boolean canceled = false;

    private Boolean allStoreOut;

    private Boolean customerConfirm;

    private String orderId;

    private String customerName;


    public Boolean getContainPayed() {
        return containPayed;
    }

    public void setContainPayed(Boolean containPayed) {
        this.containPayed = containPayed;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public Boolean getAllStoreOut() {
        return allStoreOut;
    }

    public void setAllStoreOut(Boolean allStoreOut) {
        this.allStoreOut = allStoreOut;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getCustomerConfirm() {
        return customerConfirm;
    }

    public void setCustomerConfirm(Boolean customerConfirm) {
        this.customerConfirm = customerConfirm;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


//------------------------------------


    public List<CustomerCalcItem> containCustomer;


    public List<TotalCalcData> getTotalCalcData(boolean containZero) {
        List<TotalCalcData> result = new ArrayList<TotalCalcData>();
        for (CustomerCalcItem citem : getContainCustomer()) {

            if (citem.isSelected() && (containZero || (citem.getRebate().compareTo(BigDecimal.ZERO) > 0)))
                for (OrderCalcItem oitem : citem.getOrders()) {

                    if (oitem.isSelected() && (containZero || (oitem.getRebate().compareTo(BigDecimal.ZERO) > 0))) {
                        boolean addItem = false;
                        for (OrderItem item : oitem.getOrderItems()) {
                            if (containZero || (item.getCalcMiddleMoney().compareTo(BigDecimal.ZERO) > 0)) {

                            }
                        }

                    }
                }
        }
        return result;
    }


    public class TotalCalcData {

        private OrderItem orderItem;

        private Customer customer;

        private BigDecimal orderRebateScalc;

        private BigDecimal orderRebateMoney;

        private RebateProgram.OrderRebateMode orderRebateMode;

        public TotalCalcData(OrderItem orderItem, Customer customer, BigDecimal orderRebateScalc, BigDecimal orderRebateMoney, RebateProgram.OrderRebateMode orderRebateMode) {
            this.orderItem = orderItem;
            this.customer = customer;
            this.orderRebateScalc = orderRebateScalc;
            this.orderRebateMoney = orderRebateMoney;
            this.orderRebateMode = orderRebateMode;
        }

        public OrderItem getOrderItem() {
            return orderItem;
        }

        public void setOrderItem(OrderItem orderItem) {
            this.orderItem = orderItem;
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public BigDecimal getOrderRebateScalc() {
            return orderRebateScalc;
        }

        public void setOrderRebateScalc(BigDecimal orderRebateScalc) {
            this.orderRebateScalc = orderRebateScalc;
        }

        public BigDecimal getOrderRebateMoney() {
            return orderRebateMoney;
        }

        public void setOrderRebateMoney(BigDecimal orderRebateMoney) {
            this.orderRebateMoney = orderRebateMoney;
        }

        public RebateProgram.OrderRebateMode getOrderRebateMode() {
            return orderRebateMode;
        }

        public void setOrderRebateMode(RebateProgram.OrderRebateMode orderRebateMode) {
            this.orderRebateMode = orderRebateMode;
        }
    }

    public boolean isAllCustomerSelected() {

        for (CustomerCalcItem item : getContainCustomer()) {
            if (!item.isSelected()) {
                return false;
            }
        }
        return true;
    }

    public void setAllCustomerSelected(boolean selected) {
        for (CustomerCalcItem item : getContainCustomer()) {
            item.setSelected(selected);
        }
    }

    private RebateProgram selectProgram;

    public RebateProgram getSelectProgram() {
        return selectProgram;
    }

    public void setSelectProgram(RebateProgram selectProgram) {
        this.selectProgram = selectProgram;
    }

    public void acceptAllOrderRebateProgram() {
        for (CustomerCalcItem item : getContainCustomer()) {
            if (item.isSelected()) {
                item.setRebateProgram(selectProgram);
            }
        }
    }


    public List<CustomerCalcItem> getContainCustomer() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainMiddleMan();
        return containCustomer;
    }


    private void initContainMiddleMan() {
        if (containCustomer == null) {
            Set<Customer> customers = new HashSet<Customer>();
            for (CustomerOrder order : getResultList()) {
                customers.add(order.getCustomer());
            }
            containCustomer = new ArrayList<CustomerCalcItem>();
            for (Customer middleMan : customers) {
                containCustomer.add(new CustomerCalcItem(middleMan, true));
            }
        }
    }


    @Override
    public void refresh() {
        super.refresh();

        containCustomer = null;
    }

//
//    public int getSelectOrderCount() {
//        int result = 0;
//        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
//            if (order.isSelected())
//                result++;
//        }
//        return result;
//    }
//
//    public BigDecimal getSelectOrderTotalMoney() {
//        BigDecimal result = BigDecimal.ZERO;
//        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
//            if (order.isSelected())
//                result = result.add(order.getData().getMoney());
//        }
//        return result;
//    }
//

    public long getOrderCount() {
        return getResultCount();
    }

    public int getCustomerCount() {
        return getContainCustomer().size();
    }


    public BigDecimal getContainOrderTotalPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerCalcItem item : getContainCustomer()) {
            if (item.isSelected())
                result = result.add(item.getTotalPrice());
        }
        return result;
    }


    public BigDecimal getTotalRebateMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerCalcItem item : getContainCustomer()) {
            if (item.isSelected())
                result = result.add(item.getRebate());
        }
        return result;
    }


    @Transactional
    public String saveCustomerOrder() {

        for (CustomerCalcItem customerItem : getContainCustomer()) {
            for (OrderCalcItem orderItem : customerItem.getOrders()) {
                orderItem.wirteToOrder();

            }
        }


        joinTransaction();
        getEntityManager().flush();
        refresh();
        return "updated";
    }


    public class CustomerCalcItem extends BatchOperData<Customer> {

        private boolean expanded = false;

        private RebateProgram rebateProgram;

        private List<OrderCalcItem> orderCalcItems;

        public CustomerCalcItem(Customer data, boolean selected) {
            super(data, selected);
            if (data.getMiddleMan() != null)
                rebateProgram = data.getMiddleMan().getRebateProgram();
            init();
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            for (OrderCalcItem item : orderCalcItems) {
                item.setSelected(selected);
            }
        }

        private void init() {
            orderCalcItems = new ArrayList<OrderCalcItem>();

            for (CustomerOrder order : getResultList()) {
                if (getData().getId().equals(order.getCustomer().getId())) {
                    orderCalcItems.add(new OrderCalcItem(order, true));
                }
            }
        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {
            this.rebateProgram = rebateProgram;
            for (OrderCalcItem item : orderCalcItems) {
                item.setRebateProgram(rebateProgram);
            }
        }

        public List<OrderCalcItem> getOrders() {
            if (isSelected()) {
                return orderCalcItems;
            } else {
                return new ArrayList<OrderCalcItem>(0);
            }
        }

        public BigDecimal getRebate() {
            BigDecimal result = BigDecimal.ZERO;
            if (isSelected()) {
                for (OrderCalcItem item : getOrders()) {
                    if (item.isSelected())
                        result = result.add(item.getRebate());
                }
            }
            return result;
        }


        public BigDecimal getTotalPrice() {
            BigDecimal result = BigDecimal.ZERO;
            for (OrderCalcItem item : orderCalcItems) {
                if (item.isSelected())
                    result = result.add(item.getData().getMoney());
            }
            return result;
        }


        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
    }


    public class OrderCalcItem extends BatchOperData<CustomerOrder> {

        private RebateProgram rebateProgram;

        private boolean calcItem = false;

        private boolean zeroItem = false;

        private List<OrderItem> items;

        // private List<NeedResCalcItem> needResCalcItems;

        public OrderCalcItem(CustomerOrder data, boolean selected) {
            super(data, selected);
            if (getData().getCustomer().getMiddleMan() != null) {
                rebateProgram = getData().getCustomer().getMiddleMan().getRebateProgram();
            }
            acceptRebateProgram();
        }


        public List<OrderItem> getOrderItems() {
            if (items == null) {
                if (isSelected()) {
                    if (zeroItem) {
                        items = getData().getOrderItemList();
                    } else {
                        items = getData().getNoZeroItemList();
                    }
                } else {
                    items = new ArrayList<OrderItem>(0);
                }
            }
            return items;
        }

        @Override
        public void setSelected(boolean selected) {
            if (selected != super.isSelected()) {
                items = null;
            }
            super.setSelected(selected);
        }

        private void matchOrderItem(OrderItem item) {
            OrderItemRebate resRebate = null;

            for (OrderItemRebate orderItemRebate : rebateProgram.getOrderItemRebates()) {
                if (orderItemRebate.getRes().equals(item.getRes())) {
                    resRebate = orderItemRebate;
                    break;
                }
            }
            if (resRebate == null) {
                item.setMiddleMoneyCalcType(OrderItemRebate.ItemRebateModel.NOT_CALC);
                item.setMiddleRate(null);
                item.setMiddleUnit(null);
                return;
            }

            StoreResRebate storeResRebate = null;

            for (StoreResRebate rebate : resRebate.getStoreResRebates()) {
                if (rebate.getStoreRes().equals(item.getStoreRes())) {
                    storeResRebate = rebate;
                    break;
                }
            }

            if (storeResRebate == null) {
                item.setMiddleMoneyCalcType(resRebate.getMode());
                item.setMiddleRate(resRebate.getRebate());
                item.setMiddleUnit(resRebate.getCalcUnit());
            } else {
                item.setMiddleMoneyCalcType(storeResRebate.getMode());
                item.setMiddleRate(storeResRebate.getRebate());
                item.setMiddleUnit(storeResRebate.getCalcUnit());
            }

            matchItemsSalerPrice(item);

        }

        private void matchItemsSalerPrice(OrderItem item) {
            if (OrderItemRebate.ItemRebateModel.BY_SUB.equals(item.getMiddleMoneyCalcType())) {
                SalerPrice resPrice = null;
                for (SalerPrice price : ((MiddleManHome) Component.getInstance(MiddleManHome.class)).getInstance().getSalerPrices()) {
                    if (item.getStoreRes().getRes().equals(price.getRes())) {
                        resPrice = price;
                        break;
                    }
                }
                if (resPrice == null) {
                    item.setMiddleRate(null);
                    item.setMiddleUnit(null);
                    return;
                }

                SalerStoreResPrice storeResPrice = null;
                for (SalerStoreResPrice price : resPrice.getSalerStoreResPrices()) {
                    if (price.getStoreRes().equals(item.getStoreRes())) {
                        storeResPrice = price;
                        break;
                    }
                }
                if (storeResPrice == null) {
                    item.setMiddleRate(resPrice.getPrice());
                    item.setMiddleUnit(resPrice.getResUnit());
                } else {
                    item.setMiddleRate(storeResPrice.getPrice());
                    item.setMiddleUnit(storeResPrice.getResUnit());
                }

            }
        }

        public void matchItemsSalerPrice() {
            for (OrderItem orderItem : getOrderItems()) {
                if ((orderItem.getMiddleMoneyCalcType() != null) &&
                        OrderItemRebate.ItemRebateModel.BY_SUB.equals(orderItem.getMiddleMoneyCalcType())) {
                    matchItemsSalerPrice(orderItem);
                }
            }
        }

        private void acceptRebateProgram() {
            if (rebateProgram != null) {
                getData().setMiddleMoneyCalcType(rebateProgram.getOrderMode());
                getData().setMiddleRate(rebateProgram.getRebate());
                setCalcItem(rebateProgram.isCalcItem());
                setZeroItem(rebateProgram.isZeroItem());

            } else {
                getData().setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                getData().setMiddleRate(null);
            }

            for (OrderItem orderItem : getOrderItems()) {
                if ((rebateProgram != null)) {
                    matchOrderItem(orderItem);
                } else {
                    orderItem.setMiddleMoneyCalcType(OrderItemRebate.ItemRebateModel.NOT_CALC);
                    orderItem.setMiddleRate(null);
                    orderItem.setMiddleUnit(null);
                    orderItem.setMiddleMoney(null);
                }
            }
        }


        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {

            this.rebateProgram = rebateProgram;
            acceptRebateProgram();

        }

        public boolean isCalcItem() {
            return calcItem;
        }

        public void setCalcItem(boolean calcItem) {
            if (calcItem != this.calcItem) {
                items = null;
            }
            this.calcItem = calcItem;
        }

        public boolean isZeroItem() {
            return zeroItem;
        }

        public void setZeroItem(boolean zeroItem) {
            if (zeroItem != this.zeroItem) {
                items = null;
            }
            this.zeroItem = zeroItem;
        }

        public void wirteToOrder() {
            getData().setMiddleMoney(getOrderRebate());
            getData().setMiddleTotal(getRebate());
            for (OrderItem item : getData().getOrderItemList()) {
                if (getOrderItems().contains(item)) {
                    item.setMiddleMoney(item.getCalcMiddleMoney());
                } else {
                    item.setMiddleMoney(BigDecimal.ZERO);
                }
            }
            getData().setMiddlePayed(true);
        }


        private BigDecimal getOrderRebate() {
            if (!isSelected()) {
                return BigDecimal.ZERO;
            }
            switch (getData().getMiddleMoneyCalcType()) {

                case NOT_CALC:
                    return BigDecimal.ZERO;
                case CONSULT_FIX:
                    if (getData().getMiddleRate() == null) {
                        return BigDecimal.ZERO;
                    } else
                        return getData().getMiddleRate();
                case TOTAL_MONEY_RATE:
                    if (getData().getMiddleRate() == null) {
                        return BigDecimal.ZERO;
                    } else
                        return getData().getMoney().multiply(getData().getMiddleRate().
                                divide(new BigDecimal("100"),
                                        Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP));
            }
            throw new IllegalArgumentException("nuknow type");
        }


        public BigDecimal getRebate() {
            if (!isSelected()) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalMiddleMoney = getOrderRebate();

            for (OrderItem orderItem : getOrderItems()) {
                totalMiddleMoney = totalMiddleMoney.add(orderItem.getCalcMiddleMoney());
            }

            return totalMiddleMoney;

        }

    }


}
