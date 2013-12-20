package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.erp.action.CustomerHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/20/13
 * Time: 4:50 PM
 */
@Name("orderBackMoney")
public class OrderBackMoney extends CancelOrderTaskHandle {

    private BigDecimal backMoney;

    public BigDecimal getBackMoney() {
        return backMoney;
    }

    public void setBackMoney(BigDecimal backMoney) {
        this.backMoney = backMoney;
    }

    @In(create = true)
    protected CustomerHome customerHome;

    @Override
    protected String initCancelOrderTask() {
        customerHome.setId(orderHome.getInstance().getCustomer().getId());
        return "success";
    }


    public void calcAllMoney(){
        backMoney = orderHome.getTotalReveiveMoney();
    }

    public void calcNoEarnestMoney(){
        backMoney = orderHome.getTotalReveiveMoney().subtract(orderHome.getReveiveEarnest());
    }

    @Override
    protected String completeOrderTask() {


        return "taskComplete";
    }

}
