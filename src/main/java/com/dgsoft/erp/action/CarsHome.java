package com.dgsoft.erp.action;


import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Cars;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/12/13
 * Time: 11:39 AM
 */
@Name("carsHome")
public class CarsHome extends ErpEntityHome<Cars> {


    @In
    private FacesMessages facesMessages;

    public void setSearchId(String id) {
        try {
            setId(id);
            getInstance();
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

    @Override
    protected Cars createInstance() {
        return new Cars(true);
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (getInstance().getDispatches().isEmpty()) {
            return true;
        }else{
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"cant_delete");
            return false;
        }
    }

}
