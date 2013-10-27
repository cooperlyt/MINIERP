package com.dgsoft.erp.action;

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

    @In(create = true)
    private StartData startData;



    public void clearCustomer(){
        customerHome.clearInstance();
        middleManHome.clearInstance();
    }

    public void middleManPayChangeListener(){
       if (getInstance().isMiddleManPay() && customerHome.isIdDefined() && (customerHome.getInstance().getMiddleMan() !=  null)){
           middleManHome.setId(customerHome.getInstance().getMiddleMan().getId());
       }else{
           middleManHome.clearInstance();
       }
    }

    public boolean isShowMiddleMan(){
        return getInstance().isMiddleManPay() &&
                ((customerHome.isIdDefined() && (customerHome.getInstance().getMiddleMan() != null)) || !customerHome.isIdDefined());
    }


    @Observer("com.dgsoft.BusinessCreated.order")
    @Transactional
    public void createOrder(BusinessInstance businessInstance){
        if (!customerHome.isIdDefined() && getInstance().isMiddleManPay()){
            customerHome.getInstance().setMiddleMan(middleManHome.getInstance());
        }
        getInstance().setCustomer(customerHome.getInstance());
        //getInstance().setId();
        this.persist();
    }

}
