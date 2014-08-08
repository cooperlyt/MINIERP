package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 8/7/14.
 */
@Name("proxyOrderSave")
public class ProxyOrderSave extends ErpEntityQuery<CustomerOrder>{

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea where customerOrder.payType = 'EXPRESS_PROXY' and customerOrder.canceled = false";

    private static final String[] RESTRICTIONS = {

            "customerOrder.customer.customerArea.id = #{proxyOrderSave.customerAreaId}",
            "lower(customerOrder.customer.name) like lower(concat(#{proxyOrderSave.customerName},'%'))",
            "customerOrder.createDate >= #{searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{searchDateArea.searchDateTo}",
            "lower(customerOrder.id)  like lower(concat('%',#{proxyOrderSave.orderId}))",
            "customerOrder.payTag = #{proxyOrderSave.payed}"};


    public ProxyOrderSave() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("customerOrder.createDate");
        setOrderDirection("desc");

    }

    private boolean onlyNoPayed = true;


    private String customerAreaId;

    private String orderId;


    private String customerName;

    public Boolean getPayed(){
        if (onlyNoPayed){
            return false;
        }else{
            return null;
        }
    }

    public boolean isOnlyNoPayed() {
        return onlyNoPayed;
    }

    public void setOnlyNoPayed(boolean onlyNoPayed) {
        this.onlyNoPayed = onlyNoPayed;
    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
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
}
