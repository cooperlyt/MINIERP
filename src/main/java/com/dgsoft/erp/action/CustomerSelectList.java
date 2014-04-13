package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 4/13/14.
 */
@Name("customerSelectList")
public class CustomerSelectList extends ErpEntityQuery<Customer>{

    private static final String EJBQL = "select customer from Customer customer where customer.enable = true";

    private static final String[] RESTRICTIONS = {
            "customer.customerArea.id = #{customerSelectList.searchAreaId}"};

    @In(create = true)
    private CustomerAreaHome customerAreaHome;

    public CustomerSelectList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    public String getSearchAreaId(){
       if (customerAreaHome.isIdDefined()){
           return customerAreaHome.getInstance().getId();
       }else
           return null;
    }

}
