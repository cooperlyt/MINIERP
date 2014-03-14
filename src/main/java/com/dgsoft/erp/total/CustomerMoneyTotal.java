package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;

/**
 * Created by cooper on 3/4/14.
 */
@Name("customerMoneyTotal")
@Scope(ScopeType.CONVERSATION)
public class CustomerMoneyTotal extends CustomerMoneyTotalBase {

    protected static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyTotal.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerMoneyTotal.searchDateArea.searchDateTo}"};


    public CustomerMoneyTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));

    }

}