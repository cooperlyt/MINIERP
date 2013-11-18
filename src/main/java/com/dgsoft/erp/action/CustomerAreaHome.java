package com.dgsoft.erp.action;

import com.dgsoft.common.system.action.RoleHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
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

    @In(create = true)
    private RoleHome roleHome;

    private boolean autoGenerateRole;

    public boolean isAutoGenerateRole() {
        return autoGenerateRole;
    }

    public void setAutoGenerateRole(boolean autoGenerateRole) {
        this.autoGenerateRole = autoGenerateRole;
    }

    @Override
    @Begin(flushMode = FlushModeType.MANUAL)
    public String createNew(){

        autoGenerateRole = true;
        return super.createNew();
    }


    @Override
    protected boolean wire(){
        if (!isManaged() && autoGenerateRole){
            String role = "saleArea." + getInstance().getId();
            roleHome.clearInstance();
            roleHome.getInstance().setId(role);
            roleHome.getInstance().setName(getInstance().getName());
            roleHome.getInstance().setPriority(1000);
            roleHome.persist();
            getInstance().setRole(role);
        }
        return true;
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getCustomers().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "cantDeleteCustomerArea");
            return false;
        }
        return true;
    }

}
