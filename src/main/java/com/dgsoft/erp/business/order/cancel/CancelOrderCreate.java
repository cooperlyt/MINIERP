package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.OrderBackHome;
import com.dgsoft.erp.action.OrderHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/21/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("cancelOrderCreate")

public class CancelOrderCreate {

    @In
    private OrderBackHome orderBackHome;

    @In(create = true)
    private BusinessCreate businessCreate;

    @In
    private OrderHome orderHome;

    public String cancelOrder(){
        //TODO: (!orderHome.isAnyOneStoreOut()) && (!orderHome.isAnyOneMoneyPay());
        if (!orderHome.isAnyOneMoneyPay()){
            orderBackHome.getInstance().setMoney(BigDecimal.ZERO);
        }

        if (orderBackHome.getInstance().getMoney().compareTo(BigDecimal.ZERO) <= 0){
            return orderBackHome.cancelSimpleOrder();
        }else{
            return businessCreate.create();
        }
    }
}
