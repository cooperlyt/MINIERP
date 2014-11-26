package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.MoneySave;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 11/26/14.
 */
public abstract class MoneySaveBaseHome extends ErpEntityHome<MoneySave> {
    @In
    protected FacesMessages facesMessages;
    @In
    protected Credentials credentials;
    protected Date operDate;
    @In(create = true)
    protected OrderHome orderHome;

    private SetLinkList<AccountOper> accountOpers;

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    @Override
    protected void initInstance() {
        super.initInstance();
        accountOpers = null;
    }

    @Override
    protected MoneySave createInstance() {
        MoneySave result = new MoneySave();
        result.setMoney(BigDecimal.ZERO);
        result.setRemitFee(BigDecimal.ZERO);
        return result;
    }

    public SetLinkList<AccountOper> getAccountOpers() {
        if (accountOpers == null) {
            accountOpers = new SetLinkList<AccountOper>(getInstance().getAccountOpers());
        }
        return accountOpers;
    }

    public AccountOper getEditingOper() {
        return accountOpers.get(0);
    }

    public BigDecimal getCustomerSaveReceiveMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper oper : getAccountOpers()) {
            result = result.add(oper.getAccountsReceivable());
        }
        return result;
    }

    public BigDecimal getCustomerProxyReceiveMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper oper : getAccountOpers()) {
            result = result.add(oper.getProxcAccountsReceiveable());
        }
        return result;
    }


    @Transactional()
    protected void calcCustomerOrderPayTag(){
        for(AccountOper oper: getAccountOpers()){
            oper.setOperDate(operDate);
            oper.calcCustomerMoney();
            if (oper.getOperType().equals(AccountOper.AccountOperType.PROXY_SAVINGS) &&
                    (oper.getCustomer().getProxyAccountMoney().compareTo(BigDecimal.ZERO) <= 0)){
                getEntityManager().createQuery("update CustomerOrder set payTag = true where accountChange = true and customer.id = :customerId and payType ='EXPRESS_PROXY' ").
                        setParameter("customerId", oper.getCustomer().getId()).executeUpdate();

            }
            if (oper.getOperType().equals(AccountOper.AccountOperType.CUSTOMER_SAVINGS) &&
                    (oper.getCustomer().getAccountMoney().compareTo(BigDecimal.ZERO) <= 0)){
                getEntityManager().createQuery("update CustomerOrder set payTag = true where accountChange = true and customer.id = :customerId and payType <> 'EXPRESS_PROXY' ").
                        setParameter("customerId", oper.getCustomer().getId()).executeUpdate();
            }
        }
    }


}
