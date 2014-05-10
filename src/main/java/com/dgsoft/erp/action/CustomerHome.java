package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerContact;
import com.dgsoft.erp.tools.CustomerMoneyTool;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

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

    @In
    private EntityManager systemEntityManager;

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

    private List<String> cityList;

    public int getProvinceCode(){
        return getInstance().getProvinceCode();
    }

    public void setProvinceCode(int provinceCode){
        getInstance().setProvinceCode(provinceCode);
        cityList = null;
    }

    public List<String> getCityList(){
        if (cityList == null){
            cityList = systemEntityManager.createQuery("select city.name from City city where city.province.id = :pid order by city.id").setParameter("pid",getInstance().getProvinceCode()).getResultList();
        }
        return cityList;
    }

    public void addContact() {
        CustomerContact newContact = new CustomerContact(getInstance());
        customerContactList.add(newContact);
        getInstance().getCustomerContacts().add(newContact);

    }

    public void removeContact() {
        if (customerContactList.size() <= 1) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "customer_must_have_contace");
        } else {
            customerContactList.remove(selectedContact);
            getInstance().getCustomerContacts().remove(selectedContact);
        }
    }

    public List<String> getAllTelList() {
        Set<String> result = new HashSet<String>();
        for (CustomerContact contact : getInstance().getCustomerContacts()) {
            result.add(contact.getTel());
        }
        return new ArrayList<String>(result);
    }

    public List<String> getAllContactList() {
        Set<String> result = new HashSet<String>();
        for (CustomerContact contact : getInstance().getCustomerContacts()) {
            result.add(contact.getName());
        }
        return new ArrayList<String>(result);
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
            //if ((isIdDefined() && middleManHome.isIdDefined()) || (!isIdDefined())) {
            getInstance().setMiddleMan(middleManHome.getReadyInstance());
            //}
        } else {
            getInstance().setMiddleMan(null);
        }

        if (!isManaged()) {
            getInstance().setCreateDate(new Date());
        }
        return true;
    }

    public BigDecimal getCanUseAdvanceMoney(){
        BigDecimal result = getInstance().getAdvanceMoney().subtract(CustomerMoneyTool.instance().getOrderAdvance(getInstance().getId()));
        if (result.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }else{
            return result;
        }
    }

    public void clearCustomerAndMiddleMan() {
        clearInstance();
        setHaveMiddleMan(false);
        middleManHome.clearInstance();
    }


    public void middleManPayChangeListener() {
        if (haveMiddleMan && isIdDefined() && (getInstance().getMiddleMan() != null)) {
            middleManHome.setId(getInstance().getMiddleMan().getId());
        } else {
            middleManHome.clearInstance();
        }
    }
}
