package com.dgsoft.common.system.business;

import com.dgsoft.common.exception.ProcessDefineException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.log.Log;
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

    @Out(scope = ScopeType.CONVERSATION)
    private TaskDescription taskDescription;

    @In
    private TaskInstance taskInstance;

    @Logger
    private Log log;


    @BeginTask
    public String beginTask() {
        String taskJSONDescription = taskInstance.getDescription();
        if (taskJSONDescription == null || taskJSONDescription.trim().equals("")) {
            taskDescription = new TaskDescription();
        } else {

            try {
                JSONObject jsonObject = new JSONObject(taskJSONDescription);
                jsonObject.

            } catch (JSONException e) {
                log.error("jbpm process Define error task Description JSON ERROR", e);
                throw new ProcessDefineException("jbpm process Define error task Description JSON ERROR");
            }
        }

        return "";
    }
}
