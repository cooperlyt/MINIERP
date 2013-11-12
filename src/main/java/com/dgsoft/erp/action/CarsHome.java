package com.dgsoft.erp.action;


import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Cars;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/12/13
 * Time: 11:39 AM
 */
@Name("carsHome")
public class CarsHome extends ErpEntityHome<Cars> {


    public void setSearchId(String id) {
        try {
            setId(id);
        } catch (org.jboss.seam.framework.EntityNotFoundException e) {
            setId(null);
        }
    }

    public String getSearchId() {
        try {
            return getInstance().getId();
        } catch (org.jboss.seam.framework.EntityNotFoundException e) {
            setId(null);
        }
        return "";
    }

}
