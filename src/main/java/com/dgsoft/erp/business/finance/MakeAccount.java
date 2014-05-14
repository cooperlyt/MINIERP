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

    private Date makeToDate;

    public Date getMakeToDate() {
        return makeToDate;
    }

    public void setMakeToDate(Date makeToDate) {
        this.makeToDate = makeToDate;
    }

    private Date getSearchToDate() {
        return new Date(DataFormat.halfTime(makeToDate).getTime() + 24 * 60 * 60 * 1000 - 1);
    }

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

    }

    @Transactional
    public void make() {

        int code = getMakeBeginCode();
        int count = 0;
        List<AccountOper> accountOpers = erpEntityManager.createQuery("select accountOper from AccountOper  accountOper " +
                        "where accountOper.operDate >= :beginDate and accountOper.operDate <= :endDate and accountOper.saleCertificate is null order by accountOper.operDate",
                AccountOper.class
        ).setParameter("beginDate", accountDateHelper.getNextBeginDate()).setParameter("endDate", getSearchToDate()).getResultList();

        for (AccountOper accountOper : accountOpers) {
            if ((accountOper.getMoneySave() != null) && (accountOper.getMoneySave().getSaleCertificate() != null)) {
                accountOper.setSaleCertificate(accountOper.getMoneySave().getSaleCertificate());
                accountOper.getMoneySave().getSaleCertificate().getAccountOpers().add(accountOper);
            } else {
                accountOper.setSaleCertificate(new SaleCertificate(RunParam.instance().getStringParamValue("erp.finance.c.wrod"),
                        code, accountOper.getOperDate(), credentials.getUsername()));
                accountOper.getSaleCertificate().getAccountOpers().add(accountOper);
                if (accountOper.getMoneySave() != null) {
                    accountOper.getMoneySave().setSaleCertificate(accountOper.getSaleCertificate());
                    accountOper.getSaleCertificate().getMoneySaves().add(accountOper.getMoneySave());
                }
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
                throw new IllegalArgumentException("certificate error not pin:" + accountOper.getId());
            }
        }

        erpEntityManager.flush();

        facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "AccountMakeSuccess", count);


    }

}
