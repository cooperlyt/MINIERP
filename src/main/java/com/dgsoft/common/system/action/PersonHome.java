package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.Person;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/15/13
 * Time: 1:55 PM
 */
@Name("personHome")
public class PersonHome extends SystemEntityHome<Person> {

    @Factory(value = "sexs", scope = ScopeType.SESSION)
    public Person.Sex[] getSexs() {
        return Person.Sex.values();
    }

    @Factory(value = "credentialsTypes",scope = ScopeType.SESSION )
    public Person.CredentialsType[] getCredentialsTypes(){
        return Person.CredentialsType.values();
    }

    @In("#{messages.person_nationality_default}")
    private String defaultNationality;

    @In("#{messages.person_ethnic_default}")
    private String defaultEthnic;

    @Override
    protected Person createInstance(){
        return new Person(defaultEthnic,defaultNationality);
    }

}
