package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:21
 */
public abstract class CustomerMoneyTotalBase extends ErpEntityQuery<AccountOper> {

    protected static final String EJBQL = "select accountOper from AccountOper accountOper left join fetch accountOper.customer customer ";

    public CustomerMoneyTotalBase() {
        setEjbql(EJBQL);
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
