package com.dgsoft.erp.total;

import com.dgsoft.erp.total.data.CustomerData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-22
 * Time: 下午1:49
 */
@Name("customerDataTotal")
public class CustomerDataTotal {

    private static final String EJBQL = "select new com.dgsoft.erp.total.data.CustomerData(" +
            "customer.id,customer.name,customer.type,customer.customerArea.name,customer.customerLevel.name,customer.customerLevel.priority,customer.provinceCode," +
            "customer.createDate, (customer.advanceMoney - customer.accountMoney) as noProxyBalance, (customer.advanceMoney - customer.accountMoney - customer.proxyAccountMoney) as balance," +

            // 历年
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true) as orderCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true ) as orderMoney," +

            // 本月
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE) and MONTH(o.createDate) = MONTH(CURRENT_DATE) ) as curMonthCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE) and MONTH(o.createDate) = MONTH(CURRENT_DATE) ) as curMonthMoney," +

            // 上月
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = :beforMonthYear and MONTH(o.createDate) = :beforMonth ) as beforMonthCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = :beforMonthYear  and MONTH(o.createDate) = :beforMonth ) as beforMonthMoney," +


            // 本年
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE)) as curYearCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE) ) as curYearMoney," +


            // 上年
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE) - 1) as beforYearCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and YEAR(o.createDate) = YEAR(CURRENT_DATE) - 1 ) as beforYearMoney) " +
            " from Customer customer where customer.enable = true";

    @In
    private EntityManager erpEntityManager;

    public List<CustomerData> getResultList(){
        String jpql = EJBQL;
        String moneyPath;
        if (useNoProxyBalance){
            moneyPath = " customer.advanceMoney - customer.accountMoney ";
        }else {
            moneyPath = " customer.advanceMoney - customer.accountMoney - customer.proxyAccountMoney ";
        }

        if (! containDebit){
            jpql +=  " and" + moneyPath +  "<= 0 ";
        }
        if (!containCredit){
            jpql +=  " and" + moneyPath +  "<= 0 ";
        }
        if (!containZero){
            jpql +=  " and" + moneyPath +  "<> 0 ";
        }
        jpql += " order by " + orderField;

        if (isDesc()){
            jpql += " desc";
        }

        return erpEntityManager.createQuery(jpql, CustomerData.class)
                .setParameter("beforMonthYear",getBeforMonthYear())
                .setParameter("beforMonth",getBeforMonth()).getResultList();
    }

    private boolean useNoProxyBalance;

    private boolean containDebit;

    private boolean containCredit;

    private boolean containZero;

    private boolean desc;

    private String orderField;

    public boolean isContainDebit() {
        return containDebit;
    }

    public void setContainDebit(boolean containDebit) {
        this.containDebit = containDebit;
    }

    public boolean isContainCredit() {
        return containCredit;
    }

    public void setContainCredit(boolean containCredit) {
        this.containCredit = containCredit;
    }

    public boolean isContainZero() {
        return containZero;
    }

    public void setContainZero(boolean containZero) {
        this.containZero = containZero;
    }

    public boolean isUseNoProxyBalance() {
        return useNoProxyBalance;
    }

    public void setUseNoProxyBalance(boolean useNoProxyBalance) {
        this.useNoProxyBalance = useNoProxyBalance;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public int getBeforMonthYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        return calendar.get(Calendar.YEAR);
    }

    public int getBeforMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        return calendar.get(Calendar.MONTH);
    }

}
