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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
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
    private FacesMessages facesMessages;

    @In
    private Credentials credentials;

    private BigDecimal operMoney;

    private Date changeToDate;

    public Date getChangeToDate() {
        return changeToDate;
    }

    public void setChangeToDate(Date changeToDate) {
        this.changeToDate = changeToDate;
    }

    public BigDecimal getOperMoney() {
        return operMoney;
    }

    public void setOperMoney(BigDecimal operMoney) {
        this.operMoney = operMoney;
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


    public EnumSet<AccountOper.AccountOperType> getPrepareTypes() {
        if (customerHome.getInstance().getAccountMoney().compareTo(BigDecimal.ZERO) > 0){
            return EnumSet.of(AccountOper.AccountOperType.PRE_DEPOSIT, AccountOper.AccountOperType.ORDER_FREE);
        }else{
            return EnumSet.of(AccountOper.AccountOperType.PRE_DEPOSIT);
        }

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
                getInstance().getAccountOper().setCheckNumber(null);
                getInstance().getAccountOper().setUseCheck(false);
                getInstance().getAccountOper().setRemitFee(getOperMoney());
                getInstance().getAccountOper().setAccountsReceivable(getOperMoney());
                getInstance().getAccountOper().setProxcAccountsReceiveable(BigDecimal.ZERO);
                getInstance().getAccountOper().setAdvanceReceivable(BigDecimal.ZERO);
            } else {

                BigDecimal accountsMoney;
                BigDecimal advanceMoney;

                if (getOperMoney().compareTo(customerHome.getInstance().getAccountMoney()) > 0 ){
                    accountsMoney = customerHome.getInstance().getAccountMoney();
                    advanceMoney =  getOperMoney().subtract(accountsMoney);
                }else{
                    advanceMoney = BigDecimal.ZERO;
                    accountsMoney = getOperMoney();
                }

                getInstance().getAccountOper().setAdvanceReceivable(advanceMoney);
                getInstance().getAccountOper().setAccountsReceivable(accountsMoney);
                getInstance().getAccountOper().setProxcAccountsReceiveable(BigDecimal.ZERO);
            }
            getInstance().getAccountOper().calcCustomerMoney();
        }

        return true;
    }

    @Override
    protected boolean verifyPersistAvailable() {
        if (getInstance().getAccountOper().getOperType().equals(AccountOper.AccountOperType.ORDER_FREE)
                && (customerHome.getInstance().getAccountMoney().compareTo(getOperMoney()) < 0)) {
            facesMessages.add(StatusMessage.Severity.ERROR, "cantFreeMoney");
            return false;
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
