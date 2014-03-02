package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by cooper on 3/2/14.
 */
@Name("customerMoneyConfirm")
public class CustomerMoneyConfirm extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{customerMoneyConfirm.dateFrom}",
            "accountOper.operDate <= #{customerMoneyConfirm.searchDateTo}",
            "accountOper.customer.id = #{customerMoneyConfirm.coustomerId}"};


    public CustomerMoneyConfirm() {
        setEjbql(EJBQL);

        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("accountOper.operDate");
    }

    private Date dateFrom;

    private Date dateTo;

    private String coustomerId;

    public String getCoustomerId() {
        return coustomerId;
    }

    public void setCoustomerId(String coustomerId) {
        this.coustomerId = coustomerId;
    }

    public Date getSearchDateTo() {
        if (dateTo == null) {
            return null;
        }
        return new Date(dateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }
}
