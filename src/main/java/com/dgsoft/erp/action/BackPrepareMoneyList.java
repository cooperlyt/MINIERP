package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.BackPrepareMoney;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by cooper on 5/1/14.
 */
@Name("backPrepareMoneyList")
public class BackPrepareMoneyList extends ErpEntityQuery<BackPrepareMoney>{
    private static final String EJBQL = "select backPrepareMoney from BackPrepareMoney backPrepareMoney " +
            "left join fetch backPrepareMoney.accountOper accountOper left join fetch accountOper.customer " +
            "left join fetch accountOper.bankAccount ";

    private static final String[] RESTRICTIONS = {
            "backPrepareMoney.accountOper.operDate >= #{searchDateArea.dateFrom}",
            "backPrepareMoney.accountOper.operDate <= #{searchDateArea.searchDateTo}",
            "backPrepareMoney.accountOper.customer.customerArea.id in (#{customerSearchCondition.resultAcceptAreaIds})",
            "backPrepareMoney.accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "backPrepareMoney.accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "backPrepareMoney.accountOper.customer.type = #{customerSearchCondition.type}",
            "lower(backPrepareMoney.accountOper.customer.name) like lower(concat('%',#{customerSearchCondition.name},'%'))",
            "backPrepareMoney.accountOper.customer.provinceCode = #{customerSearchCondition.provinceCode}"};


    public BackPrepareMoneyList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("backPrepareMoney.accountOper.operDate");
    }

    public Number getTotalOperMoney(){
        return super.getResultTotalSum("backPrepareMoney.accountOper.operMoney");
    }

    public Number getTotalRemitFee(){
        return super.getResultTotalSum("backPrepareMoney.accountOper.remitFee");
    }

    public Number getTotalRealMoney(){
        if (getTotalRemitFee() == null){
            return getTotalOperMoney();
        }
        return new BigDecimal(getTotalOperMoney().doubleValue()).add(new BigDecimal(getTotalRemitFee().doubleValue()));
    }

}
