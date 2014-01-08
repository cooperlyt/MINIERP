package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import org.apache.avro.generic.GenericData;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/13/13
 * Time: 4:51 PM
 */
@Name("dispatchHome")
public class DispatchHome extends ErpEntityHome<Dispatch> {

    public boolean isCanAddRes(){
        return  getInstance().getNeedRes().getCustomerOrder().getPayType().equals(CustomerOrder.OrderPayType.OVERDRAFT) ||
                getInstance().getNeedRes().getCustomerOrder().getPayType().equals(CustomerOrder.OrderPayType.COMPLETE_PAY);
    }

    public boolean isHaveOverlyOut(){
        return !getInstance().getOverlyOuts().isEmpty();
    }

}
