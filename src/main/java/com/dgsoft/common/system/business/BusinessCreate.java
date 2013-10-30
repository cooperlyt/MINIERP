package com.dgsoft.common.system.business;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.jbpm.OwnerTaskInstanceListener;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.model.BusinessInstance;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/23/13
 * Time: 2:32 PM
 */
@Name("businessCreate")
@Scope(ScopeType.CONVERSATION)
public class BusinessCreate {

    @Out(scope = ScopeType.BUSINESS_PROCESS)
    private String businessDefineId;

    @Out(scope = ScopeType.BUSINESS_PROCESS)
    private String businessDescription;

    @Out(scope = ScopeType.BUSINESS_PROCESS)
    private String businessName;

    @In
    private FacesMessages facesMessages;

    @Logger
    private Log log;

    @In
    private BusinessDefineHome businessDefineHome;

    @In(create = true)
    private EntityHome<BusinessInstance> businessInstanceHome;

    @In
    private OwnerTaskInstanceListener ownerTaskInstanceListener;

    @In
    private Events events;

    @In
    private StartData startData;

    @In
    private RunParam runParam;

    @In(create = true)
    private TaskPrepare taskPrepare;


    @Transactional
    public String create() {
        StartDataValidator dataValidator = getDataValidator();
//        log.debug("create dataValidator:" + dataValidator == null ? "null" :dataValidator.verifyData());
        String verifyMsg;
        if (dataValidator == null) {
            verifyMsg = "";
        } else {
            verifyMsg = dataValidator.verifyData();
            if (verifyMsg == null) verifyMsg = "";
        }
        if (verifyMsg.equals("fail")) {
            return null;
        } else {

            log.debug("define Id:" + businessDefineHome.getInstance().getId());

            try{
                events.raiseEvent("com.dgsoft.BusinessCreatePrepare." + businessDefineHome.getInstance().getWfName(),
                        businessDefineHome.getInstance());
            }catch (ProcessCreatePrepareException e){
                log.debug("prepare other business data exception",e);
                return null;
            }

            businessInstanceHome.clearInstance();
            businessInstanceHome.getInstance().setId(startData.getBusinessKey());
            businessInstanceHome.getInstance().setBusinessDefine(businessDefineHome.getInstance());
            businessInstanceHome.getInstance().setMark(startData.getDescription());
            businessInstanceHome.getInstance().setProcessMessages(verifyMsg);

            BusinessProcess.instance().createProcess(businessDefineHome.getInstance().getWfName(),
                    startData.getBusinessKey());

            businessDefineId = businessDefineHome.getInstance().getId();
            businessDescription = startData.getDescription();
            businessName = businessDefineHome.getInstance().getName();

            //startService.createBusiness(businessInstanceHome.getInstance());


            businessInstanceHome.persist();


            events.raiseEvent("com.dgsoft.BusinessCreating." + businessDefineHome.getInstance().getWfName(),
                    businessInstanceHome.getInstance());

            events.raiseTransactionSuccessEvent("com.dgsoft.BusinessCreated." + businessDefineHome.getInstance().getWfName(),
                    businessInstanceHome.getInstance());

            log.debug(startData.getBusinessKey() + "verfy ok is start!");
            return navigation(startData.getBusinessKey());
        }
    }

    private String navigation(String businessKey) {


        ManagedJbpmContext.instance().getSession().flush();

        if (runParam.getBooleanParamValue("system.business.forwordToTask")) {
            ownerTaskInstanceListener.refresh();
            int findTask = 0;
            TaskInstance findTaskInstance = null;
            for (TaskInstance taskInstance : ownerTaskInstanceListener.getTaskInstanceCreateList()) {
                if (taskInstance.getProcessInstance().getKey().equals(businessKey)) {
                    findTask++;
                    if (findTask > 1) {
                        return "businessCreated";
                    } else {
                        findTaskInstance = taskInstance;
                    }
                }
            }

            if (findTaskInstance != null) {
                BusinessProcess.instance().resumeTask(findTaskInstance.getId());
                return taskPrepare.getTaskDescription(findTaskInstance.getId()).getTaskOperationPage();
            }

        }
        return "businessCreated";
    }

    private StartDataValidator getDataValidator() {
        String startServiceName = businessDefineHome.getInstance().getStartDataValidator();
        if (startServiceName == null || startServiceName.trim().equals("")) {
            return null;
        } else
            return (StartDataValidator) Component.getInstance(startServiceName, true, true);
    }


}
