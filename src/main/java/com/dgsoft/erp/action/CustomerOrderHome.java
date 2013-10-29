package com.dgsoft.erp.action;

import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessInstance;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/24/13
 * Time: 11:05 AM
 */
@Name("customerOrderHome")
public class CustomerOrderHome extends ErpEntityHome<CustomerOrder>{

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private MiddleManHome middleManHome;

    @In(required = false)
    private CustomerAreaHome customerAreaHome;

    @In(create = true)
    private StartData startData;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    public void clearCustomer(){
        customerHome.clearInstance();
        middleManHome.clearInstance();
    }

    public String beginCreateOrder(){
        businessDefineHome.setId("erp.business.order");
        startData.generateKey();
        return "beginning";
    }

    @Override
    protected boolean wire() {
        if (!customerHome.isIdDefined() && getInstance().isMiddleManPay()){
            customerHome.getInstance().setMiddleMan(middleManHome.getInstance());
        }
        getInstance().setId(startData.getBusinessKey());
        if (customerHome.isIdDefined()){
            getInstance().setCustomer(customerHome.getInstance());
        }else{
            getInstance().setCustomer(customerHome.getReadyInstance());
            getInstance().getCustomer().setCustomerArea(customerAreaHome.getInstance());
        }
        return true;
    }


    @Observer("com.dgsoft.BusinessCreated.order")
    @Transactional
    public void createOrder(BusinessInstance businessInstance){

        this.persist();
    }

}
