package com.dgsoft.erp.business.finance;

import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.common.utils.finance.AccountDetailsItem;
import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-20
 * Time: 上午11:44
 */
@Name("detailsAccount")
public class DetailsAccount {



    @In
    private EntityManager erpEntityManager;

    @In(create = true)
    protected Map<String, String> messages;

    @In(create = true)
    private AccountTitleHelper accountTitleHelper;



//    private List<Certificate> getCertificate(){
//       return new ArrayList<Certificate>(erpEntityManager.createQuery("select certificate from SaleCertificate certificate " +
//                "where certificate.date >= :beginDate and certificate.date <= :endDate order by certificate.code",SaleCertificate.class).
//                setParameter("beginDate",getSelectCheckOut().getBeginDate()).
//                setParameter("endDate",getSelectCheckOut().getCloseDate()).getResultList());
//
//    }

    private List<CertificateItem> getCertifiCateItems(List<String> codes){
        return new ArrayList<CertificateItem>(erpEntityManager.createQuery("select item from SaleCertificateItem item left join fetch item.saleCertificate " +
                "where item.saleCertificate.date >= :beginDate and item.saleCertificate.date <= :endDate and item.accountCode in (:accountCodes) order by item.saleCertificate.code",SaleCertificateItem.class).
                setParameter("beginDate",getSelectCheckOut().getBeginDate()).
                setParameter("endDate",getSelectCheckOut().getCloseDate()).
                setParameter("accountCodes",codes).getResultList());
    }


    @Transactional
    public List<AccountDetailsItem> getAccountDetails() {
        Logging.getLog(getClass()).debug("year:" + year + "|month:" + month + "|code:" + selectAccountCode);
        if ((year == null) || (month == null) || (selectAccountCode == null)){
            return new ArrayList<AccountDetailsItem>(0);
        }

        if ((selectAccountCode == null) || "".equals(selectAccountCode.trim())) {
            return new ArrayList<AccountDetailsItem>(0);
        }
        Accounting accounting = erpEntityManager.find(Accounting.class, selectAccountCode);

        List<String> codes;
        if (accounting == null) {
            codes = new ArrayList<String>(1);
            codes.add(selectAccountCode);
        } else {
            codes = accountTitleHelper.getAllAccountCodes(accounting);
        }



        List<AccountCheckout> accountCheckouts = erpEntityManager.createQuery("select accountCheckout from AccountCheckout accountCheckout " +
                "where accountCheckout.checkout.id = :checkoutId and accountCheckout.accountCode in (:codes)", AccountCheckout.class).
                setParameter("checkoutId", getSelectCheckOut().getId()).setParameter("codes", codes).getResultList();


        List<AccountDetailsItem> result = new ArrayList<AccountDetailsItem>();
        Account selectAccount = accountTitleHelper.getAccountByCode(selectAccountCode);

        List<CertificateItem> items = getCertifiCateItems(codes);


        BigDecimal beginBalance = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal closeBalance = BigDecimal.ZERO;
        for (AccountCheckout aco: accountCheckouts){
            beginBalance = beginBalance.add(aco.getBeginningBalance());
            totalDebit = totalDebit.add(aco.getDebitMoney());
            totalCredit = totalCredit.add(aco.getCreditMoney());
            closeBalance = closeBalance.add(aco.getClosingBalance());
        }

        AccountDetailsItem beforItem = new AccountDetailsItem(messages.get("checkOutBeginBalance"),beginBalance,
                selectAccount.getDirection(),getSelectCheckOut().getBeginDate());
        result.add(beforItem);

        for (CertificateItem item: items){
            AccountDetailsItem newItem = new AccountDetailsItem(item,beforItem,selectAccount.getDirection());
            result.add(newItem);
            beforItem = newItem;
        }



        result.add(new AccountDetailsItem(messages.get("checkOutClosingTotal"),totalDebit,
                totalCredit,closeBalance,selectAccount.getDirection(),getSelectCheckOut().getCloseDate()));
        //

        return result;
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

    @Observer(value = {"erp.closingAccount","erp.closingAccount"})
    public void clearDate(){
        year = null;
        month = null;
    }

    private String selectAccountCode;

    private boolean detailsAccount = false;

    private Integer year;

    private Integer month;

    public List<Integer> getAllowYears() {
        return erpEntityManager.createQuery("select year(checkout.closeDate) from Checkout  checkout group by year(checkout.closeDate)", Integer.class).getResultList();
    }

    public List<Integer> getAllowMonths() {
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
