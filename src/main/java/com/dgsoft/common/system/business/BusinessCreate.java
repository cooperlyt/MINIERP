package com.dgsoft.common.system.business;

import com.dgsoft.common.jbpm.OwnerTaskInstanceListener;
import com.dgsoft.common.system.NumberBuilder;
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
    private NumberBuilder numberBuilder;

    @In
    private OwnerTaskInstanceListener ownerTaskInstanceListener;

    @In
    private Events events;

    @In
    private StartData startData;


    private String ownerTaskName;

    @BypassInterceptors
    public String getOwnerTaskName() {
        return ownerTaskName;
    }

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
            return businessDefineHome.getInstance().getStartPage();
        } else {

            log.debug("define Id:" + businessDefineHome.getInstance().getId());
            String businessKey = numberBuilder.getDateNumber("businessKeyCode");
            businessInstanceHome.clearInstance();
            businessInstanceHome.getInstance().setId(businessKey);
            businessInstanceHome.getInstance().setBusinessDefine(businessDefineHome.getInstance());
            businessInstanceHome.getInstance().setMark(startData.getDescription());
            businessInstanceHome.getInstance().setProcessMessages(verifyMsg);

            BusinessProcess.instance().createProcess(businessDefineHome.getInstance().getWfName(),
                    businessKey);

            businessDefineId = businessDefineHome.getInstance().getId();
            businessDescription = startData.getDescription();
            businessName = businessDefineHome.getInstance().getName();

            //startService.createBusiness(businessInstanceHome.getInstance());
            businessInstanceHome.persist();
            events.raiseEvent("com.dgsoft.BusinessCreated", businessInstanceHome.getInstance());
            log.debug(businessKey + "verfy ok is start!");
            return navigation(businessKey);
        }
    }

    private TaskInstance operateTask;

    private String navigation(String businessKey) {
        ownerTaskName = "";
        ManagedJbpmContext.instance().getSession().flush();

        ownerTaskInstanceListener.refresh();
        for (TaskInstance taskInstance : ownerTaskInstanceListener.getTaskInstanceCreateList()) {
            if (taskInstance.getProcessInstance().getKey().equals(businessKey)) {
                operateTask = taskInstance;
                ownerTaskName = taskInstance.getName();
                return "success_oper";
            }
        }
        return "success";
    }

    @BypassInterceptors
    public TaskInstance getOperateTask() {
        return operateTask;
    }

    private StartDataValidator getDataValidator() {
        String startServiceName = businessDefineHome.getInstance().getStartDataValidator();
        if (startServiceName == null || startServiceName.trim().equals("")) {
            return null;
        } else
            return (StartDataValidator) Component.getInstance(startServiceName, true, true);
    }


}
