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
public class CustomerMoneyTotal extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper left join fetch accountOper.customer customer ";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyTotal.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerMoneyTotal.searchDateArea.searchDateTo}"};


    public CustomerMoneyTotal() {
        setEjbql(EJBQL);

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("accountOper.operDate");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public Map<Customer, List<AccountOper>> getMoneyOperMap() {
        Map<Customer, List<AccountOper>> result = new HashMap<Customer, List<AccountOper>>();
        for (AccountOper accountOper : getResultList()) {
            List<AccountOper> aos = result.get(accountOper.getCustomer());
            if (aos == null) {
                aos = new ArrayList<AccountOper>();
                result.put(accountOper.getCustomer(), aos);
            }
            aos.add(accountOper);
        }
        return result;
    }
}