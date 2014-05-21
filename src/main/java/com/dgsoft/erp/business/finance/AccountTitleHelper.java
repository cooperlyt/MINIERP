package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.common.utils.finance.SampleLeafAccount;
import com.dgsoft.erp.model.Accounting;
import com.dgsoft.erp.model.BankAccount;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.richfaces.model.SwingTreeNodeImpl;

import javax.persistence.EntityManager;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;

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

    @In
    private DictionaryWord dictionary;

    public Account getAccountByCode(String code) {
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
                    erpEntityManager.find(Customer.class, customerId).getName(), accounting, accounting.getLevel() + 1, code);
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.proxyAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.proxyAccount").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    erpEntityManager.find(Customer.class, customerId).getName(), accounting, accounting.getLevel() + 1, code);

        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.advance"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.advance").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    erpEntityManager.find(Customer.class, customerId).getName(), accounting, accounting.getLevel() + 1, code);
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.bankAccount"));
            String bankId = code.substring(RunParam.instance().getStringParamValue("erp.finance.bankAccount").length());
            return new SampleLeafAccount(accounting.getDirection(),
                    DictionaryWord.instance().getWordValue(erpEntityManager.find(BankAccount.class, bankId).getBank()),
                    accounting, accounting.getLevel() + 1, code);


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
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class, customerId).getName();
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.proxyAccount"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.proxyAccount").length());
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class, customerId).getName();

        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.advance"));
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.advance").length());
            return accounting.getPathName() + " > " + erpEntityManager.find(Customer.class, customerId).getName();
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))) {
            accounting = erpEntityManager.find(Accounting.class, RunParam.instance().getStringParamValue("erp.finance.bankAccount"));
            String bankId = code.substring(RunParam.instance().getStringParamValue("erp.finance.bankAccount").length());
            return accounting.getPathName() + " > " +
                    DictionaryWord.instance().getWordValue(erpEntityManager.find(BankAccount.class, bankId).getBank());

        } else {
            throw new IllegalArgumentException("unkonw account code:" + code);
        }

    }


    @Factory(value = "accountTree",scope = ScopeType.CONVERSATION)
    public List<TreeNode> getAccountTree() {
        List<TreeNode> result = new ArrayList<TreeNode>();

        for (Word word : dictionary.getWordList("finance.accountType")) {
            SwingTreeNodeImpl rootNode = new SwingTreeNodeImpl<Word>();
            rootNode.setData(word);

            for (Accounting ac : erpEntityManager.createQuery("SELECT accounting from Accounting accounting " +
                    "where accounting.level = 1 and accounting.accountingType =:type", Accounting.class).
                    setParameter("type", word.getId()).getResultList()) {

                addSaleClild(ac);
                rootNode.addChild(ac);
            }
            result.add(rootNode);
        }


        return result;
    }

    private void addSaleClild(Accounting ac) {
        if (ac.getAccountings().isEmpty()) {
            List<TreeNode> treeNodes = new ArrayList<TreeNode>();
            if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    treeNodes.add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    treeNodes.add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    treeNodes.add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))) {
                for (BankAccount bankAccount : erpEntityManager.createQuery("select bankAccount from BankAccount bankAccount", BankAccount.class).getResultList()) {
                    treeNodes.add(new SampleLeafAccount(ac.getDirection(), dictionary.getWordValue(bankAccount.getBank()),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.bankAccount") + bankAccount.getId()));
                }
            }
            ac.setChilds(treeNodes);
        } else {
            for (Accounting accounting : ac.getAccountings()) {
                addSaleClild(accounting);
            }
        }

    }

    public List<String> getAllAccountCodes(Accounting accounting) {
        List<String> result = new ArrayList<String>();
        if (accounting.getAccountings().isEmpty()) {

            if (accounting.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    result.add(RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId());
                }
            } else if (accounting.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    result.add(RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId());
                }
            } else if (accounting.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    result.add(RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId());
                }
            } else if (accounting.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))) {
                for (BankAccount bankAccount : erpEntityManager.createQuery("select bankAccount from BankAccount bankAccount", BankAccount.class).getResultList()) {
                    result.add(RunParam.instance().getStringParamValue("erp.finance.bankAccount") + bankAccount.getId());
                }
            } else {
                result.add(accounting.getId());
            }


        } else {
            for (Accounting ac : accounting.getAccountings()) {
                result.addAll(getAllAccountCodes(ac));
            }
        }
        return result;
    }
}
