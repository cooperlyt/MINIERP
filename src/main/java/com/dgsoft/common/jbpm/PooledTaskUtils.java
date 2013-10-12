package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.bpm.PooledTask;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/31/13
 * Time: 4:34 PM
 */

@Name("org.jboss.seam.bpm.pooledTask")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.APPLICATION, dependencies = "org.jboss.seam.bpm.jbpm")
public class PooledTaskUtils extends PooledTask {

    @In
    private FacesMessages facesMessages;

    @Transactional
    @Override
    @RaiseEvent("com.dgsot.jbpm.taskAssign")
    public String assignToCurrentActor() {
        String result = super.assignToCurrentActor();
        ManagedJbpmContext.instance().getSession().flush();
        if (result == null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "acceptBusinessFail");
        }
        return result;
    }

    @Transactional
    @Override
    @RaiseEvent("com.dgsot.jbpm.taskAssign")
    public String assign(String actorId) {
        String result = super.assign(actorId);
        ManagedJbpmContext.instance().getSession().flush();
        if (result == null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "acceptBusinessFail");
        }
        return result;
    }

    @Transactional
    @Override
    @RaiseEvent("com.dgsot.jbpm.taskUnassign")
    public String unassign() {
        String result = super.unassign();
        ManagedJbpmContext.instance().getSession().flush();
        if (result == null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "operationFail");
        }
        return result;
    }

}
