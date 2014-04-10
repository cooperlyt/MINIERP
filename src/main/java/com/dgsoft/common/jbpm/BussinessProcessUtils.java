package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.Collection;
import java.util.Iterator;

import static org.jboss.seam.annotations.Install.APPLICATION;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/6/13
 * Time: 8:39 AM
 */
@Scope(ScopeType.CONVERSATION)
@Name("org.jboss.seam.bpm.businessProcess")
@BypassInterceptors
@Install(dependencies = "org.jboss.seam.bpm.jbpm", precedence = APPLICATION)
public class BussinessProcessUtils extends BusinessProcess {

    @Override
    public void createProcess(String processDefinitionName, String businessKey) {
        super.createProcess(processDefinitionName, businessKey);
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.createProcess");
    }

    @Override
    public void createProcess(String processDefinitionName, boolean shouldSignalProcess) {
        super.createProcess(processDefinitionName, shouldSignalProcess);
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.createProcess");
    }


    @Override
    public void endTask(String transitionName) {
        super.endTask(transitionName);
        ManagedJbpmContext.instance().getSession().flush();
        Logging.getLog(getClass()).debug("call endTask:" + transitionName);
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.endTask");
    }




    public void suspendProcess(String processDefinitionName, String businessKey) {

        ProcessDefinition definition = ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinition(processDefinitionName);
        ProcessInstance processInstance = definition == null ?
                null : ManagedJbpmContext.instance().getProcessInstanceForUpdate(definition, businessKey);

        processInstance.suspend();

        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.processSuspended");
    }


}
