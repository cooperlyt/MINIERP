package com.dgsoft.common;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 8/19/13
 * Time: 1:22 PM
 */
@Name("builtInFilterBean")
@Scope(ScopeType.PAGE)
public class BuiltInFilterBean {

    private String codeFilter;

    private String nameFilter;

    public String getCodeFilter() {
        return codeFilter;
    }

    public void setCodeFilter(String codeFilter) {
        this.codeFilter = codeFilter;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public void reset(){
        codeFilter = "";
        nameFilter = "";
    }

}
