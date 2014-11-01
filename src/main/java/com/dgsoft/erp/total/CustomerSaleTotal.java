package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.total.data.CustomerData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by cooper on 11/1/14.
 */
@Name("customerSaleTotal")
public class CustomerSaleTotal {

    private static final String EJBQL = "select new com.dgsoft.erp.total.data.CustomerData(customer.id,customer.name," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.createDate >= :dateFrom and o.createDate <= :dateTo) as beforYearCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.createDate >= :dateFrom and o.createDate <= :dateTo ) as beforYearMoney, " +

            // 历年
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false) as orderCount," +
            "(select COALESCE(sum(o.money),0) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false ) as orderMoney) " +

            " from Customer customer where customer.enable = true order by beforYearMoney desc ";

    @In
    private EntityManager erpEntityManager;


    public List<CustomerData> getResultList(){
        return erpEntityManager.createQuery(EJBQL, CustomerData.class).setParameter("dateFrom",searchDateArea.getDateFrom()).setParameter("dateTo",searchDateArea.getSearchDateTo()).getResultList();
    }


    private SearchDateArea searchDateArea = new SearchDateArea();

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }
}
