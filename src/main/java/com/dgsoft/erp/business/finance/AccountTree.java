package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.common.utils.finance.AccountDetailsItem;
import com.dgsoft.common.utils.finance.Certificate;
import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.common.utils.finance.SampleLeafAccount;
import com.dgsoft.erp.model.*;
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
                for (BankAccount bankAccount : erpEntityManager.createQuery("select bankAccount from BankAccount bankAccount", BankAccount.class).getResultList()) {
                    ac.getChilds().add(new SampleLeafAccount(ac.getDirection(), dictionary.getWordValue(bankAccount.getBank()),
                            ac, ac.getLevel() + 1, RunParam.instance().getStringParamValue("erp.finance.bankAccount") + bankAccount.getId()));
                }
            }
        } else {
            for (Accounting accounting : ac.getAccountings()) {
                addSaleClild(accounting);
            }
        }

    }

    private List<String> getAllAccountCodes(Accounting accounting) {
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

    //TODO create itemTable
    private List<Certificate> getCertificate(){
       return new ArrayList<Certificate>(erpEntityManager.createQuery("select certificate from SaleCertificate certificate " +
                "where certificate.date >= :beginDate and certificate.date <= :endDate order by certificate.code",SaleCertificate.class).
                setParameter("beginDate",getSelectCheckOut().getBeginDate()).
                setParameter("endDate",getSelectCheckOut().getCloseDate()).getResultList());

    }


    public List<AccountDetailsItem> getAccountDetails() {
        if ((selectAccountCode == null) || "".equals(selectAccountCode.trim())) {
            return new ArrayList<AccountDetailsItem>(0);
        }
        Accounting accounting = erpEntityManager.find(Accounting.class, selectAccountCode);

        List<String> codes;
        if (accounting == null) {
            codes = new ArrayList<String>(1);
            codes.add(selectAccountCode);
        } else {
            codes = getAllAccountCodes(accounting);
        }

        List<AccountCheckout> accountCheckouts = erpEntityManager.createQuery("select accountCheckout from AccountCheckout accountCheckout " +
                "where accountCheckout.checkout.id = :checkoutId and accountCheckout.accountCode in (:codes)", AccountCheckout.class).
                setParameter("checkoutId", getSelectCheckOut().getId()).setParameter("codes", codes).getResultList();



        List<CertificateItem> items = new ArrayList<CertificateItem>();
        for (Certificate ci: getCertificate()){
            items.addAll(ci.getItemByCodes(codes));
        }


        return null;
    }

    private Checkout checkout;

    public Checkout getSelectCheckOut() {
        if (checkout == null) {
            checkout = erpEntityManager.createQuery("select checkout from Checkout checkout " +
                    "where year(checkout.closeDate) = :year and month(checkout.closeDate) = :month", Checkout.class).
                    setParameter("year", year).setParameter("month", month).getSingleResult();
        }
        return checkout;
    }

    private void clearDity(){
        checkout = null;
    }


    private String selectAccountCode;

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
        if ((year == null) || (this.year == null) || !year.equals(this.year)){
            clearDity();
            month = null;
        }
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        if ((month == null) || (this.month == null) ||  month.equals(this.month)){
            clearDity();
        }
        this.month = month;
    }

    public boolean isDetailsAccount() {
        return detailsAccount;
    }

    public void setDetailsAccount(boolean detailsAccount) {
        this.detailsAccount = detailsAccount;
    }

    public String getSelectAccountCode() {
        return selectAccountCode;
    }

    public void setSelectAccountCode(String selectAccountCode) {
        this.selectAccountCode = selectAccountCode;
    }
}
