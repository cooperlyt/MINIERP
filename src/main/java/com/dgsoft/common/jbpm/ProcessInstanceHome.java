package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 03/04/14
 * Time: 10:37
 */

@Name("processInstanceHome")
@Scope(ScopeType.CONVERSATION)
public class ProcessInstanceHome {

    //private List<ProcessInstance>

    private ProcessInstance instance;

    private String processDefineName;

    private String processKey;

    //private Long processId;


    public ProcessInstance getInstance() {
        initInstance();
        return instance;
    }


    public String getProcessDefineName() {
        return processDefineName;
    }

    public void setProcessDefineName(String processDefineName) {
        if ((processDefineName == null) || !processDefineName.equals(this.processDefineName)) {
            instance = null;
        }
        this.processDefineName = processDefineName;

    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        if ((processKey == null) || (!processKey.equals(this.processKey))) {
            instance = null;
        }
        this.processKey = processKey;
    }

    @Deprecated
    public void initInstance() {
        //TODO save processId and ver to db;
        if (instance == null) {
            if ((processDefineName != null) && (processKey != null)) {
                Logging.getLog(this.getClass()).debug("Locate processInstance - processDefineName:" + processDefineName + ";processKey:" + processKey);

                List<ProcessDefinition> definitions = ManagedJbpmContext.instance().getGraphSession().findAllProcessDefinitionVersions(processDefineName);

                for (ProcessDefinition definition: definitions){
                    instance = ManagedJbpmContext.instance().getProcessInstanceForUpdate(definition, processKey);
                    if (instance != null){
                        break;
                    }
                }

            }
        }
    }

    public boolean signalState(){
        if ("State".equals(getInstance().getRootToken().getNode().getNodeType().toString())){
            getInstance().signal();
            Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.endTask");
            return true;
        }else{
            return false;
        }

    }


    public void stop() {

        Collection listTasks = getInstance().getTaskMgmtInstance().getTaskInstances();
        if (listTasks.size() > 0) {
            for (Iterator iter = listTasks.iterator(); iter.hasNext(); ) {
                TaskInstance ti = (TaskInstance) iter.next();
                if (!ti.hasEnded() && !ti.isSuspended()) {

                    ti.setSignalling(false);
                    ti.cancel();
                    ti.setEnd(new java.util.Date());


                    Logging.getLog(getClass()).debug("task instance " + ti.getName() + " has ended");
                    Token tk = ti.getToken();
                    tk.end();
                    Logging.getLog(getClass()).debug("token " + tk.getName() + " has ended");
                }
            }
        }
        if (!getInstance().hasEnded()) {
            getInstance().end();

        }

        Events.instance().raiseEvent("org.jboss.seam.stopProcess", getInstance());

        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.processStoped");
    }

    public void suspend() {
        getInstance().suspend();
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.processSuspended");
    }

    public void resume() {

        getInstance().resume();
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.processResumed");
    }


    public List<TaskInstance> getTaskInstanceList() {

        List<TaskInstance> result = new ArrayList<TaskInstance>(getInstance().getTaskMgmtInstance().getTaskInstances());
        Collections.sort(result,new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                return o1.getCreate().compareTo(o2.getCreate());
            }
        });
        return result;
    }




}
