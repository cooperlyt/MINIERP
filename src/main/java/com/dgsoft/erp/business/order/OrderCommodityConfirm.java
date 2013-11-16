package com.dgsoft.erp.business.order;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 4:24 PM
 */
@Name("orderCommodityConfirm")
public class OrderCommodityConfirm extends OrderTaskHandle {

    @Out(value = "orderConfirmed", scope = ScopeType.CONVERSATION, required = false)
    private Boolean orderconfirmed;


    protected String completeOrderTask() {

        //TODO set orderconfirmed to false
        orderconfirmed = true;
        orderHome.getInstance().setResReceived(true);
        if ("updated".equals(orderHome.update())) {
            return "taskComplete";
        } else {
            return "fail";
        }

    }

}
