package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.BatchOperEntity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

import javax.persistence.Transient;
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

    private Boolean orderId;


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

    public Boolean getOrderId() {
        return orderId;
    }

    public void setOrderId(Boolean orderId) {
        this.orderId = orderId;
    }

//------------------------------------

    private boolean allCustomerSelected = false;

    private boolean allOrderSelected = false;

    private boolean allMiddleManSelected = false;

    public List<BatchOperData<Customer>> containCustomers;

    public List<BatchOperData<MiddleMan>> containMiddleMan;

    public List<OrderCalcItem> containOrders;


    public boolean isAllOrderSelected() {
        return allOrderSelected;
    }

    public void setAllOrderSelected(boolean allOrderSelected) {
        this.allOrderSelected = allOrderSelected;
    }

    public boolean isAllCustomerSelected() {
        return allCustomerSelected;
    }

    public void setAllCustomerSelected(boolean allCustomerSelected) {
        this.allCustomerSelected = allCustomerSelected;
    }

    public boolean isAllMiddleManSelected() {
        return allMiddleManSelected;
    }

    public void setAllMiddleManSelected(boolean allMiddleManSelected) {
        this.allMiddleManSelected = allMiddleManSelected;
    }

    private RebateProgram selectProgram;

    public RebateProgram getSelectProgram() {
        return selectProgram;
    }

    public void setSelectProgram(RebateProgram selectProgram) {
        this.selectProgram = selectProgram;
    }

    public void setAllOrderRebateProgram(){
        for (OrderCalcItem item: containOrders){
            if (item.isSelected()){
                item.setRebateProgram(selectProgram);
                item.init();
            }
        }
    }


    public void allMiddleManCheckChange(){
        //TODO
    }

    public void middleManCheckChange(){
        //TODO
    }

    public void allCustomerCheckChange() {
        for (BatchOperData<Customer> customer : getContainCustomers()) {
            customer.setSelected(allCustomerSelected);
        }
        refreshOrder();
    }

    public void customerCheckChange() {

        allCustomerSelected = true;
        for (BatchOperData<Customer> customer : getContainCustomers()) {
            if (!customer.isSelected()) {
                allCustomerSelected = false;
                break;
            }
        }

        refreshOrder();

    }


    public void allOrderCheckChange() {
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            order.setSelected(allOrderSelected);
            if (allOrderSelected) {
                order.getData().setMiddleMoney(BigDecimal.ZERO);
                order.getData().setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                order.getData().setMiddleTotal(null);
                order.getData().setMiddleRate(null);
            } else {
                order.getData().setMiddleMoney(null);
                order.getData().setMiddleMoneyCalcType(null);
                order.getData().setMiddleTotal(null);
                order.getData().setMiddleRate(null);
            }
        }
    }

    public void orderCheckChange() {
        allOrderSelected = true;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (!order.isSelected()) {
                allOrderSelected = false;
                break;
            }
        }
