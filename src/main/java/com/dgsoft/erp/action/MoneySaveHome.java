package com.dgsoft.erp.action;

import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.MoneySave;
import com.dgsoft.erp.model.TransCorp;
import org.jboss.seam.annotations.*;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-9
 * Time: 下午4:51
 */
@Name("moneySaveHome")
public class MoneySaveHome extends MoneySaveBaseHome {


    @Override
    public Class<MoneySave> getEntityClass() {
        return MoneySave.class;
    }

    private TransCorp transCorp;

    public TransCorp getTransCorp() {
        return transCorp;
    }

    public void setTransCorp(TransCorp transCorp) {
        this.transCorp = transCorp;
    }

    @In(required = false)
    private CustomerHome customerHome;


    public void calcRemitFee() {
        if (getInstance().getMoney() != null) {
            switch (getEditingOper().getOperType()) {

                case PROXY_SAVINGS:
                case CUSTOMER_SAVINGS:
                    getInstance().setRemitFee(getCustomerSaveReceiveMoney().add(getCustomerProxyReceiveMoney()).subtract(getInstance().getMoney()));
                    break;
                case DEPOSIT_BACK:
                    getInstance().setRemitFee(getInstance().getMoney().subtract(getCustomerSaveReceiveMoney().add(getCustomerProxyReceiveMoney())));
                    break;
                default:
                    getInstance().setRemitFee(BigDecimal.ZERO);

            }
        }
        vaildProxyAccount();
    }

    public void addProxyItem() {
        if (customerHome.getInstance().getProxyAccountMoney().compareTo(BigDecimal.ZERO) <= 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "CustomerNotHaveProxyAccountMoney");
            return;
        }

        for (AccountOper oper : getAccountOpers()) {
            if (oper.getCustomer().getId().equals(customerHome.getInstance().getId())) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "CustomerExistsInOperList");
                return;
            }
        }
        getAccountOpers().add(new AccountOper(getInstance(), AccountOper.AccountOperType.PROXY_SAVINGS, customerHome.getInstance(), credentials.getUsername()));
    }

    private String removeProxyCustomerId;

    public String getRemoveProxyCustomerId() {
        return removeProxyCustomerId;
    }

    public void setRemoveProxyCustomerId(String removeProxyCustomerId) {
        this.removeProxyCustomerId = removeProxyCustomerId;
    }

    public void removeProxyItem() {
        for (AccountOper oper : getAccountOpers()) {
            if (oper.getCustomer().getId().equals(removeProxyCustomerId)) {
                getAccountOpers().remove(oper);
                return;
            }
        }
    }

    public String validCustomerOrder() {
        getEditingOper().setCustomer(customerHome.getInstance());

        if (getEntityManager().createQuery("select count(customerOrder.id) from CustomerOrder customerOrder Where customerOrder.canceled = false and customerOrder.customer.id =:customerId and customerOrder.accountChange = false", Long.class).
                setParameter("customerId", customerHome.getInstance().getId()).getSingleResult() > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "CustomerHaveWaitMoneyOrder");
        }
        return "next";
    }


    @Begin(pageflow = "CustomerMoneyOper", flushMode = FlushModeType.MANUAL)
    public void beginCustomerSave() {

        getAccountOpers().add(new AccountOper(getInstance(), AccountOper.AccountOperType.CUSTOMER_SAVINGS, credentials.getUsername()));
    }

    @Begin(pageflow = "CustomerMoneyOper", flushMode = FlushModeType.MANUAL)
    public void beginFreeMoneyOper() {
        getAccountOpers().add(new AccountOper(AccountOper.AccountOperType.MONEY_FREE, credentials.getUsername()));
    }

    @Begin(pageflow = "CustomerMoneyOper", flushMode = FlushModeType.MANUAL)
    public void beginProxyOper() {

    }


    @Begin(pageflow = "CustomerMoneyOper", flushMode = FlushModeType.MANUAL)
    public void beginCustomerMoneyBack() {
        getAccountOpers().add(new AccountOper(getInstance(), AccountOper.AccountOperType.DEPOSIT_BACK, credentials.getUsername()));
    }

    private boolean vaildProxyAccount() {
        for (AccountOper oper : getAccountOpers()) {
            if (oper.getOperType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)) {
                if (oper.getProxcAccountsReceiveable().compareTo(oper.getCustomer().getProxyAccountMoney()) > 0) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "CustomerReceiveProxyMoneyGt",
                            oper.getCustomer().getName(), oper.getProxcAccountsReceiveable(), oper.getCustomer().getProxyAccountMoney());
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    public String toConfirm() {
        calcRemitFee();
        if (getInstance().getRemitFee().compareTo(BigDecimal.ZERO) < 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "ReceiveMoneyltAccountMoney");
            return null;
        }
        if (getAccountOpers().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "ProxyMestHaveCustomerError");
            return null;
        }

        if (!vaildProxyAccount()) {
            return null;
        }


        return "next";
    }


    @Transactional
    public String receiveMoney() {
        joinTransaction();
        calcCustomerOrderPayTag();
        if (getEditingOper().getOperType().equals(AccountOper.AccountOperType.MONEY_FREE)) {
            getEntityManager().persist(getEditingOper());
            getEntityManager().flush();
            return "persisted";
        } else {
            if (getEditingOper().getOperType().equals(AccountOper.AccountOperType.PROXY_SAVINGS))
                getInstance().setTransCorp(transCorp);
            else
                getInstance().setTransCorp(null);
            return persist();
        }

    }

}
