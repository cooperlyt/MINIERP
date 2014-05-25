package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 18/03/14
 * Time: 11:22
 */
@Name("customerAccountChartData")
public class CustomerAccountChartData extends CustomerAreaChart {

    public void refresh() {
        areaArrearsData = null;
        areaSaveData = null;
    }

    private Map<Object, OrderMoneySeries> areaArrearsData;


    private Map<Object, OrderMoneySeries> areaSaveData;

    private void initAreaArrearsData() {
        if (areaArrearsData == null) {
            areaArrearsData = new HashMap<Object, OrderMoneySeries>();
            if (isUseSaleArea()) {


                for (OrderMoneySeries data : erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customer.customerArea.id," +
                        "abs(sum(customer.advanceMoney - customer.accountMoney)),count(customer.id) )  from Customer customer " +
                        "where customer.enable = true and (customer.advanceMoney - customer.accountMoney) < 0 group by customer.customerArea.id", OrderMoneySeries.class).getResultList()) {
                    areaArrearsData.put(data.getKey(), data);
                }
            } else {
                for (OrderMoneySeries data : erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customer.provinceCode," +
                        "abs(sum(customer.advanceMoney - customer.accountMoney)),count(customer.id) )  from Customer customer " +
                        "where customer.enable = true and (customer.advanceMoney - customer.accountMoney) < 0 group by customer.provinceCode", OrderMoneySeries.class).getResultList()) {
                    areaArrearsData.put(data.getKey(), data);
                }
            }


        }
    }

    public Map<Object, OrderMoneySeries> getAreaArrearsData() {
        initAreaArrearsData();
        return areaArrearsData;
    }

    public List<OrderMoneySeries> getAreaArrears() {
        return new ArrayList<OrderMoneySeries>(getAreaArrearsData().values());
    }

    private void initAreaSaveData() {
        if (areaSaveData == null) {
            areaSaveData = new HashMap<Object, OrderMoneySeries>();
            if (isUseSaleArea()) {
                for (OrderMoneySeries data : erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customer.customerArea.id," +
                        "sum(customer.advanceMoney - customer.accountMoney),count(customer.id) )  from Customer customer " +
                        "where customer.enable = true and (customer.advanceMoney - customer.accountMoney) > 0 group by customer.customerArea.id", OrderMoneySeries.class).getResultList()) {
                    areaSaveData.put(data.getKey(), data);
                }
            } else {
                for (OrderMoneySeries data : erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customer.provinceCode," +
                        "sum(customer.advanceMoney - customer.accountMoney),count(customer.id) )  from Customer customer " +
                        "where customer.enable = true and (customer.advanceMoney - customer.accountMoney) > 0 group by customer.provinceCode", OrderMoneySeries.class).getResultList()) {
                    areaSaveData.put(data.getKey(), data);
                }
            }

        }
    }

    public Map<Object, OrderMoneySeries> getAreaSaveData() {
        initAreaSaveData();
        return areaSaveData;
    }

    public List<OrderMoneySeries> getAreaSave() {
        return new ArrayList<OrderMoneySeries>(getAreaSaveData().values());
    }


    public OrderMoneySeries getArrearsTotal() {
        return totalDatas(getAreaArrears());
    }

    public OrderMoneySeries getSaveTotal() {
        return totalDatas(getAreaSave());
    }

    public List<Object> getValidCodes(){
        Set<Object> result = new HashSet<Object>();
        result.addAll(getAreaArrearsData().keySet());
        result.addAll(getAreaSaveData().keySet());
        return new ArrayList<Object>(result);
    }

}