//        Set<Res> reses = genContainOrderReses();
//        Set<ResMiddleMoney> removeRmm = new HashSet<ResMiddleMoney>();
//        for (ResMiddleMoney rmm : getContainReses()) {
//            if (!reses.remove(rmm.getRes())) {
//                removeRmm.add(rmm);
//            }
//        }
//        getContainReses().removeAll(removeRmm);
//        for (Res res : reses) {
//            getContainReses().add(new ResMiddleMoney(res));
//        }
    }


    private List<BatchOperData<MiddleMan>> getContainMiddleMan(){
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainMiddleMan();
        return containMiddleMan;
    }

    private void initContainMiddleMan(){
        if (containMiddleMan == null){
            //TODO
        }
    }

    public List<BatchOperData<Customer>> getContainCustomers() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainCustomers();
        return containCustomers;
    }

    private void initContainCustomers() {
        if (containCustomers == null) {
            Set<Customer> customers = new HashSet<Customer>();
            for (CustomerOrder order : getResultList()) {
                // order.getCustomer().setSelected(false);
                customers.add(order.getCustomer());
            }
            containCustomers = new ArrayList<BatchOperData<Customer>>(BatchOperData.createBatchOperDataList(customers, false));
        }
    }

    public List<OrderCalcItem> getContainOrders() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainOrders();

        return containOrders;
    }

    private void initContainOrders() {
        if (containOrders == null) {
            initContainCustomers();
            containOrders = new ArrayList<OrderCalcItem>();
            for (CustomerOrder order : getResultList()) {
                for (BatchOperData<Customer> customer : containCustomers) {
                    if (customer.isSelected() && customer.getData().getId().equals(order.getCustomer().getId())) {
                        //order.setSelected(false);
                        order.setMiddleMoney(BigDecimal.ZERO);
                        order.setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                        order.setMiddleTotal(null);
                        order.setMiddleRate(null);
                        containOrders.add(new OrderCalcItem(order, false));
                        break;
                    }
                }
            }
        }
    }


    @Override
    public void refresh() {
        super.refresh();
        refreshMiddleMan();
        containCustomers = null;
        allCustomerSelected = false;
    }



    private void refreshOrder() {
        containOrders = null;
        allOrderSelected = false;
    }

    private void refreshMiddleMan(){
        refreshOrder();

        containMiddleMan = null;
        allMiddleManSelected = false;
    }

    public int getSelectOrderCount() {
        int result = 0;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected())
                result++;
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected())
                result = result.add(order.getData().getMoney());
        }
        return result;
    }

    public BigDecimal getContainOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            result = result.add(order.getData().getMoney());
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected() && (order.getData().getMiddleMoney() != null))
                result = result.add(order.getData().getMiddleMoney());
        }
        return result;
    }



    public static class OrderCalcItem extends BatchOperData<CustomerOrder> {

        private RebateProgram rebateProgram;

        private boolean calcItem = false;

        public OrderCalcItem(CustomerOrder data, boolean selected) {
            super(data, selected);
            if (getData().getCustomer().getMiddleMan() != null) {
                rebateProgram = getData().getCustomer().getMiddleMan().getRebateProgram();
            }
            init();
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

        private void init() {

            if (rebateProgram != null) {
                getData().setMiddleMoneyCalcType(rebateProgram.getOrderMode());
                getData().setMiddleRate(rebateProgram.getRebate());
                calcItem = rebateProgram.isCalcItem();

                for (NeedRes needRes : getData().getNeedReses()) {
                    for (OrderItem orderItem : needRes.getOrderItems()) {
                        if (calcItem) {
                            matchOrderItem(orderItem);
                        } else {
                            orderItem.setMiddleMoneyCalcType(OrderItemRebate.ItemRebateModel.NOT_CALC);
                            orderItem.setMiddleRate(null);
                            orderItem.setMiddleUnit(null);
                        }
                    }
                }

                calcOrderRebate();
            }
        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {
            this.rebateProgram = rebateProgram;
        }

        public boolean isCalcItem() {
            return calcItem;
        }

        public void setCalcItem(boolean calcItem) {
            this.calcItem = calcItem;
        }

        public void calcOrderRebate() {
            switch (getData().getMiddleMoneyCalcType()) {

                case NOT_CALC:
                    getData().setMiddleMoney(BigDecimal.ZERO);
                    break;
                case CONSULT_FIX:
                    getData().setMiddleMoney(getData().getMiddleRate());
                    break;
                case TOTAL_MONEY_RATE:
                    getData().setMiddleMoney(getData().getMoney().multiply(getData().getMiddleRate().divide(new BigDecimal("100"), Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP)));
                    break;
            }

            BigDecimal totalMiddleMoney = getData().getMiddleMoney();
            for (NeedRes needRes : getData().getNeedReses()) {
                for (OrderItem orderItem : needRes.getOrderItems()) {
                    if (calcItem && !orderItem.getMiddleMoneyCalcType().equals(OrderItemRebate.ItemRebateModel.NOT_CALC)) {
                        if (orderItem.getMiddleMoneyCalcType().equals(OrderItemRebate.ItemRebateModel.BY_COUNT)){
                            orderItem.setMiddleMoney(orderItem.getCountByResUnit(orderItem.getMiddleUnit()).multiply(orderItem.getRebate()));
                        }else{
                            orderItem.setMiddleMoney(orderItem.getTotalPrice().multiply(
                                    orderItem.getMiddleRate().divide(
                                            new BigDecimal("100"),Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(),BigDecimal.ROUND_HALF_UP)));
                        }
                        totalMiddleMoney = totalMiddleMoney.add(orderItem.getMiddleMoney());
                    } else {
                       orderItem.setMiddleMoney(BigDecimal.ZERO);
                    }
                }
            }

            getData().setMiddleTotal(totalMiddleMoney);
        }
    }

}
