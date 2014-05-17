package com.dgsoft.common.utils.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by cooper on 5/17/14.
 */
public interface Certificate {

    public abstract List<CertificateItem> getCertificateItems();

    public abstract BigDecimal getMoney();

    public String getMemo();

    public String getCashier();

    public String getCheckedEmp();

    public String getApprovedEmp();

    public String getPreparedEmp();

    public Date getDate();

    public int getCode();

    public String getWord();
}
