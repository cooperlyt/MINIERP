package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Name("outNumberOrderList")
public class OutNumberOrderList  {

    private static final String EJBQL = "select od.needRes.customerOrder from OutNumber onb left join fetch onb.stockChange sc left join sc.orderDispatchs od ";

    private static final String[] RESTRICTIONS = {

            "lower(onb.prefx) = lower(concat(#{orderList.customerName},'%'))",
            "customerOrder.createDate >= #{orderList.createDateFrom}",
            "customerOrder.createDate <= #{orderList.dateTo}",
            "lower(customerOrder.id)  like lower(concat('%',#{orderList.orderId}))",
            "customerOrder.canceled = #{orderList.canceled}",
            "customerOrder.allStoreOut = #{orderList.allStoreOut}",
            "customerOrder.payType = #{orderList.payType}",
            "customerOrder.moneyComplete = #{orderList.moneyComplete}"};

    private String before;
    private String after;
    private  int number;

    private List<CustomerOrder> resultList = new ArrayList<CustomerOrder>();

    @In
    private EntityManager erpEntityManager;

    public String getBefore() {
        if (before == null){
            return "";
        }
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        if (after == null){
            return "";
        }
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public void search(){
        resultList = erpEntityManager.createQuery("select od.needRes.customerOrder from OutNumber onb left join  onb.stockChange sc left join sc.orderDispatchs od where  onb.prefx = :before and onb.after = :after and onb.begin <= :number and onb.end >= :number",CustomerOrder.class)
        .setParameter("before", getBefore()).setParameter("after",getAfter()).setParameter("number",getNumber()).getResultList();
    }

    public List<CustomerOrder> getResultList() {
        return resultList;
    }


}
