package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.business.CustomerAccountOper;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.MoneySave;
import com.dgsoft.erp.model.TransCorp;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-9
 * Time: 下午4:51
 */
@Name("moneySaveHome")
public class MoneySaveHome extends ErpEntityHome<MoneySave> {

    private SetLinkList<AccountOper> accountOperList;

    //private BigDecimal toAccountMoney;

    private TransCorp transCorp;

    @In
    private FacesMessages facesMessages;

    @In(required = false)
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    @In
    private CustomerAccountOper customerAccountOper;


    @Override
    protected void initInstance() {
        super.initInstance();
        accountOperList = new SetLinkList<AccountOper>(getInstance().getAccountOpers());
    }

    public BigDecimal getOperMoney() {
        if (customerAccountOper.getOperMoney() == null) {
            return null;
        } else if (getInstance().getRemitFee() == null) {
            return customerAccountOper.getOperMoney();
        } else if (customerAccountOper.getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS) || customerAccountOper.getType().equals(AccountOper.AccountOperType.CUSTOMER_SAVINGS)) {
            return customerAccountOper.getOperMoney().add(getInstance().getRemitFee());
        } else if (customerAccountOper.getType().equals(AccountOper.AccountOperType.DEPOSIT_BACK)) {
            return customerAccountOper.getOperMoney().subtract(getInstance().getRemitFee());
        } else
            return customerAccountOper.getOperMoney();

    }

    public SetLinkList<AccountOper> getAccountOperList() {
        return accountOperList;
    }

    public AccountOper getSingleAccountOper() {
        return accountOperList.get(0);
    }

    public TransCorp getTransCorp() {
        return transCorp;
    }

    public void setTransCorp(TransCorp transCorp) {
        this.transCorp = transCorp;
    }

    public void addProxyItem() {
        for (AccountOper oper: getAccountOperList()){
            if (oper.getCustomer().getId().equals(customerHome.getInstance().getId())){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"CustomerExistsInOperList");
                return;
            }
        }
        getAccountOperList().add(new AccountOper(getInstance(), AccountOper.AccountOperType.PROXY_SAVINGS, customerHome.getInstance(), credentials.getUsername()));
    }

    private String removeProxyCustomerId;

    public String getRemoveProxyCustomerId() {
        return removeProxyCustomerId;
    }

    public void setRemoveProxyCustomerId(String removeProxyCustomerId) {
        this.removeProxyCustomerId = removeProxyCustomerId;
    }

    public void removeProxyItem(){
        for (AccountOper oper: getAccountOperList()){
            if (oper.getCustomer().getId().equals(removeProxyCustomerId)){
                getAccountOperList().remove(oper);
                return;
            }
        }
    }

    public BigDecimal getTotalCustomerPorxyMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper accountOper : getAccountOperList()) {
            result = result.add(accountOper.getCustomer().getProxyAccountMoney());
        }
        return result;
    }

    public BigDecimal getTotalReceiveProxyMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper accountOper : getAccountOperList()) {
            result = result.add(accountOper.getProxcAccountsReceiveable());
        }
        return result;
    }


    public void initAccountOper() {
        getInstance();
        getAccountOperList().clear();

        switch (customerAccountOper.getType()) {

            case DEPOSIT_BACK:
            case CUSTOMER_SAVINGS:
                getAccountOperList().add(new AccountOper(customerAccountOper.getType(), credentials.getUsername()));
                break;
            case PROXY_SAVINGS:
                break;

        }
    }

    public String checkProxyMoney() {
        if (getTotalReceiveProxyMoney().compareTo(getOperMoney()) != 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "proxyMoneyNotBalance", getOperMoney(), getTotalReceiveProxyMoney());
            return null;
        }


        return "next";
    }

    @Override
    protected boolean wire() {
        getInstance().setMoney(customerAccountOper.getOperMoney());

        if (!customerAccountOper.getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS) &&
                (getAccountOperList().size() != 1)) {
            if (getAccountOperList().size() != 1) {
                throw new IllegalArgumentException(customerAccountOper.getType() + "error accountOperList:" + getAccountOperList().size());
            }
        }


        for (AccountOper accountOper : getAccountOperList()) {
            accountOper.setOperType(customerAccountOper.getType());
            accountOper.setOperEmp(credentials.getUsername());
            accountOper.setOperDate(customerAccountOper.getOperDate());
            accountOper.setMoneySave(getInstance());
            accountOper.setAdvanceReceivable(BigDecimal.ZERO);
            accountOper.setAccountsReceivable(BigDecimal.ZERO);

            if (!customerAccountOper.getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)) {
                accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
                accountOper.setCustomer(customerHome.getReadyInstance());

            }
        }

        if (customerAccountOper.getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)) {
            getInstance().setTransCorp(transCorp);
        } else {
            getInstance().setTransCorp(null);
            getSingleAccountOper().setCustomer(customerHome.getReadyInstance());
            if (customerAccountOper.getType().equals(AccountOper.AccountOperType.DEPOSIT_BACK)) {
                if (RunParam.instance().getBooleanParamValue("erp.finance.useAdvance")) {
                    getSingleAccountOper().setAdvanceReceivable(getOperMoney());
                }else{
                    getSingleAccountOper().setAccountsReceivable(getOperMoney());
                }

            } else if (customerAccountOper.getType().equals(AccountOper.AccountOperType.CUSTOMER_SAVINGS)) {

                if (RunParam.instance().getBooleanParamValue("erp.finance.useAdvance")) {
                    if (customerHome.getInstance().getAccountMoney().compareTo(getOperMoney()) >= 0) {
                        getSingleAccountOper().setAdvanceReceivable(BigDecimal.ZERO);
                        getSingleAccountOper().setAccountsReceivable(getOperMoney());
                    } else if (customerHome.getInstance().getAccountMoney().compareTo(BigDecimal.ZERO) <= 0) {
                        getSingleAccountOper().setAdvanceReceivable(getOperMoney());
                        getSingleAccountOper().setAccountsReceivable(BigDecimal.ZERO);
                    } else {
                        getSingleAccountOper().setAdvanceReceivable(getOperMoney().subtract(customerHome.getInstance().getAccountMoney()));
                        getSingleAccountOper().setAccountsReceivable(customerHome.getInstance().getAccountMoney());
                    }
                } else {
                    getSingleAccountOper().setAccountsReceivable(getOperMoney());
                }
            } else {
                throw new IllegalArgumentException("unkonw type:" + customerAccountOper.getType());
            }
        }

        for (AccountOper accountOper : getAccountOperList()) {
            accountOper.calcCustomerMoney();
        }
        return true;
    }
}
