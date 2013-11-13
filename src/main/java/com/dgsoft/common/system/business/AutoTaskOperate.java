package com.dgsoft.common.system.business;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/13/13
 * Time: 1:35 PM
 */
@Name("autoTaskOperate")
@Scope(ScopeType.CONVERSATION)
public class AutoTaskOperate extends TaskHandle {

    @Override
    protected String completeTask() {
        return "taskComplete";
    }

    @Override
    protected String initTask() {
        return "";
    }
}
