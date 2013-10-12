package com.dgsoft.common.system;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.model.*;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

import javax.faces.event.ValueChangeEvent;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 8/22/13
 * Time: 3:50 PM
 */
public class AuthenticationInfo implements java.io.Serializable {

    private List<FuncCategory> authenticationFuncCategorys = new ArrayList<FuncCategory>();

    private List<BusinessCategory> authenticationBussinessCategorys = new ArrayList<BusinessCategory>();

    private RoleCategory currRoleCategory;

    private Employee loginEmployee;

    public Employee getLoginEmployee() {
        return loginEmployee;
    }

    public void setLoginEmployee(Employee loginEmployee) {
        this.loginEmployee = loginEmployee;
    }

    public RoleCategory getCurrRoleCategory() {
        return currRoleCategory;
    }

    public void setCurrRoleCategory(RoleCategory currRoleCategory) {
        this.currRoleCategory = currRoleCategory;
    }

    public List<FuncCategory> getAuthenticationFuncCategorys() {
        return authenticationFuncCategorys;
    }

    public void setAuthenticationFuncCategorys(List<FuncCategory> authenticationFuncCategorys) {
        this.authenticationFuncCategorys = authenticationFuncCategorys;
    }

    public List<BusinessCategory> getAuthenticationBussinessCategorys() {
        return authenticationBussinessCategorys;
    }

    public void setAuthenticationBussinessCategorys(List<BusinessCategory> authenticationBussinessCategorys) {
        this.authenticationBussinessCategorys = authenticationBussinessCategorys;
    }

    public void generateFuncCategorys() {

        authenticationFuncCategorys.clear();

        Map<String, FuncCategory> result = new HashMap<String, FuncCategory>();

        for (Function function : currRoleCategory.getFunctions()) {

            FuncCategory curCategory = result.get(function.getFuncCategory().getId());

            if (curCategory == null) {
                curCategory = new FuncCategory(function.getFuncCategory().getId(), function.getFuncCategory().getName());
                result.put(curCategory.getId(),curCategory);
            }

            curCategory.getFunctions().add(function);

        }


        authenticationFuncCategorys.addAll(result.values());
        Collections.sort(authenticationFuncCategorys, OrderBeanComparator.getInstance());
    }

    public void roleCategoryChanged(ValueChangeEvent event) {
        String roleCategoryId = (String) event.getNewValue();

        for (RoleCategory roleCategory : loginEmployee.getRoleCategorys()) {
            if (roleCategory.getId().equals(roleCategoryId)) {
                currRoleCategory = roleCategory;
                generateFuncCategorys();
            }
        }
    }

    public String getCurrRoleCategoryId() {
        if (currRoleCategory != null) {
            return currRoleCategory.getId();
        }
        return null;
    }

    public void setCurrRoleCategoryId(String roleCategoryId) {
        for (RoleCategory roleCategory : loginEmployee.getRoleCategorys()) {
            if (roleCategory.getId().equals(roleCategoryId)) {
                currRoleCategory = roleCategory;
            }
        }
    }

}
