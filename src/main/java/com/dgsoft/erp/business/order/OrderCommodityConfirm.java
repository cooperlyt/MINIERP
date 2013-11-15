package com.dgsoft.erp.business.order;

import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 4:24 PM
 */
@Name("orderCommodityConfirm")
public class OrderCommodityConfirm extends OrderTaskHandle{

    private BigDecimal fare;

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }



    protected String initOrderTask(){
        return "success";
    }

    protected String completeOrderTask(){
        return "taskComplete";
    }

}
