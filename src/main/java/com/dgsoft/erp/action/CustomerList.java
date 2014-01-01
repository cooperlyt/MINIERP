package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerArea;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/20/13
 * Time: 12:50 PM
 */
@Name("customerList")
public class CustomerList extends ErpEntityQuery<Customer> {

    private static final String EJBQL = "select customer from Customer customer left join fetch customer.customerLevel left join fetch customer.customerArea";

    private static final String[] RESTRICTIONS = {
            "customer.customerArea.id in (#{customerList.resultAcceptAreaIds})",
            "customer.customerLevel.priority >= #{customerList.levelFrom}",
            "customer.customerLevel.priority <= #{customerList.levelTo}",
            "lower(customer.name) like lower(concat(#{customerList.customer.name},'%'))",
            "customer.type = #{customerList.type}"};


    public CustomerList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    @In(create = true)
    private List<CustomerArea> mySaleArea;

    private String customerAreaId;

    private Integer levelFrom;

    private Integer levelTo;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private Customer customer = new Customer();

    public Integer getLevelTo() {
        return levelTo;
    }

    public void setLevelTo(Integer levelTo) {
        this.levelTo = levelTo;
    }

    public Integer getLevelFrom() {
        return levelFrom;
    }

    public void setLevelFrom(Integer levelFrom) {
        this.levelFrom = levelFrom;
    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public List<String> getResultAcceptAreaIds(){

        if ((customerAreaId == null) || ("".equals(customerAreaId.trim()))){
            List<String> result = new ArrayList<String>(mySaleArea.size());
            for (CustomerArea customerArea: mySaleArea){
                result.add(customerArea.getId());
            }
            return result;
        }else{
            List<String> result = new ArrayList<String>(1);
            result.add(customerAreaId);
            return result;
        }
    }

    public Customer getCustomer() {
        return customer;
    }

}
