package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.BankAccount;
import com.dgsoft.erp.model.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Created by cooper on 3/1/14.
 */
@Name("bankAccountHome")
public class BankAccountHome extends ErpSimpleEntityHome<BankAccount> {


    @In
    private FacesMessages facesMessages;

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getMoneySaves().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "CantRemoveBankAccount");
            return false;
        }
        return true;
    }

    @Override
    protected BankAccount createInstance(){
        return new BankAccount(true);
    }
}
