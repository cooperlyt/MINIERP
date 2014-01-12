package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

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

    private boolean allCustomerSelected = true;

    private boolean allOrderSelected = true;

    private CustomerOrder.MiddleMoneyCalcType orderCalcType;

    public List<Customer> containCustomers;

    public List<CustomerOrder> containOrders;

    public BigDecimal allOrderRate;

    public BigDecimal allOrderMiddleMoney;


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
        refreshOrder();
        for (Customer customer : getContainCustomers()) {
            if (!customer.isSelected()) {
                allCustomerSelected = false;
                return;
            }
        }
        allCustomerSelected = true;
    }

    public void allOrderCheckChange() {
        for (CustomerOrder order : getContainOrders()) {
            order.setSelected(allOrderSelected);
        }
        refreshRes();
    }

    public void orderCheckChange() {
        refreshRes();
        for (CustomerOrder order : getContainOrders()) {
            if (!order.isSelected()) {
                allOrderSelected = false;
                return;
            }
        }
        allOrderSelected = true;
    }


    private void refreshOrder() {
        containOrders = null;
        allOrderSelected = false;
        refreshRes();
    }

    private void refreshRes() {

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

    public BigDecimal getSelectOrderTotalMiddleMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (CustomerOrder order : getContainOrders()) {
            if (order.isSelected() && (order.getMiddleMoney() != null))
                result = result.add(order.getMiddleMoney());
        }
        return result;
    }

    @Override
    public void refresh() {

        super.refresh();
        containCustomers = null;
        containOrders = null;
        allOrderSelected = false;
        allCustomerSelected = false;


    }


}
