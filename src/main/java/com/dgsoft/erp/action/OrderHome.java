package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import com.dgsoft.erp.model.api.DeliveryType;
import com.dgsoft.erp.model.api.FarePayType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:11 AM
 */
@Name("orderHome")
public class OrderHome extends ErpEntityHome<CustomerOrder>{

    @Factory(value = "deliveryTypes", scope = ScopeType.CONVERSATION)
    public DeliveryType[] getDeliveryTypes(){
        return DeliveryType.values();
    }

    @Factory(value= "farePayTypes", scope = ScopeType.CONVERSATION)
    public FarePayType[] getFarePayTypes(){
        return FarePayType.values();
    }


    public BigDecimal getTotalFare(){
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes nr: getInstance().getNeedReses()){
            for(Dispatch dispatch: nr.getDispatches()){
                if (dispatch.getFare()!= null){
                    result = result.add(dispatch.getFare());
                }
            }
        }
        return result;
    }

    public NeedRes getMasterNeedRes(){
        for (NeedRes nr: getInstance().getNeedReses()){
            if (nr.getType().equals(NeedRes.NeedResType.ORDER_SEND)){
                return nr;
            }
        }
        return null;
    }
}
