package com.dgsoft.erp.business.finance;

import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.erp.model.AccountCheckout;
import com.dgsoft.erp.model.Checkout;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Credentials;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 5/17/14.
 */
@Name("closingAccount")
public class ClosingAccount {

    @In
    private AccountDateHelper accountDateHelper;

    @In
    private EntityManager erpEntityManager;


    @In
    private Credentials credentials;

    @In(create = true)
    private SaleAccountClose saleAccountClose;

    @In(create = true)
    private AccountTitleHelper accountTitleHelper;

    @Transactional
    public void unclosing(){
        Long maxId = erpEntityManager.createQuery("select max(checkout.id) from Checkout checkout",Long.class).getSingleResult();
        if (maxId == null){
            return;
        }
        erpEntityManager.remove(erpEntityManager.find(Checkout.class,maxId));
        erpEntityManager.flush();

        Events.instance().raiseTransactionSuccessEvent("erp.closingAccount");

    }

    @Transactional
    public void closing(){
        Long maxId = erpEntityManager.createQuery("select max(checkout.id) from Checkout checkout",Long.class).getSingleResult();
        if (!saleAccountClose.canClose()){
            return;
        }

        Checkout checkout = new Checkout((maxId == null) ? new Long(1) : maxId + 1 ,new Date(),credentials.getUsername(),accountDateHelper.getNextBeginDate(),accountDateHelper.getNextCloseDate());
        if (maxId != null){
            for(AccountCheckout aco : erpEntityManager.find(Checkout.class,maxId).getAccountCheckouts()){
                checkout.getAccountCheckouts().add(new AccountCheckout(aco.getAccountCode(),checkout,aco.getClosingBalance(),aco.getClosingCount()));
            }
        }

        saleAccountClose.doClose(checkout);



        for (AccountCheckout aco : checkout.getAccountCheckouts()){

            Account account = accountTitleHelper.getAccountByCode(aco.getAccountCode());
            if (account.getDirection().equals(Account.Direction.CREDIT)){
                aco.setClosingBalance(aco.getBeginningBalance().add(aco.getCreditMoney()).subtract(aco.getDebitMoney()));
                aco.setClosingCount(aco.getBeginningCount().add(aco.getCreditCount()).subtract(aco.getDebitCount()));
            }else{
                aco.setClosingBalance(aco.getBeginningBalance().add(aco.getDebitMoney()).subtract(aco.getCreditMoney()));
                aco.setClosingCount(aco.getBeginningCount().add(aco.getDebitCount()).subtract(aco.getCreditCount()));
            }

        }

        erpEntityManager.persist(checkout);
        erpEntityManager.flush();

        Events.instance().raiseTransactionSuccessEvent("erp.closingAccount");
    }


}
