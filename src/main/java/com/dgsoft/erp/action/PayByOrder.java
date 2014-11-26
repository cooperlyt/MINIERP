package com.dgsoft.erp.action;

import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.MoneySave;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooper on 11/23/14.
 */
@Name("payByOrder")
@Scope(ScopeType.CONVERSATION)
public class PayByOrder extends MoneySaveBaseHome {


    @Override
    public Class<MoneySave> getEntityClass(){
        return MoneySave.class;
    }

    @In
    private OrderSelectList orderSelectList;

    public String orderSelectComplete() {
        if (orderSelectList.getSelectOrders().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "OrderMustSelect");
            return null;
        }

        int proxyCount = 0;
        for (CustomerOrder order : orderSelectList.getSelectOrders()) {
            if (order.getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                proxyCount++;
            }
        }
        if (proxyCount > 0) {
            if (orderSelectList.getSelectOrders().size() != proxyCount) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "OrderMustAllProxy");
                return null;
            }

            Map<Customer, AccountOper> opers = new HashMap<Customer, AccountOper>();
            for (CustomerOrder order : orderSelectList.getSelectOrders()) {
                AccountOper oper = opers.get(order.getCustomer());
                if (oper == null) {
                    oper = new AccountOper(getInstance(),order.getCustomer(), credentials.getUsername(), AccountOper.AccountOperType.PROXY_SAVINGS);
                    getAccountOpers().add(oper);
                    oper.setProxcAccountsReceiveable(order.getMoney());
                    opers.put(order.getCustomer(), oper);
                } else {
                    oper.setProxcAccountsReceiveable(oper.getProxcAccountsReceiveable().add(order.getMoney()));
                }


            }

            for(Map.Entry<Customer, AccountOper> entry : opers.entrySet()){
                BigDecimal customerProxyAM = entry.getKey().getProxyAccountMoney();
                if (customerProxyAM == null) {
                    customerProxyAM = BigDecimal.ZERO;
                }
                if (customerProxyAM.compareTo(entry.getValue().getProxcAccountsReceiveable()) < 0){
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"CustomerProxyAccountGt",entry.getKey().getName(),customerProxyAM,entry.getValue().getProxcAccountsReceiveable());
                    entry.getValue().setProxcAccountsReceiveable(BigDecimal.ZERO);
                }
            }

            return "proxy";
        } else {
            Customer customer = null;
            for (CustomerOrder order : orderSelectList.getSelectOrders()) {
                if ((customer != null) && !customer.getId().equals(order.getCustomer().getId())) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "OrderMustSameCustomer");
                    return null;
                }
                customer = order.getCustomer();
            }

            getAccountOpers().add(new AccountOper(getInstance(),customer, credentials.getUsername(), AccountOper.AccountOperType.CUSTOMER_SAVINGS));


            return "customer";
        }

    }

    @Transactional
    public String receiveMoney() {
        if (getOutMoney().compareTo(BigDecimal.ZERO) < 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "MoneyNotinf");
            return null;
        }

        if (getEditingOper().getOperType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)){
            BigDecimal outMoney = BigDecimal.ZERO;
            for(AccountOper oper: getAccountOpers()){
                outMoney = outMoney.add(oper.getAccountsReceivable());
                if (oper.getProxcAccountsReceiveable().compareTo(BigDecimal.ZERO) == 0){
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"CustomerProxyAccountGt",oper.getCustomer().getName(),oper.getCustomer().getProxyAccountMoney());
                    return null;
                }
            }
            if (outMoney.compareTo(getOutMoney()) != 0 ){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"ProxyOutMoneyError",getOutMoney(),outMoney);
                return null;
            }
        }else{
            if(getOutMoney().compareTo(BigDecimal.ZERO) < 0){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"MoneyNotinf");
                return null;
            }
            getEditingOper().setAccountsReceivable(getInstance().getMoney().add(getInstance().getRemitFee()));
        }

        for(CustomerOrder order: orderSelectList.getSelectOrders()){
            order.setPayTag(true);
            if(!order.isAccountChange() && (order.getMoney().compareTo(BigDecimal.ZERO) > 0) && order.getAccountOpers().isEmpty() ) {
                orderHome.setInstance(order);
                orderHome.signalMoney(operDate);
            }

        }

        joinTransaction();
        calcCustomerOrderPayTag();


        return "persisted".equals(persist()) ? "/func/erp/finance/cashier/CustomerMoneySavings.xhtml" : null;
    }

    public BigDecimal getOutMoney() {
        return getInstance().getMoney().add(getInstance().getRemitFee()).subtract(orderSelectList.getSelectOrderTotalMoney());
    }
}
