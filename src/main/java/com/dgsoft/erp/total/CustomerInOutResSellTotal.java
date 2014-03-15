package com.dgsoft.erp.total;

import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 3/15/14.
 */
@Name("customerInOutResSellTotal")
public class CustomerInOutResSellTotal extends CustomerResSellTotal {


    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{inOutMoneyTotal.searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{inOutMoneyTotal.searchDateArea.searchDateTo}"};

    public CustomerInOutResSellTotal() {
        super();

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }


}
