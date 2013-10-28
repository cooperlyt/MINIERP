package com.dgsoft.common.system.business;

import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.model.SimpleVarSubscribe;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/27/13
 * Time: 9:12 AM
 */
@Name("defaultTaskOperate")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DefaultTaskOperate {

    private List<String> fallBackTransitions;

    @Logger
    private Log log;

    @In
    private TaskInstance taskInstance;

    @In(scope = ScopeType.BUSINESS_PROCESS)
    private String businessDefineId;

    private BusinessOperator businessOperator;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    @Transactional
    public void save() {

    }

    @EndTask
    @Transactional
    public String complete() {


        return "success";
    }

    @Transactional
    public String fallback() {

        return "success";
    }

    @Transactional
    public void init() {
        fallBackTransitions = new ArrayList<String>();
        businessDefineHome.setId(businessDefineId);
        log.debug("run taskOperate init [" + "version:" + String.valueOf(taskInstance.getProcessInstance().getProcessDefinition().getVersion()) + ",taskName:" + taskInstance.getName() + "]");
        List<SimpleVarSubscribe> simpleVarSubscribes = businessDefineHome.getSimpleVarDefineList(String.valueOf(taskInstance.getProcessInstance().getProcessDefinition().getVersion()), taskInstance.getName());
        businessOperator = (BusinessOperator) Component.getInstance(businessDefineHome.getInstance().getTaskService(), true, true);
        Map<String, SimpleVarSubscribeStore> simpleVarStores = businessOperator.getSimpleVars(taskInstance.getProcessInstance().getKey());
        writeSimpleVarList = new ArrayList<SimpleVar>();
        Map<String, List<SimpleVar>> readSimpleVars = new HashMap<String, List<SimpleVar>>();
        for (SimpleVarSubscribe simpleVarSubscribe : simpleVarSubscribes) {
            SimpleVarSubscribeStore simpleVarSubscribeStore = simpleVarStores.get(simpleVarSubscribe.getId());

            if (simpleVarSubscribe.isReadonly()) {
                if (simpleVarSubscribeStore != null) {
                    List<SimpleVar> vars = readSimpleVars.get(simpleVarSubscribeStore.getCategory());
                    if (vars == null) {
                        vars = new ArrayList<SimpleVar>();
                        readSimpleVars.put(simpleVarSubscribeStore.getCategory(), vars);
                    }
                    vars.add(new SimpleVar(simpleVarSubscribe, simpleVarSubscribeStore.getValue()));
                }
            } else {
                if (simpleVarSubscribeStore != null)
                    writeSimpleVarList.add(new SimpleVar(simpleVarSubscribe, simpleVarSubscribeStore.getValue()));
                else
                    writeSimpleVarList.add(new SimpleVar(simpleVarSubscribe));
            }
        }

        Collections.sort(writeSimpleVarList);

        viewVariableCategory = new ArrayList<VariableCategory>();
        for (Map.Entry<String, List<SimpleVar>> entry : readSimpleVars.entrySet()) {
            List<SubscribeVar> vars = new ArrayList<SubscribeVar>(entry.getValue());
            Collections.sort(vars);
            viewVariableCategory.add(new VariableCategory(entry.getKey(), vars));
        }

    }

    private List<SimpleVar> writeSimpleVarList;

    private List<VariableCategory> viewVariableCategory;


    public List<SimpleVar> getWriteSimpleVarList() {
        return writeSimpleVarList;
    }

    public List<VariableCategory> getViewVariableCategory() {
        return viewVariableCategory;
    }

    //this func call maybe task not begin
    @BypassInterceptors
    public String operate() {


        return "success";
    }

    public boolean isCanFallback() {
        return !fallBackTransitions.isEmpty();
    }

    public class VariableCategory {
        private String category;

        private List<SubscribeVar> subscribers;

        public VariableCategory(String category, List<SubscribeVar> subscribers) {
            super();
            this.category = category;
            this.subscribers = subscribers;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setSubscribers(List<SubscribeVar> subscribers) {
            this.subscribers = subscribers;
        }
    }

}
