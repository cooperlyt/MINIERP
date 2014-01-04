package com.dgsoft.common.system;

import com.dgsoft.common.system.action.PersonHome;
import com.dgsoft.common.system.model.Person;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/2/13
 * Time: 12:02 PM
 */
@Name("personInput")
public class PersonInput {

    private String credentialsNumber;

    private Person.CredentialsType cerdentialsType;

    @In(create = true)
    private PersonHome personHome;

    @Logger
    protected Log log;

    @In
    private FacesMessages facesMessages;

    public String getCredentialsNumber() {
        return credentialsNumber;
    }

    public void setCredentialsNumber(String credentialsNumber) {
        this.credentialsNumber = credentialsNumber;
    }

    public Person.CredentialsType getCerdentialsType() {
        return cerdentialsType;
    }

    public void setCerdentialsType(Person.CredentialsType cerdentialsType) {
        this.cerdentialsType = cerdentialsType;
    }

    public void credentialsChangeListener() {


        if ((cerdentialsType == null) || (credentialsNumber == null) || (credentialsNumber.trim().equals("")))
            return;


        if ((credentialsNumber.equals(personHome.getInstance().getCredentialsNumber())) &&
                (cerdentialsType.equals(personHome.getInstance().getCredentialsType()))) {
            return;
        }

        if (cerdentialsType != Person.CredentialsType.OTHER) {

            List<Person> persons = personHome.getEntityManager().createQuery("select person from Person person where person.credentialsNumber =?1 and person.credentialsType = ?2").setParameter(1, credentialsNumber).setParameter(2, cerdentialsType).getResultList();
            if (!persons.isEmpty()) {
                personHome.setId(persons.get(0).getId());
                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "person_read_from_database", personHome.getInstance().getName());
                return;
            }
        }

        //if (personHome.isManaged())
            //personHome.clearInstance();
        personHome.setId("");
        personHome.getInstance().setCredentialsNumber(credentialsNumber);
        personHome.getInstance().setCredentialsType(cerdentialsType);


    }

}
