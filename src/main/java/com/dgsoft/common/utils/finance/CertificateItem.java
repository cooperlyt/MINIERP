package com.dgsoft.common.utils.finance;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-14
 * Time: 上午9:08
 */
public interface CertificateItem {

    public String getDescription();

    public String getAccountCode();

    public BigDecimal getDebit();

    public BigDecimal getCredit();
}
