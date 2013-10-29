package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 8:14 AM
 * To change this template use File | Settings | File Templates.
 */
@Name("customerAreaHome")
public class CustomerAreaHome extends ErpSimpleEntityHome<CustomerArea> {

    @In
    private FacesMessages facesMessages;

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getCustomers().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "cantDeleteCustomerArea");
            return false;
        }
        return true;
    }

}
