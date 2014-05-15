package com.dgsoft.erp.business;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-13
 * Time: 上午11:07
 */
@Name("customerCheckoutAction")
public class CustomerCheckoutAction {
//
//    @In
//    private EntityManager erpEntityManager;
//
//    //日期 全部为 00:00  beginDate 为本欠开始日期    closeDate 为下次开始日期
//    public boolean checkOut(Date beginDate, Date closeDate, Checkout checkout) {
//
//        Map<String, AccountCheckout> accountCheckoutMap = checkout.getAccountCheckOutMap();
//
//
//        List<AccountOper> opers = erpEntityManager.createQuery("select accountOper from AccountOper accountOper left join accountOper.customer " +
//                "where accountOper.operDate >= :beginDate and accountOper.operDate < :endDate", AccountOper.class).
//                setParameter("beginDate", beginDate).setParameter("endDate", closeDate).getResultList();
//        for (AccountOper oper : opers) {
//            AccountCheckout accountCheckout;
//            if (oper.getOperType().getAdDirection() != null) {
//                accountCheckout = accountCheckoutMap.get(getCustomerADCode(oper.getCustomer()));
//                if (accountCheckout == null) {
//                    accountCheckout = new AccountCheckout(getCustomerADAccount(oper.getCustomer()),checkout, BigDecimal.ZERO, BigDecimal.ZERO);
//                    accountCheckoutMap.put(getCustomerADCode(oper.getCustomer()), accountCheckout);
//                    checkout.getAccountCheckouts().add(accountCheckout);
//                }
//                if (oper.getOperType().getAdDirection().equals(Accounting.Direction.CREDIT)){
//                    accountCheckout.setCreditMoney(accountCheckout.getCreditMoney().add(oper.getAdvanceReceivable()));
//                }else{
//                    accountCheckout.setDebitMoney(accountCheckout.getDebitMoney().add(oper.getAdvanceReceivable()));
//                }
//            }
//
//            if (oper.getOperType().getAcDirection() != null) {
//                accountCheckout = accountCheckoutMap.get(getCustomerADCode(oper.getCustomer()));
//                if (accountCheckout == null) {
//                    accountCheckout = new AccountCheckout(getCustomerADAccount(oper.getCustomer()),checkout, BigDecimal.ZERO, BigDecimal.ZERO);
//                    accountCheckoutMap.put(getCustomerADCode(oper.getCustomer()), accountCheckout);
//                    checkout.getAccountCheckouts().add(accountCheckout);
//                }
//                if (oper.getOperType().getAdDirection().equals(Accounting.Direction.CREDIT)){
//                    accountCheckout.setCreditMoney(accountCheckout.getCreditMoney().add(oper.getAdvanceReceivable()));
//                }else{
//                    accountCheckout.setDebitMoney(accountCheckout.getDebitMoney().add(oper.getAdvanceReceivable()));
//                }
//            }
//
//
//
//        }
//
//        return true;
//    }
//
//    private com.dgsoft.common.utils.finance.Account getCustomerADAccount(Customer customer) {
//        com.dgsoft.common.utils.finance.Account result = erpEntityManager.find(Account.class, getCustomerADCode(customer));
//        if (result == null) {
//           return new Account();//TODO fill
//        }
//        return result;
//    }
//
//    private com.dgsoft.common.utils.finance.Account getCustomerACAccount(Customer customer) {
//        com.dgsoft.common.utils.finance.Account result = erpEntityManager.find(Account.class, getCustomerACCode(customer));
//        if (result == null) {
//            return new Account();//TODO fill
//        }
//        return result;
//    }
//
//    private com.dgsoft.common.utils.finance.Account getCustomerPACAccount(Customer customer) {
//        com.dgsoft.common.utils.finance.Account result = erpEntityManager.find(Account.class, getCustomerPACCode(customer));
//        if (result == null) {
//            return new Account();//TODO fill
//        }
//        return result;
//    }
//
//    @In
//    private RunParam runParam;
//
//    private String getCustomerADCode(Customer customer) {
//        return runParam.getStringParamValue("erp.finance.advance").trim() + customer.getId().trim();
//    }
//
//    private String getCustomerACCode(Customer customer) {
//        return runParam.getStringParamValue("erp.finance.customerAccount").trim() + customer.getId().trim();
//    }
//
//    private String getCustomerPACCode(Customer customer) {
//        return runParam.getStringParamValue("erp.finance.proxyAccount").trim() + customer.getId().trim();
//    }

}
