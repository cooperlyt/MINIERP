package com.dgsoft.erp.action;

import com.dgsoft.common.PinyinTools;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerContact;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @In
    private FacesMessages facesMessages;

    private boolean haveMiddleMan;

    @DataModel("editingCustomerContacts")
    private List<CustomerContact> customerContactList;

    @DataModelSelection
    private CustomerContact selectedContact;

    public boolean isHaveMiddleMan() {
        return haveMiddleMan;
    }

    public void setHaveMiddleMan(boolean haveMiddleMan) {
        this.haveMiddleMan = haveMiddleMan;
    }


    public void addContact() {

        CustomerContact newContact = new CustomerContact(getInstance());
        customerContactList.add(newContact);
        getInstance().getCustomerContacts().add(newContact);

    }

    public void removeContact() {
        if (customerContactList.size() <= 1) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"customer_must_have_contace");
        } else {
            customerContactList.remove(selectedContact);
            getInstance().getCustomerContacts().remove(selectedContact);
        }
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
            customerContactList = new ArrayList<CustomerContact>(getInstance().getCustomerContacts());

        } else {
            haveMiddleMan = false;
            customerContactList = new ArrayList<CustomerContact>();
            addContact();
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

        if (!isManaged()) {
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
