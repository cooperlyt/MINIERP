package com.dgsoft.common.system.business;

import com.dgsoft.common.EntityHomeAdapter;
import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.system.model.BusinessDefine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jbpm.graph.def.ProcessDefinition;

import javax.faces.event.ValueChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/23/13
 * Time: 2:32 PM
 */

@Scope(ScopeType.CONVERSATION)
public abstract class BusinessCreate<E extends BussinessInstance> extends EntityHomeAdapter<E>{

//    @Out(scope = ScopeType.BUSINESS_PROCESS)
//    private String businessDefineId;
//
//    @Out(scope = ScopeType.BUSINESS_PROCESS)
//    private String businessDescription;
//
//    @Out(scope = ScopeType.BUSINESS_PROCESS)
//    private String businessName;

    @In
    private FacesMessages facesMessages;

    @Logger
    private Log log;

    @In
    private Events events;

    //protected abstract String getBusinessKey();

    protected abstract String getWfName();

//    @In(create = true)
//    private TaskPrepare taskPrepare;


    public void verifyBusinessKeyAvailable(ValueChangeEvent event) {
        String key = (String) event.getNewValue();
        if (!verifyBusinessKey(key)) {
            facesMessages.addToControlFromResourceBundle(event.getComponent().getId(),
                    StatusMessage.Severity.ERROR, "businessKeyConflict");
        }
    }


    public boolean verifyBusinessKey(String key) {

        ProcessDefinition definition = ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinition(getWfName());
        return ManagedJbpmContext.instance().getProcessInstance(definition,
                key) == null;
    }


    @Override
    protected boolean wire() {
        if (!isManaged()) {
            return createWorkFlow();
        }else return true;
    }

    @Transactional
    protected boolean createWorkFlow() {


        BusinessProcess.instance().createProcess(getWfName(),getInstance().getId());

        getInstance().setWfProcessId(BusinessProcess.instance().getProcessId());
        getInstance().setWfVer(ManagedJbpmContext.instance().getProcessInstance(BusinessProcess.instance().getProcessId()).getVersion());


       // ManagedJbpmContext.instance().getSession().flush();
        return true;
        // return navigation(startData.getBusinessKey());

    }
//
//    private String navigation(String businessKey) {
//
//
//        ManagedJbpmContext.instance().getSession().flush();
//
//        if (runParam.getBooleanParamValue("system.business.forwordToTask")) {
//            //ownerTaskInstanceListener.refresh();
//            int findTask = 0;
//            TaskInstance findTaskInstance = null;
//            ownerTaskInstanceCacheList.refresh();
//            for (TaskInstance taskInstance : ownerTaskInstanceCacheList.getTaskInstanceCreateList()) {
//                if (taskInstance.getProcessInstance().getKey().equals(businessKey)) {
//                    findTask++;
//                    if (findTask > 1) {
//                        return "businessCreated";
//                    } else {
//                        findTaskInstance = taskInstance;
//                    }
//                }
//            }
//
//            if (findTaskInstance != null) {
//                BusinessProcess.instance().resumeTask(findTaskInstance.getId());
//                return taskPrepare.getTaskDescription(findTaskInstance.getId()).getTaskOperationPage();
//            }
//
//        }
//        return "businessCreated";
//    }


}
