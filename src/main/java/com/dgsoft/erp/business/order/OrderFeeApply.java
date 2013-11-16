package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.OrderFee;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/16/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("orderFee")
public class OrderFeeApply extends OrderTaskHandle{

    private List<OrderFee> orderFeeList;



    public List<OrderFee> getOrderFeeList() {
        return orderFeeList;
    }

    public void setOrderFeeList(List<OrderFee> orderFeeList) {
        this.orderFeeList = orderFeeList;
    }

    public void middleMoneyRateChangeListener(){
        if ((orderHome.getInstance().getMiddleRate() == null) ||
                (orderHome.getInstance().getMiddleRate().compareTo(BigDecimal.ZERO) == 0)){
            orderHome.getInstance().setMiddleMoney(BigDecimal.ZERO);
            return;
        }
        orderHome.getInstance().setMiddleMoney(
                orderHome.getInstance().getMoney().multiply(
                        orderHome.getInstance().getMiddleRate().divide(new BigDecimal("100"),20,BigDecimal.ROUND_HALF_UP)));
    }

    public void middleMoneyChangeListener(){
        if ((orderHome.getInstance().getMiddleMoney() == null) ||
                (orderHome.getInstance().getMiddleMoney().compareTo(BigDecimal.ZERO) == 0)){
            orderHome.getInstance().setMiddleRate(BigDecimal.ZERO);
            return ;
        }
        orderHome.getInstance().setMiddleRate(
                orderHome.getInstance().getMiddleMoney().divide(orderHome.getInstance().getMoney(),20,BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal("100")));
    }

    protected String initOrderTask(){
        orderFeeList = new ArrayList<OrderFee>();
        //orderFeeList.add()


        return "success";
    }

}
