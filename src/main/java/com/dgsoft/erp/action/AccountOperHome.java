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

import java.util.EnumSet;

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

    @Factory(value ="moneyPayTypes", scope = ScopeType.CONVERSATION)
    public PayType[] getMoneyPayTypes(){
        return EnumSet.of(PayType.BANK_TRANSFER,PayType.CASH,PayType.CHECK).toArray(new PayType[0]);
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
    protected AccountOper createInstance(){
        return new AccountOper(PayType.BANK_TRANSFER,credentials.getUsername());

    }

    @Override
    protected boolean wire(){
        getInstance().setOperEmp(credentials.getUsername());
        return true;
    }

    public void payTypeChangeListener(){
        getInstance().setAccountingByDebitAccount(null);
    }


//
//    <a:outputPanel id="debitAccountSelectTreePanel">
//    <ui:include src="/layout/erp/FinanceAccountingSelect.xhtml">
//    <ui:param name="panelId" value="debitAccountSelectPanel"/>
//    <ui:param name="panelTitle" value="#{messages.debitAccountSelect}"/>
//    <ui:param name="rootAccounting"
//    value="#{accountOperHome.instance.payType eq 'BANK_TRANSFER' ? rootBankAccountingList.resultList : (accountOperHome.instance.payType eq 'FROM_PRE_DEPOSIT' ? rootAccountingList.resultList : rootCashAccountingList.resultList)}"/>
//    <ui:param name="render" value="debitAccountField"/>
//    <ui:param name="assignTo" value="#{accountOperHome.debitAccountId}"/>
//    </ui:include>
//    </a:outputPanel>
//
//    <ui:include src="/layout/erp/FinanceAccountingSelect.xhtml">
//    <ui:param name="panelId" value="creditAccountSelectPanel"/>
//    <ui:param name="panelTitle" value="#{messages.creditAccountSelect}"/>
//    <ui:param name="rootAccounting"
//    value="#{rootAccountingList.resultList}"/>
//    <ui:param name="render" value="creditAccountField"/>
//    <ui:param name="assignTo" value="#{accountOperHome.creditAccountId}"/>
//    </ui:include>


//    <s:decorate template="/layout/seam-edit.xhtml" id="debitAccountField">
//    <ui:define name="label">#{messages.field_debit_account}</ui:define>
//    <rich:autocomplete value="#{accountOperHome.debitAccountId}" required="true" showButton="true"
//
//    label="#{messages.field_debit_account}"
//    mode="client"
//    autocompleteList="#{accountOperHome.instance.payType eq 'BANK_TRANSFER' ? bankAccountingList.resultList : (accountOperHome.instance.payType eq 'FROM_PRE_DEPOSIT' ? allAccountingList.resultList : cashAccountingList.resultList)}"
//    var="_account" fetchValue="#{_account.id}" layout="table">
//    <rich:column>
//    <b>#{_account.id}</b>
//    </rich:column>
//    <rich:column>
//            #{_account.name}
//    </rich:column>
//    <a:ajax event="blur" render="debitAccountField"/>
//    </rich:autocomplete>
//
//
//    <ui:define name="tail">
//    <a:commandButton image="/img/onetomany.gif"
//    onclick="#{rich:component('debitAccountSelectPanel')}.show();return false;"/>
//    </ui:define>
//    </s:decorate>
//
//
//    <s:decorate id="creditAccountField" template="/layout/seam-edit.xhtml">
//    <ui:define name="label">#{messages.field_credit_account}</ui:define>
//    <rich:autocomplete value="#{accountOperHome.creditAccountId}" required="true" showButton="true"
//    mode="client" autocompleteList="#{allAccountingList.resultList}"
//    label="#{messages.field_credit_account}"
//    var="_account" fetchValue="#{_account.id}" layout="table">
//    <rich:column>
//    <b>#{_account.id}</b>
//    </rich:column>
//    <rich:column>
//            #{_account.name}
//    </rich:column>
//    <a:ajax event="blur" render="creditAccountField">
//    </a:ajax>
//    </rich:autocomplete>
//
//    <ui:define name="tail">
//    <a:commandButton image="/img/onetomany.gif"
//    onclick="#{rich:component('creditAccountSelectPanel')}.show();return false;"/>
//    </ui:define>
//    </s:decorate>



}
