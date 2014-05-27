package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.utils.finance.CertificateItem;
import com.dgsoft.common.utils.finance.SampleLeafAccount;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;
import org.jfree.util.Log;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

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

    private BigDecimal calcCustomerAdvanceMoney(String customerId, Date beginDate) {
        BigDecimal result = erpEntityManager.createQuery("select COALESCE(sum(accountOper.advanceReceivable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType in (:types)  and accountOper.advanceReceivable > 0 ", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate)
                .setParameter("types", new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.CUSTOMER_SAVINGS,
                        AccountOper.AccountOperType.ORDER_BACK))).getSingleResult();

        result = result.subtract(erpEntityManager.createQuery("select COALESCE(sum(accountOper.advanceReceivable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType in (:types) and accountOper.advanceReceivable > 0 ", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate)
                .setParameter("types", new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.DEPOSIT_BACK,
                        AccountOper.AccountOperType.DEPOSIT_PAY, AccountOper.AccountOperType.ORDER_PAY))).getSingleResult());


        return result;
    }


    private BigDecimal calcCustomerAccountMoney(String customerId, Date beginDate) {
        BigDecimal result = erpEntityManager.createQuery("select COALESCE(sum(accountOper.accountsReceivable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType = 'ORDER_PAY' and accountOper.accountsReceivable > 0 ", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate).getSingleResult();

        Logging.getLog(getClass()).debug(beginDate + "AA" + result);

        result = result.subtract(erpEntityManager.createQuery("select COALESCE(sum(accountOper.accountsReceivable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType in (:types) and accountOper.accountsReceivable > 0 ", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate)
                .setParameter("types", new ArrayList<AccountOper.AccountOperType>(EnumSet.of(AccountOper.AccountOperType.CUSTOMER_SAVINGS,
                        AccountOper.AccountOperType.DEPOSIT_PAY, AccountOper.AccountOperType.MONEY_FREE))).getSingleResult());

        Logging.getLog(getClass()).debug("BB" + result);
        return result;
    }

    private BigDecimal calcCustomerProxyAccountMoney(String customerId, Date beginDate) {
        BigDecimal result = erpEntityManager.createQuery("select COALESCE(sum(accountOper.proxcAccountsReceiveable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType = 'ORDER_PAY' and accountOper.proxcAccountsReceiveable > 0", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate).getSingleResult();

        result = result.subtract(erpEntityManager.createQuery("select COALESCE(sum(accountOper.proxcAccountsReceiveable),0) from AccountOper accountOper " +
                "where accountOper.customer.id = :customerId and accountOper.operDate >= :beginDate " +
                "and operType = 'PROXY_SAVINGS' and accountOper.proxcAccountsReceiveable > 0 ", BigDecimal.class).
                setParameter("customerId", customerId).setParameter("beginDate", beginDate).getSingleResult());


        return result;
    }

    private BigDecimal calcFirstBeginMoneyByCode(String code) {
        String customerId;
        BigDecimal calcMoney;
        BigDecimal customerMoney;
        if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.customerAccount"))) {
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.customerAccount").length());
            calcMoney = calcCustomerAccountMoney(customerId, accountDateHelper.getNextBeginDate());
            customerMoney = erpEntityManager.find(Customer.class, customerId).getAccountMoney();
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.proxyAccount"))) {
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.proxyAccount").length());
            calcMoney = calcCustomerProxyAccountMoney(customerId, accountDateHelper.getNextBeginDate());
            customerMoney = erpEntityManager.find(Customer.class, customerId).getProxyAccountMoney();
        } else if (code.startsWith(RunParam.instance().getStringParamValue("erp.finance.advance"))) {
            customerId = code.substring(RunParam.instance().getStringParamValue("erp.finance.advance").length());
            calcMoney = calcCustomerAdvanceMoney(customerId, accountDateHelper.getNextBeginDate());
            customerMoney = erpEntityManager.find(Customer.class, customerId).getAdvanceMoney();
        } else
           return BigDecimal.ZERO;

        return customerMoney.subtract(calcMoney);
    }

    public boolean canClose(Checkout checkout) {
        boolean result = erpEntityManager.createQuery("select COUNT(accountOper.id) from AccountOper  accountOper " +
                        " where accountOper.operDate >= :beginDate and accountOper.operDate <= :endDate and accountOper.saleCertificate is null order by accountOper.operDate",
                Long.class
        ).setParameter("beginDate", checkout.getBeginDate()).setParameter("endDate", checkout.getCloseDate()).getSingleResult() == 0;

        if (!result) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "NotMakeAccountError");
        }else if (!accountDateHelper.isFirst()){
            Map<String, AccountCheckout> checkoutMap = checkout.getAccountCheckOutMap();

            List<Customer> customers = erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList();
            for (Customer customer: customers){

                AccountCheckout cc = checkoutMap.get(RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId());
                BigDecimal money = BigDecimal.ZERO;
                if ( cc != null){
                    money = money.add(cc.getBeginningBalance());
                }
                Logging.getLog(getClass()).debug(checkout.getBeginDate());
                money = money.add(calcCustomerAccountMoney(customer.getId(),checkout.getBeginDate()));
                if (money.compareTo(customer.getAccountMoney()) != 0){
                    throw new IllegalArgumentException("customer Account balance error:" + customer.getId() + "m:" +  money + "cm:" + customer.getAccountMoney());
                }

                //--------------------------
                cc = checkoutMap.get(RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId());
                money = BigDecimal.ZERO;
                if ( cc != null){
                    money = money.add(cc.getBeginningBalance());
                }
                money = money.add(calcCustomerProxyAccountMoney(customer.getId(),checkout.getBeginDate()));
                if (money.compareTo(customer.getProxyAccountMoney()) != 0){
                    throw new IllegalArgumentException("customer ProxyAccount balance error:" + customer.getId());
                }

                //--------------------------
                cc = checkoutMap.get(RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId());
                money = BigDecimal.ZERO;
                if ( cc != null){
                    money = money.add(cc.getBeginningBalance());
                }
                money = money.add(calcCustomerAdvanceMoney(customer.getId(),checkout.getBeginDate()));
                if (money.compareTo(customer.getAdvanceMoney()) != 0){
                    throw new IllegalArgumentException("customer advance balance error:" + customer.getId());
                }

            }
        }
        return result;
    }

    public void doClose(Checkout checkout) {
        List<SaleCertificate> certificates = erpEntityManager.createQuery("select saleCertificate from SaleCertificate saleCertificate " +
                "where date >= :beginDate order by saleCertificate.code", SaleCertificate.class).setParameter("beginDate", accountDateHelper.getNextBeginDate()).getResultList();

        Map<String, AccountCheckout> checkoutMap = checkout.getAccountCheckOutMap();
        for (SaleCertificate certificate : certificates) {
            for (CertificateItem item : certificate.getCertificateItems()) {
                AccountCheckout accountCheckout = checkoutMap.get(item.getAccountCode());
                if (accountCheckout == null) {
                    if (accountDateHelper.isFirst()) {
                        accountCheckout = new AccountCheckout(item.getAccountCode(), checkout,
                                calcFirstBeginMoneyByCode(item.getAccountCode()), BigDecimal.ZERO);

                    } else {
                        accountCheckout = new AccountCheckout(item.getAccountCode(), checkout, BigDecimal.ZERO, BigDecimal.ZERO);

                    }


                    checkoutMap.put(accountCheckout.getAccountCode(), accountCheckout);
                    checkout.getAccountCheckouts().add(accountCheckout);
                }
                accountCheckout.setDebitMoney(accountCheckout.getDebitMoney().add(item.getDebit()));
                accountCheckout.setDebitCount(BigDecimal.ZERO);
                accountCheckout.setCreditMoney(accountCheckout.getCreditMoney().add(item.getCredit()));
                accountCheckout.setCreditCount(BigDecimal.ZERO);
            }
        }

        if (accountDateHelper.isFirst()){
            List<Customer> customers = erpEntityManager.createQuery("select customer from Customer customer ", Customer.class).getResultList();
            for (Customer customer: customers){
                if ((customer.getProxyAccountMoney().compareTo(BigDecimal.ZERO) != 0) ||
                        (customer.getAccountMoney().compareTo(BigDecimal.ZERO) != 0) ||
                        (customer.getAdvanceMoney().compareTo(BigDecimal.ZERO) != 0) || !customer.getAccountOpers().isEmpty()){



                    initNoOperCustomerAccount(checkout,checkoutMap,
                            RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId(),
                            calcFirstBeginMoneyByCode(RunParam.instance().getStringParamValue("erp.finance.customerAccount") + customer.getId()));
                    initNoOperCustomerAccount(checkout,checkoutMap,
                            RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId(),
                            calcFirstBeginMoneyByCode(RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + customer.getId()));
                    initNoOperCustomerAccount(checkout,checkoutMap,
                            RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId(),
                            calcFirstBeginMoneyByCode(RunParam.instance().getStringParamValue("erp.finance.advance") + customer.getId()));


                }
            }
        }


    }

    private void initNoOperCustomerAccount(Checkout checkout,Map<String, AccountCheckout> checkoutMap,String code, BigDecimal money){
        if (checkoutMap.get(code) == null){
            AccountCheckout accountCheckout = new AccountCheckout(code, checkout, money, BigDecimal.ZERO);
            checkoutMap.put(accountCheckout.getAccountCode(), accountCheckout);
            checkout.getAccountCheckouts().add(accountCheckout);
        }

    }

}
