package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.common.utils.finance.SampleLeafAccount;
import com.dgsoft.erp.model.Accounting;
import com.dgsoft.erp.model.BankAccount;
import com.dgsoft.erp.model.Customer;
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
 * Date: 14-5-20
 * Time: 上午11:44
 */
@Name("account")
public class AccountTree {

    @In
    private DictionaryWord dictionary;

    @In
    private EntityManager erpEntityManager;

    public List<TreeNode> getAccountTree() {
        List<TreeNode> result = new ArrayList<TreeNode>();

        for (Word word : dictionary.getWordList("finance.accountType")) {
            SwingTreeNodeImpl rootNode = new SwingTreeNodeImpl<Word>();
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
        if (ac.isLeaf()) {
            if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    ac.getChilds().add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    ac.getChilds().add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
                for (Customer customer : erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList()) {
                    ac.getChilds().add(new SampleLeafAccount(ac.getDirection(), customer.getName(),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId()));
                }
            } else if (ac.getAccountCode().equals(RunParam.instance().getStringParamValue("erp.finance.bankAccount"))) {
                for (BankAccount bankAccount : erpEntityManager.createQuery("select bankAccount from BankAccount bankAccount",BankAccount.class).getResultList()) {
                    ac.getChilds().add(new SampleLeafAccount(ac.getDirection(),dictionary.getWordValue(bankAccount.getBank()) ,
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.bankAccount") + bankAccount.getId()));
                }
            }
        } else {
            for (Accounting accounting : ac.getAccountings()) {
                addSaleClild(accounting);
            }
        }

    }




    private boolean detailsAccount = false;

    private Integer year;

    private Integer month;

    private List<Integer> getAllowYears() {
        return erpEntityManager.createQuery("select year(checkout.closeDate) from Checkout  checkout group by year(checkout.closeDate)", Integer.class).getResultList();
    }

    private List<Integer> getAllowMonths() {
        return erpEntityManager.createQuery("select month(checkout.closeDate) from Checkout checkout " +
                "where year(checkout.closeDate) = :year group by month(checkout.closeDate)", Integer.class).setParameter("year", year).getResultList();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public boolean isDetailsAccount() {
        return detailsAccount;
    }

    public void setDetailsAccount(boolean detailsAccount) {
        this.detailsAccount = detailsAccount;
    }
}
