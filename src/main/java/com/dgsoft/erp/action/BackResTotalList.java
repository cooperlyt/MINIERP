package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.OrderBack;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 12/3/14.
 */
@Name("backResTotalList")
public class BackResTotalList extends ErpEntityQuery<OrderBack>{

    private static final String EJBQL = "select orderBack from OrderBack orderBack left join fetch orderBack.customer customer left join fetch customer.customerArea customerArea ";

    private static final String[] RESTRICTIONS = {
            "orderBack.customer.id = #{customerHome.instance.id}",
            "orderBack.createDate >= #{customerStoresTotalConditions.searchDateArea.dateFrom}",
            "orderBack.createDate <= #{customerStoresTotalConditions.searchDateArea.searchDateTo}",
            "orderBack.confirmed = #{customerStoresTotalConditions.storeChangeCondition}"};


    public BackResTotalList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(20);
    }


    public Number getTotalMoney(){
        return getResultTotalSum("orderBack.money");
    }

}
