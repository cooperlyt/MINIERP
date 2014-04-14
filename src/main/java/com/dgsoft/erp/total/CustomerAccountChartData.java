package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 18/03/14
 * Time: 11:22
 */
@Name("customerAccountChartData")
public class CustomerAccountChartData {


    @In(create = true)
    private EntityManager erpEntityManager;


    public Number getCustomerCount(){
        return (Number) erpEntityManager.createQuery("select count(customer.id) as c from Customer customer where customer.enable = true").getSingleResult();
    }

    public Map<String,Number> getCustomerMoneyCountSum() {
        Map<String,Number> result = new HashMap<String, Number>();

        result.put("overdraftCount",
                (Number) erpEntityManager.createQuery("select count(customer.id) as c from Customer customer where customer.balance < 0 and customer.enable = true").getSingleResult());

        result.put("depositCount",
                (Number) erpEntityManager.createQuery("select count(customer.id) as c from Customer customer where customer.balance > 0 and customer.enable = true").getSingleResult());

        result.put("zeroCount",
                (Number) erpEntityManager.createQuery("select count(customer.id) as c from Customer customer where customer.balance = 0 and customer.enable = true").getSingleResult());

        return result;
    }

    public String getCustomerOverdraftMoneyTotal(){
        return DecimalFormat.getCurrencyInstance(Locale.CHINA).format((BigDecimal) erpEntityManager.createQuery("select sum(customer.balance) as c from Customer customer where customer.balance < 0 and customer.enable = true ").getSingleResult());
    }

    public Map<String,Number> getCustomerOverdraftMoneySum(){
        Map<String,Number> result = new HashMap<String, Number>();
        List qResult = erpEntityManager.createQuery("select count(customer.id) as c, sum(customer.balance) * -1 as b ,max(customer.customerArea.name) as n from Customer customer where customer.balance < 0 and customer.enable = true group by customer.customerArea.id").getResultList();
        for(Object o: qResult){

            result.put(((Object[])o)[2] + " " + DecimalFormat.getCurrencyInstance(Locale.CHINA).format((BigDecimal) ((Object[]) o)[1]) + "(" + ((Object[])o)[0] + ")" ,  (Number)((Object[])o)[1]);
        }
        return result;
    }


    public String getCustomerDepositMoneyTotal(){
        return DecimalFormat.getCurrencyInstance(Locale.CHINA).format((Number) erpEntityManager.createQuery("select sum(customer.balance) as c from Customer customer where customer.balance > 0 and customer.enable = true ").getSingleResult());
    }


    public Map<String,Number> getCustomerDepositMoneySum(){
        Map<String,Number> result = new HashMap<String, Number>();
        List qResult = erpEntityManager.createQuery("select count(customer.id) as c, sum(customer.balance) as b ,max(customer.customerArea.name) as n from Customer customer where customer.balance > 0 and customer.enable = true group by customer.customerArea.id").getResultList();
        for(Object o: qResult){
            result.put(((Object[])o)[2] + " " + DecimalFormat.getCurrencyInstance(Locale.CHINA).format((BigDecimal)((Object[]) o)[1]) + "(" + ((Object[])o)[0] + ")" , (Number)((Object[])o)[1]);
        }
        return result;
    }


}
