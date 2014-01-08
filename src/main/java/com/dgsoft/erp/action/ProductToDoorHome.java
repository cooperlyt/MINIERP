package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.ProductToDoor;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/8/14
 * Time: 4:24 PM
 */
@Name("productToDoorHome")
public class ProductToDoorHome extends ErpEntityHome<ProductToDoor>{

    @In(create=true)
    private CarsHome carsHome;


    @Override
    protected boolean wire(){
        getInstance().setCars(carsHome.getReadyInstance());
        return true;
    }

}
