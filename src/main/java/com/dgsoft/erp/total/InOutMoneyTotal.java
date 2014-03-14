package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:06
 */
@Name("inOutMoneyTotal")
public class InOutMoneyTotal extends CustomerMoneyTotalBase{

    protected static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{inOutMoneyTotal.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{inOutMoneyTotal.searchDateArea.searchDateTo}",
            "accountOper.operType in (#{inOutMoneyTotal.showTypes})"};


    public InOutMoneyTotal(){
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    public List<AccountOper.AccountOperType> getShowTypes(){
         return new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.ORDER_SAVINGS,
                AccountOper.AccountOperType.PRE_DEPOSIT,
                AccountOper.AccountOperType.DEPOSIT_BACK,
                AccountOper.AccountOperType.ORDER_FREE,
                AccountOper.AccountOperType.ORDER_BACK));
    }


    public List<TotalDataGroup<Date,AccountOper>> getGroupResultList(){
        return TotalDataGroup.groupBy(getResultList(),new TotalGroupStrategy<Date, AccountOper>() {
            @Override
            public Date getKey(AccountOper accountOper) {
                return DataFormat.halfTime(accountOper.getOperDate());
            }
        });
    }
}
