package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:06
 */
@Name("customerContactsMoneyTotal")
public class CustomerContactsMoneyTotal extends CustomerMoneyTotalBase {


    protected static final String MEJBQL = "select accountOper from AccountOper accountOper " +
            "where (accountOper.operType = 'DEPOSIT_BACK' or accountOper.operType = 'PROXY_SAVINGS' " +
            " or accountOper.operType = 'CUSTOMER_SAVINGS' or ( accountOper.operType = 'ORDER_BACK' and accountOper.advanceReceivable = 0))";


    protected static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{searchDateArea.dateFrom}",
            "accountOper.operDate <= #{searchDateArea.searchDateTo}",
            "accountOper.operType in (#{customerMoneyCondition.searchAccountOperTypes})",
            "accountOper.customer.customerArea.id in (#{customerSearchCondition.resultAcceptAreaIds})",
            "accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "accountOper.customer.type = #{customerSearchCondition.type}",
            "accountOper.customer.provinceCode <= #{customerSearchCondition.provinceCode}",
            "lower(accountOper.customer.name)  like lower(concat('%',#{customerSearchCondition.name},'%'))"
    };


    private boolean groupByDay = false;

    public boolean isGroupByDay() {
        return groupByDay;
    }

    public void setGroupByDay(boolean groupByDay) {
        this.groupByDay = groupByDay;
    }

    public CustomerContactsMoneyTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setEjbql(MEJBQL);
    }



}
