package com.dgsoft.common.system;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.model.*;

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

    private Role currRole;

    private List<Role> functionRoleList;

    private Employee loginEmployee;

    public Employee getLoginEmployee() {
        return loginEmployee;
    }

    public void setLoginEmployee(Employee loginEmployee) {
        this.loginEmployee = loginEmployee;
    }

    public Role getCurrRole() {
        return currRole;
    }

    public void setCurrRole(Role currRole) {
        this.currRole = currRole;
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

        Collection<Function> showFunctions = new HashSet<Function>();

        if (currRole == null) {
            for (Role role: functionRoleList){
                showFunctions.addAll(role.getFunctions());
            }
        } else {
            showFunctions = currRole.getFunctions();
        }

        for (Function function : showFunctions) {

            FuncCategory curCategory = result.get(function.getFuncCategory().getId());

            if (curCategory == null) {
                curCategory = new FuncCategory(function.getFuncCategory().getId(), function.getFuncCategory().getName());
                result.put(curCategory.getId(), curCategory);
            }

            curCategory.getFunctions().add(function);

        }


        authenticationFuncCategorys.addAll(result.values());
        Collections.sort(authenticationFuncCategorys, OrderBeanComparator.getInstance());
    }

    public List<Role> getFunctionRoleList() {
        return functionRoleList;
    }

    public void setFunctionRoleList(List<Role> functionRoleList) {
        this.functionRoleList = functionRoleList;
    }

    public String getCurrRoleCategoryId() {
        if (currRole != null) {
            return currRole.getId();
        }
        return null;
    }

    public void setCurrRoleCategoryId(String roleCategoryId) {
        for (Role role : functionRoleList) {
            if (role.getId().equals(roleCategoryId)) {
                currRole = role;
                return;
            }
        }
        currRole = null;
    }

}
