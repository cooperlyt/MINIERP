package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Format;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 25/02/14
 * Time: 10:20
 */
public class FormatList extends ErpEntityQuery<Format>{

    private static final String EJBQL = "select format from Format format left join fetch format.storeRes";

    private static final String[] RESTRICTIONS = {
            "format.storeRes.res.id  in (#{customerList.resultAcceptAreaIds})",
            "customer.customerLevel.priority >= #{customerList.levelFrom}",};


    public FormatList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }
}
