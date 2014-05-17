package com.dgsoft.erp.business.finance;

import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.erp.model.AccountCheckout;
import com.dgsoft.erp.model.Checkout;
import com.dgsoft.erp.model.SaleCertificate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 5/17/14.
 */
@Name("saleAccountClose")
public class SaleAccountClose {

    @In
    private EntityManager erpEntityManager;

    @In
    private AccountDateHelper accountDateHelper;

    @In
    private FacesMessages facesMessages;


    public boolean canClose() {
        boolean result = erpEntityManager.createQuery("select COUNT(accountOper.id) from AccountOper  accountOper " +
                        " where accountOper.operDate >= :beginDate and accountOper.operDate <= :endDate and accountOper.saleCertificate is null order by accountOper.operDate",
                Long.class
        ).setParameter("beginDate", accountDateHelper.getNextBeginDate()).setParameter("endDate", accountDateHelper.getNextCloseDate()).getSingleResult() == 0;

        if (!result)
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "NotMakeAccountError");
        return result;
    }


    public void doClose(Checkout checkout) {
        List<SaleCertificate> certificates = erpEntityManager.createQuery("select saleCertificate from SaleCertificate saleCertificate " +
                "where date >= :beginDate order by saleCertificate.code", SaleCertificate.class).setParameter("beginDate",accountDateHelper.getNextBeginDate()).getResultList();

        Map<String,AccountCheckout> checkoutMap = checkout.getAccountCheckOutMap();
        for(SaleCertificate certificate: certificates){
             for (CertificateItem item: certificate.getCertificateItems()){
                 AccountCheckout accountCheckout = checkoutMap.get(item.getAccountCode());
                 if (accountCheckout == null){
                     accountCheckout = new AccountCheckout(item.getAccountCode(),checkout, BigDecimal.ZERO, BigDecimal.ZERO);
                     checkoutMap.put(accountCheckout.getAccountCode(),accountCheckout);
                     checkout.getAccountCheckouts().add(accountCheckout);
                 }
                 accountCheckout.setDebitMoney(accountCheckout.getDebitMoney().add(item.getDebit()));
                 accountCheckout.setDebitCount(BigDecimal.ZERO);
                 accountCheckout.setCreditMoney(accountCheckout.getCreditMoney().add(item.getCredit()));
                 accountCheckout.setCreditCount(BigDecimal.ZERO);
             }
        }


    }
}
