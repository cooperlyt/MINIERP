package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 5/12/14.
 */
@Name("customerMoneyOperList")
public class CustomerMoneyOperList extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper " +
            "left join fetch accountOper.customer left join fetch accountOper.moneySave moneySave " +
            "left join fetch moneySave.bankAccount left join fetch accountOper.saleCertificate  ";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{searchDateArea.dateFrom}",
            "accountOper.operDate <= #{searchDateArea.searchDateTo}",
            "accountOper.operType in (#{customerMoneyCondition.searchAccountOperTypes})",

            "accountOper.customer.customerArea.id = #{customerSearchCondition.customerAreaId}",
            "accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "lower(accountOper.customer.name) like lower(concat(#{customerSearchCondition.name},'%'))",
            "accountOper.customer.type = #{customerSearchCondition.type}",
            "accountOper.customer.provinceCode = #{customerSearchCondition.provinceCode}"};


    public CustomerMoneyOperList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("accountOper.operDate");
    }


}
