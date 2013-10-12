package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityQuery;
import com.dgsoft.common.system.model.SimpleVarDefine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/18/13
 * Time: 11:28 AM
 */
@Name("simpleVarList")
@Scope(ScopeType.CONVERSATION)
public class SimpleVarList extends SystemEntityQuery<SimpleVarDefine> {

    private static final String EJBQL = "select var from SimpleVarDefine var";

    private static final String[] RESTRICTIONS = {
            "lower(var.id) like lower(concat(#{simpleVarList.simpleVarDefine.id},'%'))",
            "lower(var.title) like lower(concat(#{simpleVarList.simpleVarDefine.title},'%'))",};

    public SimpleVarList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setMaxResults(25);
    }

    private SimpleVarDefine simpleVarDefine = new SimpleVarDefine();

    public SimpleVarDefine getSimpleVarDefine() {
        return simpleVarDefine;
    }
}
