package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.tools.OrderStateCondition;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 02/04/14
 * Time: 09:03
 */

public abstract class OrderStateSearch {

    @In(create = true)
    protected EntityManager erpEntityManager;

    protected OrderStateCondition orderState = new OrderStateCondition();

    protected SearchDateArea dateArea = new SearchDateArea(new Date(), new Date());

    public OrderStateCondition getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderStateCondition orderState) {
        this.orderState = orderState;
    }

    public SearchDateArea getDateArea() {
        return dateArea;
    }

    public void setDateArea(SearchDateArea dateArea) {
        this.dateArea = dateArea;
    }



}
