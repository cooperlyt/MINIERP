package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
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
            "lower(customerOrder.customer.middleMan.name) like lower(concat('%',#{middleManRewardCalc.middleManName},'%'))",
            "lower(customerOrder.customer.name) like lower(concat('%',#{middleManRewardCalc.customerName},'%'))",
            "customerOrder.createDate >= #{middleManRewardCalc.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{middleManRewardCalc.searchDateArea.searchDateTo}",
            "customerOrder.canceled = #{middleManRewardCalc.canceled}",
            "customerOrder.allStoreOut = #{middleManRewardCalc.allStoreOut}",
            "customerOrder.resReceived = #{middleManRewardCalc.customerConfirm}",
            "customerOrder.moneyComplete = #{middleManRewardCalc.moneyComplete}",
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

    private Boolean moneyComplete = true;

    private Boolean customerConfirm;

    private String orderId;

    private String customerName;

    private String middleManName;


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

    public Boolean getMoneyComplete() {
        return moneyComplete;
    }

    public void setMoneyComplete(Boolean moneyComplete) {
        this.moneyComplete = moneyComplete;
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

    public String getMiddleManName() {
        return middleManName;
    }

    public void setMiddleManName(String middleManName) {
        this.middleManName = middleManName;
    }
//------------------------------------

    public List<MiddleCalcItem> containMiddleMan;


    public boolean isAllMiddleManSelected() {

        for (MiddleCalcItem item : getContainMiddleMan()) {
            if (!item.isSelected()) {
                return false;
            }
        }
        return true;
    }

    public void setAllMiddleManSelected(boolean allMiddleManSelected) {
        for (MiddleCalcItem item : getContainMiddleMan()) {
            item.setSelected(allMiddleManSelected);
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
        for (MiddleCalcItem item : containMiddleMan) {
            if (item.isSelected()) {
                item.setRebateProgram(selectProgram);
            }
        }
    }


    public List<MiddleCalcItem> getContainMiddleMan() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainMiddleMan();
        return containMiddleMan;
    }


    private void initContainMiddleMan() {
        if (containMiddleMan == null) {
            Set<MiddleMan> middleMans = new HashSet<MiddleMan>();
            for (CustomerOrder order : getResultList()) {
                middleMans.add(order.getCustomer().getMiddleMan());
            }
            containMiddleMan = new ArrayList<MiddleCalcItem>();
            for (MiddleMan middleMan : middleMans) {
                containMiddleMan.add(new MiddleCalcItem(middleMan, true));
            }
        }
    }


    @Override
    public void refresh() {
        super.refresh();

        containMiddleMan = null;
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

    public int getMiddleManCount() {
        return containMiddleMan.size();
    }

    public int getCustomerCount() {
        int result = 0;
        for (MiddleCalcItem item : containMiddleMan) {
            result += item.customerItems.size();
        }
        return result;
    }


    public BigDecimal getContainOrderTotalPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (MiddleCalcItem item : containMiddleMan) {
            result = result.add(item.getTotalPrice());
        }
        return result;
    }


    public BigDecimal getTotalRebateMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (MiddleCalcItem item : containMiddleMan) {
            if (item.isSelected())
                result = result.add(item.getRebate());
        }
        return result;
    }


    @Transactional
    public String saveCustomerOrder(){
        for (MiddleCalcItem middleItem : containMiddleMan) {
            if (middleItem.isSelected()){
                for (CustomerCalcItem customerItem: middleItem.getCustomers()){
                    for (OrderCalcItem orderItem: customerItem.getOrders()){
                        orderItem.wirteToOrder();

                    }
                }
            }
        }

        joinTransaction();
        getEntityManager().flush();
        refresh();
        return "updated";
    }

    public class MiddleCalcItem extends BatchOperData<MiddleMan> {

        private RebateProgram rebateProgram;

        List<CustomerCalcItem> customerItems;

        public MiddleCalcItem(MiddleMan data, boolean selected) {
            super(data, selected);
            if (data != null)
                rebateProgram = data.getRebateProgram();
            init();
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            for (CustomerCalcItem item : customerItems) {
                item.setSelected(selected);
            }
        }

        private void init() {
            customerItems = new ArrayList<CustomerCalcItem>();
            Set<Customer> customers = new HashSet<Customer>();
            for (CustomerOrder order : getResultList()) {
                if ((order.getCustomer().getMiddleMan() == null) && (getData() == null)) {
                    customers.add(order.getCustomer());
                } else if ((getData() != null) && (getData().equals(order.getCustomer().getMiddleMan()))) {
                    customers.add(order.getCustomer());
                }
            }
            for (Customer customer : customers) {
                customerItems.add(new CustomerCalcItem(customer, true));
            }
        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {
            this.rebateProgram = rebateProgram;
            for (CustomerCalcItem item : customerItems) {
                item.setRebateProgram(rebateProgram);
            }
        }

        public List<CustomerCalcItem> getCustomers() {
            if (isSelected()) {
                return customerItems;
            } else {
                return new ArrayList<CustomerCalcItem>(0);
            }
        }

        public BigDecimal getTotalPrice() {
            BigDecimal result = BigDecimal.ZERO;
            for (CustomerCalcItem item : customerItems) {
                result = result.add(item.getTotalPrice());
            }
            return result;
        }


        public BigDecimal getRebate() {
            BigDecimal result = BigDecimal.ZERO;
            if (isSelected()) {
                for (CustomerCalcItem item : customerItems) {
                    if (item.isSelected())
                        result = result.add(item.getRebate());
                }
            }
            return result;
        }
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

        private boolean patchItem = false;

        private List<NeedResCalcItem> needResCalcItems;

        public OrderCalcItem(CustomerOrder data, boolean selected) {
            super(data, selected);
            if (getData().getCustomer().getMiddleMan() != null) {
                rebateProgram = getData().getCustomer().getMiddleMan().getRebateProgram();
            }
            init();
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            for (NeedResCalcItem item : needResCalcItems) {
                item.setSelected(selected);
            }
        }

        private void acceptRebateProgram() {
            if (rebateProgram != null) {
                getData().setMiddleMoneyCalcType(rebateProgram.getOrderMode());
                getData().setMiddleRate(rebateProgram.getRebate());
                calcItem = rebateProgram.isCalcItem();
                patchItem = rebateProgram.isPatchItem();
            } else {
                getData().setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                getData().setMiddleRate(null);
                calcItem = true;
                patchItem = true;
            }
        }

        private void init() {
            needResCalcItems = new ArrayList<NeedResCalcItem>();

            for (NeedRes needRes : getData().getNeedResList()) {
                needResCalcItems.add(new NeedResCalcItem(needRes, true));
            }
            acceptRebateProgram();
            // calcOrderRebate();

        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {

            this.rebateProgram = rebateProgram;
            acceptRebateProgram();
            for (NeedResCalcItem item : needResCalcItems) {
                item.setRebateProgram(rebateProgram);
            }
        }

        public boolean isCalcItem() {
            return calcItem;
        }

        public void setCalcItem(boolean calcItem) {
            this.calcItem = calcItem;
        }


        public boolean isPatchItem() {
            return patchItem;
        }

        public void setPatchItem(boolean patchItem) {
            this.patchItem = patchItem;
        }

        public void wirteToOrder() {
            getData().setMiddleMoney(getOrderRebate());
            getData().setMiddleTotal(getRebate());
            for (NeedResCalcItem item: needResCalcItems){
                item.wirteToOrder();
            }
            getData().setMiddlePayed(true);
            // for ()
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

        public List<NeedResCalcItem> getNeedReses() {
            if (isSelected() && calcItem) {
                if (patchItem) {
                    return needResCalcItems;
                } else {
                    for (NeedResCalcItem item : needResCalcItems) {
                        if (item.getData().getType().equals(NeedRes.NeedResType.ORDER_SEND)) {
                            List<NeedResCalcItem> result = new ArrayList<NeedResCalcItem>(1);
                            result.add(item);
                            return result;
                        }
                    }
                    throw new IllegalArgumentException("order not have Send needres");
                }
            } else {
                return new ArrayList<NeedResCalcItem>(0);
            }

        }

        public BigDecimal getRebate() {
            if (!isSelected()) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalMiddleMoney = getOrderRebate();

            for (NeedResCalcItem needRes : getNeedReses()) {
                if (needRes.isSelected())
                    totalMiddleMoney = totalMiddleMoney.add(needRes.getRebate());
            }

            return totalMiddleMoney;

        }
    }

    public class NeedResCalcItem extends BatchOperData<NeedRes> {

        private RebateProgram rebateProgram;

        private boolean zeroItem = false;

        public NeedResCalcItem(NeedRes data, boolean selected) {
            super(data, selected);
            if (data.getCustomerOrder().getCustomer().getMiddleMan() != null) {
                rebateProgram = data.getCustomerOrder().getCustomer().getMiddleMan().getRebateProgram();
            }
            acceptProgram();
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
        }

        private void acceptProgram() {

            for (OrderItem orderItem : getData().getOrderItems()) {
                if ((rebateProgram != null)) {
                    matchOrderItem(orderItem);
                    zeroItem = rebateProgram.isZeroItem();
                } else {
                    orderItem.setMiddleMoneyCalcType(OrderItemRebate.ItemRebateModel.NOT_CALC);
                    orderItem.setMiddleRate(null);
                    orderItem.setMiddleUnit(null);
                    orderItem.setMiddleMoney(null);
                    zeroItem = false;
                }
            }
        }

        public boolean isZeroItem() {
            return zeroItem;
        }

        public void setZeroItem(boolean zeroItem) {
            this.zeroItem = zeroItem;
        }

        public BigDecimal getTotalPrice() {
            BigDecimal result = BigDecimal.ZERO;
            for (OrderItem item : getData().getOrderItemList()) {
                result = result.add(item.getTotalPrice());
            }
            return result;
        }

        public List<OrderItem> getOrderItems() {
            if (isSelected()) {
                if (zeroItem) {
                    return getData().getOrderItemList();
                } else {
                    return getData().getNoZeroItemList();
                }
            } else {
                return new ArrayList<OrderItem>(0);
            }
        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {
            this.rebateProgram = rebateProgram;
            acceptProgram();
        }


        public void wirteToOrder() {
            for (OrderItem item: getData().getOrderItems()){
                item.setMiddleMoney(item.getCalcMiddleMoney());
            }
        }

        public BigDecimal getRebate() {

            BigDecimal totalMiddleMoney = BigDecimal.ZERO;
            if (isSelected()) {
                for (OrderItem orderItem : getOrderItems()) {
                    totalMiddleMoney = totalMiddleMoney.add(orderItem.getCalcMiddleMoney());
                }
            }
            return totalMiddleMoney;

        }

    }

}
