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
@Name("customerContactsMoneyTotal")
public class CustomerContactsMoneyTotal extends CustomerMoneyTotalBase {

    protected static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerContactsSearchCondition.searchDateArea.dateFrom}",
            "accountOper.operDate <= #{customerContactsSearchCondition.searchDateArea.searchDateTo}",
            "accountOper.operType in (#{customerContactsSearchCondition.searchAccountOperTypes})",
            "accountOper.customer.customerArea.id = #{customerSearchCondition.customerAreaId}",
            "accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "accountOper.customer.type = #{customerSearchCondition.type}",
            "accountOper.customer.provinceCode <= #{customerSearchCondition.provinceCode}",


    };


    public CustomerContactsMoneyTotal() {
        super();
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


}
