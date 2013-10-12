package com.dgsoft.common;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/5/13
 * Time: 4:34 PM
 */
@Name("maxIntegerValue")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class MaxIntegerValue {

    @Unwrap
    public int getMaxIntegerValue(){
        return Integer.MAX_VALUE;
    }
}
