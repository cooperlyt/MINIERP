package com.dgsoft.common.system.business;

import com.dgsoft.common.exception.ProcessDefineException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import org.jboss.seam.log.Logging;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/28/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */

@Scope(ScopeType.STATELESS)
@Name("taskDescription")
@BypassInterceptors
@AutoCreate
public class CurrTaskDescription {

    @Unwrap
    public TaskDescription getTaskDescription() throws Exception {
        String taskJSONDescription = org.jboss.seam.bpm.TaskInstance.instance().getDescription();
        if (taskJSONDescription == null || taskJSONDescription.trim().equals("")) {
            return new TaskDescription();
        }
        try {
            return new TaskDescription(new JSONObject(taskJSONDescription));
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).error("jbpm process Define error task Description JSON ERROR", e);
            throw new ProcessDefineException("jbpm process Define error task Description JSON ERROR");
        }
    }
}
