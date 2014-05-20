package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.common.utils.finance.SampleLeafAccount;
import com.dgsoft.erp.model.Accounting;
import com.dgsoft.erp.model.BankAccount;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-15
 * Time: 下午2:58
 */

@Name("accountTitleHelper")
public class AccountTitleHelper {

    @In
    private EntityManager erpEntityManager;

    public Account getAccountByCode(String code){
        if ((code == null) || code.trim().equals("")) {
            return null;
        }
        Accounting accounting = erpEntityManager.find(Accounting.class, code);

        if (accounting != null) {
            return accounting;
        }

        String customerId;
        if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.customerAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.customerAccount").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    erpEntityManager.find(Customer.class,customerId).getName(),accounting,accounting.getLevel() + 1,code);
        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.proxyAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.proxyAccount").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    erpEntityManager.find(Customer.class,customerId).getName(),accounting,accounting.getLevel() + 1,code);

        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.advance"))){
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.advance"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.advance").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    erpEntityManager.find(Customer.class,customerId).getName(),accounting,accounting.getLevel() + 1,code);
        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))){
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.bankAccount"));
            String bankId =code.substring(RunParam.instance().getStringParamValue("erp.finance.bankAccount").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    DictionaryWord.instance().getWordValue(erpEntityManager.find(BankAccount.class,bankId).getBank()),
                    accounting,accounting.getLevel() + 1,code);


        } else {
            throw new IllegalArgumentException("unkonw account code:" + code);
        }

    }

    public String getAccountTitleByCode(String code) {
        if ((code == null) || code.trim().equals("")) {
            return null;
        }
        Accounting accounting = erpEntityManager.find(Accounting.class, code);

        if (accounting != null) {
            return accounting.getPathName();
        }

        String customerId;
        if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.customerAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.customerAccount").length());
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class,customerId).getName();
        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.proxyAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.proxyAccount").length());
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class, customerId).getName();

        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.advance"))){
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.advance"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.advance").length());
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class, customerId).getName();
        }else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))){
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.bankAccount"));
            String bankId =code.substring(RunParam.instance().getStringParamValue("erp.finance.bankAccount").length());
            return accounting.getPathName() + " > " +
                    DictionaryWord.instance().getWordValue(erpEntityManager.find(BankAccount.class,bankId).getBank());

        } else {
            throw new IllegalArgumentException("unkonw account code:" + code);
        }


    }
}
