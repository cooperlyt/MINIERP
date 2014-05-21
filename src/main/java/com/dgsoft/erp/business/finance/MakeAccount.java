package com.dgsoft.erp.business.finance;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.SaleCertificate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by cooper on 5/14/14.
 */
@Name("makeAccount")
public class MakeAccount implements Serializable {

    @In
    private EntityManager erpEntityManager;

    @In
    private AccountDateHelper accountDateHelper;

    @In
    private Credentials credentials;

    @In
    private FacesMessages facesMessages;


    private int getMakeBeginCode() {
        Integer result = erpEntityManager.createQuery("select max(saleCertificate.code) from SaleCertificate saleCertificate", Integer.class).getSingleResult();
        if (result == null) {
            return 1;
        } else {
            return result + 1;
        }
    }

    @Transactional
    public void deleteAll() {
        erpEntityManager.createQuery("update AccountOper accountOper set accountOper.saleCertificate = null " +
                "where accountOper.saleCertificate.id in (select saleCertificate.id  from SaleCertificate saleCertificate where saleCertificate.date >= :beginDate)")
                .setParameter("beginDate", accountDateHelper.getNextBeginDate()).executeUpdate();

        erpEntityManager.createQuery("update MoneySave moneySave set moneySave.saleCertificate = null " +
                "where moneySave.saleCertificate.id in (select saleCertificate.id  from SaleCertificate saleCertificate where saleCertificate.date >= :beginDate)")
                .setParameter("beginDate", accountDateHelper.getNextBeginDate()).executeUpdate();

        erpEntityManager.createQuery("delete from SaleCertificate saleCertificate where saleCertificate.date >= :beginDate")
                .setParameter("beginDate", accountDateHelper.getNextBeginDate()).executeUpdate();
    }

    @Transactional
    public void make() {

        int code = getMakeBeginCode();
        int count = 0;
        List<AccountOper> accountOpers = erpEntityManager.createQuery("select accountOper from AccountOper  accountOper left join fetch accountOper.moneySave moneySave " +
                        "left join fetch accountOper.customer left join fetch moneySave.bankAccount left join moneySave.transCorp " +
                        " where accountOper.operDate >= :beginDate and accountOper.operDate <= :endDate and accountOper.saleCertificate is null order by accountOper.operDate",
                AccountOper.class
        ).setParameter("beginDate", accountDateHelper.getNextBeginDate()).setParameter("endDate", accountDateHelper.getNextCloseDate()).getResultList();

        for (AccountOper accountOper : accountOpers) {
            if ((accountOper.getMoneySave() != null) && (accountOper.getMoneySave().getSaleCertificate() != null)) {
                accountOper.setSaleCertificate(accountOper.getMoneySave().getSaleCertificate());
                accountOper.getMoneySave().getSaleCertificate().getAccountOpers().add(accountOper);
            } else {
                accountOper.setSaleCertificate(new SaleCertificate(RunParam.instance().getStringParamValue("erp.finance.c.wrod"),
                        code, accountOper.getOperDate(), credentials.getUsername()));

                accountOper.getSaleCertificate().setMemo(accountOper.getDescription());
                accountOper.getSaleCertificate().setCashier(accountOper.getOperEmp());
                accountOper.getSaleCertificate().getAccountOpers().add(accountOper);
                if (accountOper.getMoneySave() != null) {
                    accountOper.getMoneySave().setSaleCertificate(accountOper.getSaleCertificate());
                    accountOper.getSaleCertificate().getMoneySaves().add(accountOper.getMoneySave());
                }
                accountOper.getSaleCertificate().writeItem();

                code++;
                count++;
            }
        }


        // valid

        for (AccountOper accountOper : accountOpers) {
            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;
            for (CertificateItem item : accountOper.getSaleCertificate().getCertificateItems()) {
                debit = debit.add(item.getDebit());
                credit = credit.add(item.getCredit());
            }

            if (debit.compareTo(credit) != 0) {
                throw new IllegalArgumentException("certificate error not pin:" + accountOper.getId() + "debit:" + debit + "credit:" + credit);
            }
            accountOper.getSaleCertificate().setMoney(debit);
        }

        erpEntityManager.flush();

        facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "AccountMakeSuccess", count);


    }

}
