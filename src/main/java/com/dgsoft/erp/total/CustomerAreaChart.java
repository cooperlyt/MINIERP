package com.dgsoft.erp.total;

import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.In;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-26
 * Time: 上午10:43
 */
public abstract class CustomerAreaChart {

    @In(create = true)
    protected EntityManager erpEntityManager;


    private List<CustomerArea> customerAreas;

    public List<CustomerArea> getCustomerAreas() {
        if (customerAreas == null) {
            customerAreas = erpEntityManager.createQuery("select area from CustomerArea area ", CustomerArea.class).getResultList();
        }
        return customerAreas;
    }


    protected void fillAreaData(Map<String, OrderMoneySeries> data) {
        for (CustomerArea area : getCustomerAreas()) {
            if (data.get(area.getId()) == null) {
                data.put(area.getId(), new OrderMoneySeries(area.getName()));
            } else {
                data.get(area.getId()).setKey(area.getName());
            }
        }
    }

    protected OrderMoneySeries totalDatas(Collection<OrderMoneySeries> datas) {
        BigDecimal resultMoney = BigDecimal.ZERO;
        Long resultCount = new Long(0);
        for (OrderMoneySeries data : datas) {
            resultCount += data.getCount();
            resultMoney = resultMoney.add(data.getMoney());
        }
        return new OrderMoneySeries(resultMoney,resultCount);
    }


}
