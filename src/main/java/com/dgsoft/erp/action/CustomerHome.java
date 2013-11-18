package com.dgsoft.erp.action;

import com.dgsoft.common.PinyinTools;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.util.Date;

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
        return haveMiddleMan;
    }

    public void setHaveMiddleMan(boolean haveMiddleMan) {
        this.haveMiddleMan = haveMiddleMan;
    }

    @Override
    protected Customer createInstance() {
        return new Customer(true);
    }

    @Override
    protected void initInstance() {
        super.initInstance();
        if (isIdDefined()) {
            haveMiddleMan = getInstance().getMiddleMan() != null;
            if (haveMiddleMan)
                middleManHome.setId(getInstance().getMiddleMan().getId());
        } else {
            haveMiddleMan = false;
        }

    }

    @Override
    public boolean wire() {
        if (haveMiddleMan) {
            if ((isIdDefined() && middleManHome.isIdDefined()) || (!isIdDefined())) {
                getInstance().setMiddleMan(middleManHome.getReadyInstance());
            }
        } else {
            getInstance().setMiddleMan(null);
        }

        if(!isManaged()){
            getInstance().setCreateDate(new Date());
        }
        return true;
    }


    public void middleManPayChangeListener() {


        if (haveMiddleMan && isIdDefined() && (getInstance().getMiddleMan() != null)) {
            middleManHome.setId(getInstance().getMiddleMan().getId());
        } else {
            middleManHome.clearInstance();
        }
    }
}
