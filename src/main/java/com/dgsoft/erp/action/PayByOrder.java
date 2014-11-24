package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.MoneySave;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 11/23/14.
 */
@Name("payByOrder")
@Scope(ScopeType.CONVERSATION)
public class PayByOrder extends ErpEntityHome<MoneySave> {

    @In
    private OrderSelectList orderSelectList;

    @In
    private FacesMessages facesMessages;

    @In
    private Credentials credentials;

    private SetLinkList<AccountOper> accountOpers;


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

            getAccountOpers().add(new AccountOper(customer, credentials.getUsername(), AccountOper.AccountOperType.CUSTOMER_SAVINGS));


            return "customer";
        }

    }


    public String receiveMoney(){
        if (getOutMoney().compareTo(BigDecimal.ZERO) < 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"MoneyNotinf");
            return null;
        }

        return null;
    }

    @Override
    public void initInstance() {
        super.initInstance();
        accountOpers = null;
    }

    @Override
    protected MoneySave createInstance(){
        MoneySave result = new MoneySave();
        result.setMoney(BigDecimal.ZERO);
        return result;
    }

    public SetLinkList<AccountOper> getAccountOpers() {
        if(accountOpers == null){
            accountOpers = new SetLinkList<AccountOper>(getInstance().getAccountOpers());
        }
        return accountOpers;
    }

    public AccountOper getEditingOper() {
        return accountOpers.get(0);
    }

    public BigDecimal getOutMoney(){
      return getInstance().getMoney().subtract(orderSelectList.getSelectOrderTotalMoney());
    }
}
