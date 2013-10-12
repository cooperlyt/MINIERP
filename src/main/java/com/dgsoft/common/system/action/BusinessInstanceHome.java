package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.BusinessInstance;
import com.dgsoft.common.system.model.Employee;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/9/13
 * Time: 8:36 AM
 */
@Name("businessInstanceHome")
public class BusinessInstanceHome extends SystemEntityHome<BusinessInstance> {

    @Factory(value = "businessState", scope = ScopeType.SESSION)
    public BusinessInstance.BusinessState[] getBusinessState() {
        return BusinessInstance.BusinessState.values();
    }

    @In("#{authInfo.loginEmployee}")
    private Employee loginEmployee;

    @In(create = true)
    private EmployeeHome employeeHome;

    @Override
    @Transactional
    protected BusinessInstance createInstance(){
       employeeHome.setId(loginEmployee.getId());
       return new BusinessInstance(employeeHome.getInstance());
    }

}
