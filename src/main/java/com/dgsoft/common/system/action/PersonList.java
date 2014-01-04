package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityQuery;
import com.dgsoft.common.system.model.Person;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;

@Name("personList")
@Scope(ScopeType.CONVERSATION)
public class PersonList extends SystemEntityQuery<Person> {

	private static final String EJBQL = "select person from Person person";

	private static final String[] RESTRICTIONS = {
			"lower(person.credentialsNumber) like lower(concat(#{personList.person.credentialsNumber},'%'))",
			"lower(person.name) like lower(concat(#{personList.person.name},'%'))",};

	private Person person = new Person();

	public PersonList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Person getPerson() {
		return person;
	}
}
