package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by cooper on 3/2/14.
 */
@Name("customerMoneyConfirm")
public class CustomerMoneyConfirm extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyConfirm.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerMoneyConfirm.searchDateArea.searchDateTo}",
            "accountOper.customer.id = #{customerMoneyConfirm.coustomerId}"};


    public CustomerMoneyConfirm() {
        setEjbql(EJBQL);

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("accountOper.operDate");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    private String coustomerId;

    public String getCoustomerId() {
        return coustomerId;
    }

    public void setCoustomerId(String coustomerId) {
        this.coustomerId = coustomerId;
    }


}
