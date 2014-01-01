package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerContact;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 1/1/14
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("customerContactList")
public class CustomerContactList extends ErpEntityQuery<CustomerContact>{

    private static final String EJBQL = "select customerContact from CustomerContact customerContact left join fetch customerContact.customer";

    private static final String[] RESTRICTIONS = {
            "lower(customerContact.customer.name) like lower(concat(#{customerContactList.customerName},'%'))",
            "lower(customerContact.name) like lower(concat(#{customerContactList.customerContact.name},'%'))",
            "lower(customerContact.tel) like lower(concat(#{customerContactList.customerContact.tel},'%'))",
            "customerContact.type = #{customerContactList.type}"};



    public CustomerContactList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    private CustomerContact customerContact = new CustomerContact();

    private String customerName;

    private String type;

    public CustomerContact getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(CustomerContact customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
