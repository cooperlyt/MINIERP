package com.dgsoft.erp.action;

import com.dgsoft.common.helper.DataFormat;
import com.dgsoft.common.system.model.Employee;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Cars;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 1/5/14
 * Time: 8:02 AM
 * To change this template use File | Settings | File Templates.
 */
@Name("carsList")
public class CarsList extends ErpEntityQuery<Cars> {

    private static final String EJBQL = "select cars from Cars cars";

    private static final String[] RESTRICTIONS = {
            "lower(cars.id) like lower(concat(#{carsList.searchCarCode},'%'))",
            "cars.employeeId in (#{carsList.searchDriverIds})"};


    public CarsList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    private String searchCarCode;

    private String searchDriverName;

    @In
    private EntityManager systemEntityManager;

    public List<String> getSearchDriverIds() {
        if (!DataFormat.isEmpty(searchDriverName)) {
            List<String> result = new ArrayList<String>();

            for (Employee emp : systemEntityManager.createQuery("select emp from Employee emp where lower(emp.person.name) like lower(concat(:searchName,'%'))", Employee.class).setParameter("searchName", searchDriverName).getResultList()) {
               result.add(emp.getId());
            }
            return result;
        } else
            return null;

    }


    public String getSearchCarCode() {
        return searchCarCode;
    }

    public void setSearchCarCode(String searchCarCode) {
        this.searchCarCode = searchCarCode;
    }

    public String getSearchDriverName() {
        return searchDriverName;
    }

    public void setSearchDriverName(String searchDriverName) {
        this.searchDriverName = searchDriverName;
    }
}
