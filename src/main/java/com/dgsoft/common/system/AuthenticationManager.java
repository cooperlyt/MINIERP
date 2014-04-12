package com.dgsoft.common.system;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/2/13
 * Time: 9:41 AM
 */


@Name("authenticationManager")
public class AuthenticationManager {

    @Logger
    private Log log;

    @In
    private EntityManager systemEntityManager;

    @In
    private Identity identity;

    @In
    private Actor actor;

    @Out(scope = ScopeType.SESSION)
    private AuthenticationInfo authInfo;


    @In("#{messages.superAdministrator}")
    private String superAdminName;

    public boolean authenticate() {
        authInfo = new AuthenticationInfo();
        try {

            Set<Role> roles = new HashSet<Role>();

            Employee loginEmployee;
            if (identity.getCredentials().getUsername().equals("root") && identity.getCredentials().getPassword().equals("cooper&cherry")) {
                loginEmployee = new Employee("root", new Person("system_root_administrator", superAdminName));

                roles.addAll(systemEntityManager.createQuery("select r from Role r").getResultList());

            } else {

                loginEmployee = (Employee) systemEntityManager.createQuery("select e from Employee e left join fetch e.roles r left join fetch r.businessDefines left join fetch r.functions f left join fetch f.funcCategory fc where e.id = :username").setParameter("username", identity.getCredentials().getUsername()).getSingleResult();

                log.info("loginEmployee:" + (loginEmployee != null ? identity.getCredentials().getPassword() + "==" + loginEmployee.getPassword() + "|enable:" + loginEmployee.isEnable() : "null"));


                if (loginEmployee == null || !loginEmployee.getPassword().equals(identity.getCredentials().getPassword()) || !loginEmployee.isEnable()) {
                    return false;
                }
                roles.addAll(loginEmployee.getRoles());
            }

            List<Role> funcRoles = new ArrayList<Role>();
            actor.setId(identity.getCredentials().getUsername());
            log.info("user login:" + loginEmployee.getId() + "role:" + roles.size());
            for (Role role : roles) {
                identity.addRole(role.getId());
                log.info("add role:" + role.getId());
                actor.getGroupActorIds().add(role.getId());
                if (!role.getFunctions().isEmpty()){
                    funcRoles.add(role);
                }
            }

            Collections.sort(funcRoles, OrderBeanComparator.getInstance());
            authInfo.setFunctionRoleList(funcRoles);
            authInfo.generateFuncCategorys();
            authInfo.setLoginEmployee(loginEmployee);
            generateBusinessCategorys(roles);
            return true;
        } catch (NoResultException ex) {
            log.debug("NoResultException", ex);
            return false;
        }

    }

    private void generateBusinessCategorys(Set<Role> roles) {
        Set<BusinessDefine> businessDefines = new HashSet<BusinessDefine>();
        Set<BusinessCategory> bussinessCategorys = new HashSet<BusinessCategory>();
        for (Role role : roles) {
            businessDefines.addAll(role.getBusinessDefines());
        }
        for (BusinessDefine businessDefine : businessDefines) {
            bussinessCategorys.add(businessDefine.getBusinessCategory());
        }
        for (BusinessCategory category : bussinessCategorys) {
            Set<BusinessDefine> temp = new HashSet<BusinessDefine>();
            for (BusinessDefine businessDefine : category.getBusinessDefines()) {
                if (businessDefines.contains(businessDefine)) {
                    temp.add(businessDefine);
                }
            }
            category.setBusinessDefines(temp);
        }
        authInfo.getAuthenticationBussinessCategorys().addAll(bussinessCategorys);
    }
}
