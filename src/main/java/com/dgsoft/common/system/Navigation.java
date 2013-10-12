package com.dgsoft.common.system;

import com.dgsoft.common.system.model.FuncCategory;
import com.dgsoft.common.system.model.Function;
import com.dgsoft.common.system.model.RoleCategory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.security.AuthorizationException;

import javax.faces.event.ValueChangeEvent;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/9/13
 * Time: 3:48 PM
 */
@Name("navigation")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class Navigation {


    @Out(scope = ScopeType.SESSION)
    private Function currentFunction;

    @In
    private AuthenticationInfo authInfo;

    @RequestParameter
    private String functionId;

    @End
    public String navigationTo(){
        for (FuncCategory funcCategory: authInfo.getAuthenticationFuncCategorys()){
            for (Function function: funcCategory.getFunctions()){
                if (function.getId().equals(functionId)){
                    currentFunction = function;
                    return currentFunction.getLocation();
                }
            }
        }
        throw new AuthorizationException("function not found!");
    }

}
