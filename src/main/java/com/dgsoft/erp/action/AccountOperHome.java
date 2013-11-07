package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Accounting;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.security.Credentials;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:45 AM
 */
@Name("accountOperHome")
public class AccountOperHome extends ErpEntityHome<AccountOper> {

    @Factory(value = "accountPayTypes",scope = ScopeType.CONVERSATION)
    public PayType[] getAccountPayTypes() {
        return PayType.values();
    }

    @In
    protected Credentials credentials;

    public String getDebitAccountId(){
        if (getInstance().getAccountingByDebitAccount() == null){
            return null;
        }else{
            log.debug(getInstance().getAccountingByDebitAccount().getId());
            return getInstance().getAccountingByDebitAccount().getId();
        }
    }

    public void setDebitAccountId(String accountingId){
        log.debug("DebitAccountId:"+  accountingId+"|");
       if (accountingId == null || "".equals(accountingId.trim())){
           getInstance().setAccountingByDebitAccount(null);
       }else {
           log.debug(getEntityManager().find(Accounting.class,accountingId));
           getInstance().setAccountingByDebitAccount(getEntityManager().find(Accounting.class,accountingId));
       }
    }

    public String getCreditAccountId(){
        if (getInstance().getAccountingByCreditAccount() == null){
            return null;
        }else{
            return getInstance().getAccountingByCreditAccount().getId();
        }
    }

    public void setCreditAccountId(String accountingId){

        log.debug("setCreditAccountId:"+  accountingId+"|");
        if (accountingId == null || "".equals(accountingId.trim())){
            getInstance().setAccountingByCreditAccount(null);
        }else {
            getInstance().setAccountingByCreditAccount(getEntityManager().find(Accounting.class,accountingId));
        }
    }

    @Override
    protected boolean wire(){
        getInstance().setOperEmp(credentials.getUsername());
        return true;
    }

}
