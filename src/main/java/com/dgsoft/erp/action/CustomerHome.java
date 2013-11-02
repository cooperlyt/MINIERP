package com.dgsoft.erp.action;

import com.dgsoft.common.PinyinTools;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/25/13
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("customerHome")
public class CustomerHome extends ErpSimpleEntityHome<Customer> {

    @In(create = true)
    private MiddleManHome middleManHome;

    private boolean haveMiddleMan;

    public boolean isHaveMiddleMan() {
        if (isIdDefined()){
            haveMiddleMan = getInstance().getMiddleMan() !=  null;
        }
        return haveMiddleMan;
    }

    public void setHaveMiddleMan(boolean haveMiddleMan) {
        this.haveMiddleMan = haveMiddleMan;
    }



    @Override
    public boolean wire(){
        if (haveMiddleMan){
            if ((isIdDefined() && middleManHome.isIdDefined()) || (!isIdDefined())){
                getInstance().setMiddleMan(middleManHome.getReadyInstance());
            }
        }else{
            getInstance().setMiddleMan(null);
        }
        return true;
    }


    public void middleManPayChangeListener(){
        if (haveMiddleMan && isIdDefined() && (getInstance().getMiddleMan() !=  null)){
            middleManHome.setId(getInstance().getMiddleMan().getId());
        }else{
            if (!haveMiddleMan){
                getInstance().setMiddleMan(null);
            }
            middleManHome.clearInstance();
        }
    }
}
