package com.dgsoft.erp.business;

import com.dgsoft.erp.action.CustomerHome;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created by cooper on 5/9/14.
 */
@Name("customerAccountOper")
@Scope(ScopeType.CONVERSATION)
public class CustomerAccountOper implements Serializable {

    @Factory(value = "moneySaveTypes", scope = ScopeType.CONVERSATION)
    public EnumSet<AccountOper.AccountOperType> getMoneySaveTypes() {
        return EnumSet.of(AccountOper.AccountOperType.PROXY_SAVINGS,
                AccountOper.AccountOperType.CUSTOMER_SAVINGS,
                AccountOper.AccountOperType.MONEY_FREE,
                AccountOper.AccountOperType.DEPOSIT_PAY);
    }

    @In
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    @In
    private EntityManager erpEntityManager;

    private BigDecimal operMoney;

    private Date operDate;

    private AccountOper.AccountOperType type;

    public AccountOper.AccountOperType getType() {
        return type;
    }

    public void setType(AccountOper.AccountOperType type) {
        this.type = type;
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public BigDecimal getOperMoney() {
        return operMoney;
    }

    public void setOperMoney(BigDecimal operMoney) {
        this.operMoney = operMoney;
    }

    public String doOper() {
        AccountOper accountOper = new AccountOper(getType(), customerHome.getReadyInstance(), credentials.getUsername());
        accountOper.setOperDate(getOperDate());

        if (getType().equals(AccountOper.AccountOperType.DEPOSIT_PAY)) {
            accountOper.setAdvanceReceivable(getOperMoney());
            accountOper.setAccountsReceivable(getOperMoney());
            accountOper.calcCustomerMoney();

        } else if (getType().equals(AccountOper.AccountOperType.MONEY_FREE)) {
            accountOper.setAccountsReceivable(getOperMoney());
            accountOper.calcCustomerMoney();
        } else {
            throw new IllegalArgumentException("doOper unkonw operType:" + getType());
        }


        erpEntityManager.persist(accountOper);
        erpEntityManager.flush();
        return "persisted";
    }

}
