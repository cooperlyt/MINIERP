package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.*;

/**
 * Created by cooper on 3/2/14.
 */
@Name("customerMoneyConfirm")
public class CustomerMoneyConfirm extends CustomerMoneyTotalBase {

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyConfirm.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerMoneyConfirm.searchDateArea.searchDateTo}",
            "accountOper.operType in (#{customerMoneyConfirm.showAccountTypes})",
            "accountOper.customer.id = #{customerMoneyConfirm.coustomerId}"};


    public CustomerMoneyConfirm() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


    private String coustomerId;

    public String getCoustomerId() {
        return coustomerId;
    }

    public void setCoustomerId(String coustomerId) {
        this.coustomerId = coustomerId;
    }

    public List<AccountOper.AccountOperType> getShowAccountTypes(){
        return new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.ORDER_SAVINGS,
                AccountOper.AccountOperType.PRE_DEPOSIT,AccountOper.AccountOperType.DEPOSIT_BACK,
                AccountOper.AccountOperType.ORDER_BACK,AccountOper.AccountOperType.ORDER_CANCEL_BACK));
    }

}
