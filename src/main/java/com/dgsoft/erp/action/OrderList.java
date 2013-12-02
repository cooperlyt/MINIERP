package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/19/13
 * Time: 9:35 AM
 */
@Name("orderList")
public class OrderList extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea";

    private static final String[] RESTRICTIONS = {
            "customerOrder.orderEmp = #{orderList.customerOrder.orderEmp}",
            "customerOrder.customer.customerArea.id = #{orderList.customerAreaId}",
            "lower(customerOrder.customer.name) like lower(concat(#{orderList.customerName},'%'))",
            "customerOrder.createDate >= #{orderList.createDateFrom}",
            "customerOrder.createDate <= #{orderList.dateTo}"};

    @In
    private Credentials credentials;

    private CustomerOrder customerOrder = new CustomerOrder();

    private String customerName;

    private String customerAreaId;

    private Date createDateFrom;

    private Date createDateTo;



    //private Map<CustomerOrder.OrderState, Boolean> checkStates;


    public OrderList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrder("customerOrder.createDate");
        //setOrderDirection();
        customerOrder.setOrderEmp(((Credentials) Component.getInstance("org.jboss.seam.security.credentials")).getUsername());

    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public boolean isOnlyMyOrder() {
        return customerOrder.getOrderEmp() != null;
    }

    public void setOnlyMyOrder(boolean onlyMyOrder) {
        if (onlyMyOrder) {
            customerOrder.setOrderEmp(credentials.getUsername());
        } else {
            customerOrder.setOrderEmp(null);
        }
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getCreateDateFrom() {
        return createDateFrom;
    }

    public void setCreateDateFrom(Date createDateFrom) {
        this.createDateFrom = createDateFrom;
    }

    public Date getCreateDateTo() {
        return createDateTo;
    }

    public void setCreateDateTo(Date createDateTo) {
        this.createDateTo = createDateTo;
    }

    public Date getDateTo() {
        if (createDateTo == null) {
            return null;
        }
        return new Date(createDateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public Number getTotalMoney() {
        return getResultTotalSum("customerOrder.money");
    }

}
