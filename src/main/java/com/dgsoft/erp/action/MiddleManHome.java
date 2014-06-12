package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.MiddleMan;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("middleManHome")
public class MiddleManHome extends ErpSimpleEntityHome<MiddleMan>{

    @In
    private FacesMessages facesMessages;


    @Override
    protected MiddleMan createInstance(){
        return new MiddleMan(true);
    }

    @Override
    protected boolean verifyRemoveAvailable() {

        if ( getInstance().getCustomers().isEmpty() && getInstance().getMiddleMoneys().isEmpty()){
            return true;
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"usageCantDelete");
            return false;
        }
    }
}
