package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 18/03/14
 * Time: 09:43
 */
@Name("bankConfirm")
public class BankConfirm extends ErpEntityQuery<AccountOper>{

    protected static final String EJBQL = "select accountOper from AccountOper accountOper left join fetch accountOper.bankAccount ";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{bankConfirm.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{bankConfirm.searchDateArea.searchDateTo}",
            "accountOper.bankAccount.id = #{bankConfirm.bankId}"};

    public BankConfirm() {
        setEjbql(EJBQL);
        setRestrictionLogicOperator("and");
        setOrderColumn("accountOper.operDate");
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    private SearchDateArea searchDateArea = new SearchDateArea();


    private String bankId;

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
