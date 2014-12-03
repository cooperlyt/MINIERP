package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 12/2/14.
 */
@Name("orderResTotalList")
public class OrderResTotalList extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea where customerOrder.canceled <> true";

    private static final String[] RESTRICTIONS = {
            "customerOrder.customer.id = #{customerHome.instance.id}",
            "customerOrder.createDate >= #{customerStoresTotalConditions.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{customerStoresTotalConditions.searchDateArea.searchDateTo}",
            "customerOrder.allStoreOut = #{customerStoresTotalConditions.storeChangeCondition}"};


    public OrderResTotalList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(20);
    }


    public Number getTotalRebate(){
        return getResultTotalSum("customerOrder.totalRebateMoney");
    }

    public Number getTotalMoney(){
        return getResultTotalSum("customerOrder.money");
    }

}
