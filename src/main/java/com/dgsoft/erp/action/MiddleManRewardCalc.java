package com.dgsoft.erp.action;

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

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea";

    private static final String[] RESTRICTIONS = {
            "customerOrder.customer.middleMan.id = #{middleManHome.instance.id}",
            "customerOrder.createDate >= #{middleManRewardCalc.calcDateFrom}",
            "customerOrder.createDate <= #{middleManRewardCalc.dateTo}",
            "customerOrder.canceled = #{middleManRewardCalc.canceled}",
            "customerOrder.allStoreOut = #{middleManRewardCalc.allStoreOut}",
            "customerOrder.resReceived = #{middleManRewardCalc.customerConfirm}",
            "customerOrder.moneyComplete = #{middleManRewardCalc.moneyComplete}",
            "customerOrder.middlePayed = #{middleManRewardCalc.containPayed}"};

    private boolean allCustomerSelected = false;

    private boolean allOrderSelected = false;

    private boolean allResSelected = false;

    private CustomerOrder.MiddleMoneyCalcType orderCalcType;

    public BigDecimal allOrderRate;

    public BigDecimal allOrderMiddleMoney;

    public List<Customer> containCustomers;

    public List<CustomerOrder> containOrders;

    public List<ResMiddleMoney> containReses;

    public MiddleManRewardCalc() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        //setMaxResults(100);
    }


    //--------------------contidion
    private Date calcDateFrom = new Date();

    private Date calcDateTo = new Date();

    private Boolean containPayed = false;

    private Boolean canceled = false;

    private Boolean allStoreOut;

    private Boolean moneyComplete = true;

    private Boolean customerConfirm;


    public Boolean getContainPayed() {
        return containPayed;
    }

    public void setContainPayed(Boolean containPayed) {
        this.containPayed = containPayed;
    }

    public Date getCalcDateFrom() {
        return calcDateFrom;
    }

    public void setCalcDateFrom(Date calcDateFrom) {
        this.calcDateFrom = calcDateFrom;
    }

    public Date getCalcDateTo() {
        return calcDateTo;
    }

    public void setCalcDateTo(Date calcDateTo) {
        this.calcDateTo = calcDateTo;
    }

    public Date getDateTo() {
        if (calcDateTo == null) {
            return null;
        }
        return new Date(calcDateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
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


    //------------------------------------

    public boolean isAllResSelected() {
        return allResSelected;
    }

    public void setAllResSelected(boolean allResSelected) {
        this.allResSelected = allResSelected;
    }

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

    public CustomerOrder.MiddleMoneyCalcType getOrderCalcType() {
        return orderCalcType;
    }

    public void setOrderCalcType(CustomerOrder.MiddleMoneyCalcType orderCalcType) {
        this.orderCalcType = orderCalcType;
    }


    public BigDecimal getAllOrderMiddleMoney() {
        return allOrderMiddleMoney;
    }

    public void setAllOrderMiddleMoney(BigDecimal allOrderMiddleMoney) {
        this.allOrderMiddleMoney = allOrderMiddleMoney;
    }

    public BigDecimal getAllOrderRate() {
        return allOrderRate;
    }

    public void setAllOrderRate(BigDecimal allOrderRate) {
        this.allOrderRate = allOrderRate;
    }

    public void calcAllOrderMiddleMoney() {
        for (CustomerOrder order : getContainOrders()) {
            if (order.isSelected()) {
                order.setMiddleMoneyCalcType(orderCalcType);
                order.setMiddleRate(allOrderRate);
                order.setMiddleMoney(allOrderMiddleMoney);
                order.calcOrderMiddleMoney();
            }
        }
    }


    public void allCustomerCheckChange() {
        for (Customer customer : getContainCustomers()) {
            customer.setSelected(allCustomerSelected);
        }
        refreshOrder();
    }

    public void customerCheckChange() {

        allCustomerSelected = true;
        for (Customer customer : getContainCustomers()) {
            if (!customer.isSelected()) {
                allCustomerSelected = false;
                break;
            }
        }

        refreshOrder();

    }

    public void allResCheckChange() {
        for (ResMiddleMoney rmm : getContainReses()) {
            rmm.setSelected(allResSelected);

        }
    }

    public void allOrderCheckChange() {
        for (CustomerOrder order : getContainOrders()) {
            order.setSelected(allOrderSelected);
            if (allOrderSelected) {
                order.setMiddleMoney(BigDecimal.ZERO);
                order.setMiddleMoneyCalcType(CustomerOrder.MiddleMoneyCalcType.NOT_CALC);
                order.setMiddleTotal(null);
                order.setMiddleRate(null);
            } else {
                order.setMiddleMoney(null);
                order.setMiddleMoneyCalcType(null);
                order.setMiddleTotal(null);
                order.setMiddleRate(null);
            }
        }
        refreshRes();
    }

    public void orderCheckChange() {
        allOrderSelected = true;
        for (CustomerOrder order : getContainOrders()) {
            if (!order.isSelected()) {
                allOrderSelected = false;
                break;
            }
        }
        Set<Res> reses = genContainOrderReses();
        Set<ResMiddleMoney> removeRmm = new HashSet<ResMiddleMoney>();
        for (ResMiddleMoney rmm : getContainReses()) {
            if (!reses.remove(rmm.getRes())) {
                removeRmm.add(rmm);
            }
        }
        getContainReses().removeAll(removeRmm);
        for (Res res : reses) {
            getContainReses().add(new ResMiddleMoney(res));
        }
    }


    private void refreshOrder() {
        containOrders = null;
        allOrderSelected = false;
        refreshRes();
    }

    private void refreshRes() {
        containReses = null;
        allResSelected = false;
        refreshItems();
    }

    private void refreshItems() {
        //TODO
    }

    public List<Customer> getContainCustomers() {
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
                order.getCustomer().setSelected(false);
                customers.add(order.getCustomer());
            }
            containCustomers = new ArrayList<Customer>(customers);
        }
    }

    public List<CustomerOrder> getContainOrders() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainOrders();

        return containOrders;
    }

    private void initContainOrders() {
        if (containOrders == null) {
            initContainCustomers();
            containOrders = new ArrayList<CustomerOrder>();
            for (CustomerOrder order : getResultList()) {
                for (Customer customer : containCustomers) {
                    if (customer.isSelected() && customer.getId().equals(order.getCustomer().getId())) {
                        order.setSelected(false);
                        order.setMiddleMoney(BigDecimal.ZERO);
                        order.setMiddleMoneyCalcType(CustomerOrder.MiddleMoneyCalcType.NOT_CALC);
                        order.setMiddleTotal(null);
                        order.setMiddleRate(null);
                        containOrders.add(order);
                        break;
                    }
                }
            }
        }
    }

    public int getSelectOrderCount() {
        int result = 0;
        for (CustomerOrder order : getContainOrders()) {
            if (order.isSelected())
                result++;
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerOrder order : getContainOrders()) {
            if (order.isSelected())
                result = result.add(order.getMoney());
        }
        return result;
    }

    public BigDecimal getContainOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerOrder order : getContainOrders()) {
            result = result.add(order.getMoney());
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerOrder order : getContainOrders()) {
            if (order.isSelected() && (order.getMiddleMoney() != null))
                result = result.add(order.getMiddleMoney());
        }
        return result;
    }

    public List<ResMiddleMoney> getContainReses() {
        if (isAnyParameterDirty()) {
            refresh();
        }

        initContainReses();

        return containReses;
    }

    private void initContainReses() {
        if (containReses == null) {

            containReses = new ArrayList<ResMiddleMoney>();
            for (Res res : genContainOrderReses()) {
                containReses.add(new ResMiddleMoney(res));
            }

        }
    }

    private Set<Res> genContainOrderReses() {
        initContainOrders();
        Set<Res> result = new HashSet<Res>();
        for (CustomerOrder order : containOrders) {
            if (order.isSelected()) {
                for (OrderItem orderItem : order.getAllOrderItemList()) {
                    //result.add(orderItem.getUseRes());
                }
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        super.refresh();
        refreshOrder();
        containCustomers = null;
        allCustomerSelected = false;
    }

    public static class ResMiddleMoney extends BatchOperEntity {

        private Res res;

        private OrderItem.MiddleMoneyCalcType calcType;

        private ResUnit calcUnit;

        private BigDecimal rate;

        public ResMiddleMoney(Res res) {
            this.res = res;
        }

        public Res getRes() {
            return res;
        }

        public OrderItem.MiddleMoneyCalcType getCalcType() {
            return calcType;
        }

        public void setCalcType(OrderItem.MiddleMoneyCalcType calcType) {
            this.calcType = calcType;
        }

        public ResUnit getCalcUnit() {
            return calcUnit;
        }

        public void setCalcUnit(ResUnit calcUnit) {
            this.calcUnit = calcUnit;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }

        @Override
        @Transient
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other == this) {
                return true;
            }

            if (!(other instanceof ResMiddleMoney)) {
                return false;
            }

            return getRes().equals(((ResMiddleMoney) other).getRes());

        }

        @Override
        @Transient
        public int hashCode() {
            return getRes().hashCode();
        }
    }


}
