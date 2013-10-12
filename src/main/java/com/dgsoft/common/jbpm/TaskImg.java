package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import static org.jboss.seam.annotations.Install.BUILT_IN;

/**
 * Created by IntelliJ IDEA.
 * User: cooper
 * Date: 8/24/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("taskImg")
@Install(precedence = 11, dependencies = "org.jboss.seam.bpm.jbpm")
public class TaskImg extends TokenImg {

    @In(create=true)
    TaskInstance taskInstance;

    @Unwrap
    @BeginTask(flushMode = FlushModeType.MANUAL)
    public byte[] getTaskProcessImg() {
        return super.getImage();
    }

    //@In
    // JbpmContext jbpmContext;

    @Override
    protected ProcessDefinition getProcessDefinition() {
        return taskInstance.getProcessInstance().getProcessDefinition();
    }

    @Override
    protected Token getCurrentToken() {
        return taskInstance.getToken();
    }

    @Override
    protected boolean drawChild() {
        return false;
    }
}
