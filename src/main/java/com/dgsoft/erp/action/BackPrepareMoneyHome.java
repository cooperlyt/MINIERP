package com.dgsoft.erp.action;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.BackPrepareMoney;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("backPrepareMoneyHome")
public class BackPrepareMoneyHome extends ErpEntityHome<BackPrepareMoney> {


    @In(create = true)
    private ActionExecuteState actionExecuteState;

    @In(required = false)
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    @Override
    public BackPrepareMoney createInstance() {
        if (customerHome != null) {
            BackPrepareMoney result = new BackPrepareMoney();
            result.setAccountOper(new AccountOper(result, customerHome.getInstance(), credentials.getUsername()));
            return result;
        } else return super.createInstance();
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            getInstance().getAccountOper().setAccountsReceivable(BigDecimal.ZERO);
            getInstance().getAccountOper().setProxcAccountsReceiveable(BigDecimal.ZERO);
            getInstance().getAccountOper().calcCustomerMoney();
            //getInstance().getAccountOper().getCustomer().setBalance(getInstance().getAccountOper().getCustomer().getBalance().subtract(getInstance().getAccountOper().getOperMoney()));
        }

        return true;
    }

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

    @Override
    protected boolean verifyRemoveAvailable() {
        //TODO check account
        getInstance().getAccountOper().revertCustomerMoney();

        return true;
    }


    public void backAllMoney() {
        getInstance().getAccountOper().setAdvanceReceivable(customerHome.getInstance().getAdvanceMoney());
    }
}
