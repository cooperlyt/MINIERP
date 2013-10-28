package com.dgsoft.common.system.business;

import org.jboss.seam.log.Logging;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 5:24 PM
 */
public class TaskDescription {

    //{"description":"","operPage":"/func/erp/biz/custom/OrderPay.xhtml"}

    private static final String DEFAULT_TASK_OPERPAGE = "/func/system/business/taskOperate.xhtml";

    private JSONObject jsonObject;

    public TaskDescription() {

    }

    public TaskDescription(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getTaskOperationPage() {
        if (jsonObject == null){
            return  DEFAULT_TASK_OPERPAGE;
        }
        String result = null;
        try {
            result = jsonObject.getString("description");
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).warn("TaskDescription get task OperationPage fail",e);
        }
        if ((result == null) || result.trim().equals("")){
            result = DEFAULT_TASK_OPERPAGE;
        }
        return result;
    }

    public String getDescription(){
        if (jsonObject == null){
            return null;
        }
        try {
            return jsonObject.getString("description");
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).warn("TaskDescription get task Description fail",e);
            return "";
        }
    }

}
