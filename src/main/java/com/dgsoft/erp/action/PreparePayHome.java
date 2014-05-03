package com.dgsoft.erp.action;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.PreparePay;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("preparePayHome")
public class PreparePayHome extends ErpEntityHome<PreparePay> {

    @In(create = true)
    private ActionExecuteState actionExecuteState;

    @In(required = false)
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    private Date changeToDate;

    public Date getChangeToDate() {
        return changeToDate;
    }

    public void setChangeToDate(Date changeToDate) {
        this.changeToDate = changeToDate;
    }


    @Transactional
    public String changeDate() {
        getInstance().getAccountOper().setOperDate(changeToDate);
        String result = update();
        if (result.equals("updated")) {
            actionExecuteState.actionExecute();
        }
        return result;
    }

    @Factory(value = "prepareTypes", scope = ScopeType.CONVERSATION)
    public EnumSet<AccountOper.AccountOperType> getPrepareTypes() {
        return EnumSet.of(AccountOper.AccountOperType.PRE_DEPOSIT, AccountOper.AccountOperType.ORDER_FREE);
    }

    @Override
    public PreparePay createInstance() {

        if (customerHome != null) {
            PreparePay result = new PreparePay();
            result.setAccountOper(new AccountOper(result, customerHome.getInstance(), credentials.getUsername()));
            return result;
        } else return super.createInstance();
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            if (getInstance().getAccountOper().getOperType().equals(AccountOper.AccountOperType.ORDER_FREE)) {
                getInstance().getAccountOper().setPayType(null);
                getInstance().getAccountOper().setRemitFee(getInstance().getAccountOper().getAccountsReceivable());
                getInstance().getAccountOper().setProxcAccountsReceiveable(BigDecimal.ZERO);
                getInstance().getAccountOper().setAdvanceReceivable(BigDecimal.ZERO);
            }else{
                getInstance().getAccountOper().setAccountsReceivable(BigDecimal.ZERO);
                getInstance().getAccountOper().setProxcAccountsReceiveable(BigDecimal.ZERO);
            }
        }

        return true;
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        //TODO check account
        getInstance().getAccountOper().revertCustomerMoney();
        return true;
    }


}
