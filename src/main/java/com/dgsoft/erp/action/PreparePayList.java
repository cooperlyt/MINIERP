package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.PreparePay;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by cooper on 5/1/14.
 */
@Name("preparePayList")
public class PreparePayList extends ErpEntityQuery<PreparePay>{

    private static final String EJBQL = "select preparePay from PreparePay preparePay " +
            "left join fetch preparePay.accountOper accountOper left join fetch accountOper.customer " +
            "left join fetch accountOper.bankAccount ";

    private static final String[] RESTRICTIONS = {
            "preparePay.accountOper.operDate >= #{searchDateArea.dateFrom}",
            "preparePay.accountOper.operDate <= #{searchDateArea.searchDateTo}",

            "preparePay.accountOper.customer.customerArea.id in (#{customerSearchCondition.resultAcceptAreaIds})",
            "preparePay.accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "preparePay.accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "preparePay.accountOper.customer.type = #{customerSearchCondition.type}",
            "lower(preparePay.accountOper.customer.name) like lower(concat('%',#{customerSearchCondition.name},'%'))",
            "preparePay.accountOper.customer.provinceCode = #{customerSearchCondition.provinceCode}"};


    public PreparePayList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("preparePay.accountOper.operDate");
    }

    public Number getTotalOperMoney(){
       return super.getResultTotalSum("preparePay.accountOper.operMoney");
    }

    public Number getTotalRemitFee(){
        return super.getResultTotalSum("preparePay.accountOper.remitFee");
    }

    public Number getTotalRealMoney(){
        if ( getTotalRemitFee() == null){
            return getTotalOperMoney();
        }
        return new BigDecimal(getTotalOperMoney().doubleValue()).subtract(new BigDecimal(getTotalRemitFee().doubleValue()));
    }
}
