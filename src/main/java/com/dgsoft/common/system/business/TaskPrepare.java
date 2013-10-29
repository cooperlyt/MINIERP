package com.dgsoft.common.system.business;

import com.dgsoft.common.exception.ProcessDefineException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 5:21 PM
 */
@Name("taskPrepare")
public class TaskPrepare {

    @In("#{param.taskId}")
    private String taskId;


    @BeginTask
    public String beginTask() {
        return getTaskDescription(Long.parseLong(taskId)).getTaskOperationPage();
    }

    @BypassInterceptors
    public TaskDescription getTaskDescription(long taskId){
        TaskInstance targetTaskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(taskId);
        if (targetTaskInstance != null){
            String taskJSONDescription = targetTaskInstance.getDescription();
            Logging.getLog(this.getClass()).debug("task Description json str:" + taskJSONDescription);
            try {
                return new TaskDescription(new JSONObject(taskJSONDescription));
            } catch (JSONException e) {
                Logging.getLog(this.getClass()).error("jbpm process Define error task Description JSON ERROR", e);
                throw new ProcessDefineException("jbpm process Define error task Description JSON ERROR");
            }
        } else{
            Logging.getLog(this.getClass()).warn("taskInstance not found.");
            return null;
        }
    }


}
