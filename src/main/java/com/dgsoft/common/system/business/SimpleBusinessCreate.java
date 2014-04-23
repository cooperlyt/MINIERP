package com.dgsoft.common.system.business;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.system.action.BusinessDefineHome;
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
 * Date: 12/04/14
 * Time: 14:46
 */
@Name("businessCreate")
@Scope(ScopeType.CONVERSATION)
public class SimpleBusinessCreate {

    @In
    private BusinessDefineHome businessDefineHome;

    private String businessKey;

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }



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

//    @In(create = true)
//    private TaskPrepare taskPrepare;

    @Transactional
    public String create() {


        log.debug("define Id:" + businessDefineHome.getInstance().getId());

        try {
            events.raiseEvent("com.dgsoft.BusinessCreatePrepare." + businessDefineHome.getInstance().getId());
        } catch (ProcessCreatePrepareException e) {
            log.debug("prepare other business data exception", e);
            return "prepare_fail";
        }

        BusinessProcess.instance().createProcess(businessDefineHome.getInstance().getWfName(),getBusinessKey());


        events.raiseEvent("com.dgsoft.BusinessCreating." + businessDefineHome.getInstance().getId());

        events.raiseTransactionSuccessEvent("com.dgsoft.BusinessCreated." + businessDefineHome.getInstance().getId());

        log.debug(getBusinessKey() + "verfy ok is start!");

        ManagedJbpmContext.instance().getSession().flush();
        return "businessCreated";
        // return navigation(startData.getBusinessKey());

    }
}
