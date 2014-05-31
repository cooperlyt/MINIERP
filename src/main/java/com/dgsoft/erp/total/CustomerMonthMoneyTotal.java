package com.dgsoft.erp.total;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.AccountCheckout;
import com.dgsoft.erp.model.Checkout;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 5/31/14.
 */
@Name("customerMonthMoneyTotal")
public class CustomerMonthMoneyTotal {

    public static class CustomerACAccountCheckout{

        private Customer customer;

        private AccountCheckout accountCheckout;

        public CustomerACAccountCheckout(Customer customer, AccountCheckout accountCheckout) {
            this.customer = customer;
            this.accountCheckout = accountCheckout;
        }

        public Customer getCustomer() {
            return customer;
        }

        public AccountCheckout getAccountCheckout() {
            return accountCheckout;
        }
    }


    @In
    private EntityManager erpEntityManager;

    public List<CustomerACAccountCheckout> getCustomerAccountCheckOut(int year, int month, boolean desc ,
                                              boolean conatinDebit, boolean containCredit, boolean containZero) {
        try {
            Checkout checkout = erpEntityManager.createQuery("select checkout from Checkout checkout " +
                    "where year(checkout.closeDate) = :year and month(checkout.closeDate) = :month", Checkout.class)
                    .setParameter("year", year).setParameter("month", month).getSingleResult();

            String searchJpql = "select accountCheckout from AccountCheckout accountCheckout " +
                    "where accountCheckout.checkout.id =:checkoutId and accountCheckout.accountCode like :code ";

            if (!conatinDebit){
                searchJpql += " and accountCheckout.closingBalance <= 0 ";
            }

            if (!containCredit){
                searchJpql += " and accountCheckout.closingBalance >= 0 ";
            }

            if (!containZero){
                searchJpql += " and accountCheckout.closingBalance <> 0 ";
            }

            searchJpql += " order by accountCheckout.closingBalance ";

            if (desc){
                searchJpql += " desc";
            }

            String customerAccountCode = RunParam.instance().getStringParamValue("erp.finance.customerAccount");

            List<AccountCheckout> accountCheckouts = erpEntityManager.createQuery(searchJpql, AccountCheckout.class)
                    .setParameter("checkoutId",checkout.getId())
                    .setParameter("code",customerAccountCode + "%").getResultList();

            Map<String,Customer > customerMap = new HashMap<String, Customer>();

            for(Customer customer: erpEntityManager.createQuery("select customer from Customer customer",Customer.class).getResultList()){
                customerMap.put(customerAccountCode + customer.getId(),customer);
            }

            List<CustomerACAccountCheckout> result = new ArrayList<CustomerACAccountCheckout>(accountCheckouts.size());

            for(AccountCheckout accountCheckout: accountCheckouts){
                result.add(new CustomerACAccountCheckout(customerMap.get(accountCheckout.getAccountCode()),accountCheckout));
            }

            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
}
