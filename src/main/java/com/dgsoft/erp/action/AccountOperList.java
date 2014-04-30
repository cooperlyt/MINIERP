package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-30
 * Time: 下午1:37
 */
@Name("accountOperList")
public class AccountOperList extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{searchDateArea.dateFrom}",
            "accountOper.operDate <= #{searchDateArea.searchDateTo}",
            "accountOper.customer.id = #{customerHome.instance.id}"};


    public AccountOperList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("accountOper.operDate");
    }


}
