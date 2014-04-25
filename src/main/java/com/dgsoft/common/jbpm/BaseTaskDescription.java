package com.dgsoft.common.jbpm;

import org.jboss.seam.log.Logging;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 5:24 PM
 */
public class BaseTaskDescription implements TaskDescription {

    /*

    {"businessName":"销售订单","businessKey":"#{orderHome.instance.id}","tags":["#{orderHome.instance.customer.customerArea.name}"], "description":"#{orderHome.instance.customer.name}","operPage":"/business/taskOperator/erp/finance/ReceiveOrderEarnest.xhtml"}

     */

    private static final String DEFAULT_TASK_OPERPAGE = "/business/taskOperator/AutoTaskOperate.xhtml";

    private static final String BUNINESS_KEY = "businessKey";
    private static final String BUNINESS_NAME_KEY = "businessName";
    private static final String DESCRIPTION_NAME_KEY = "description";
    private static final String OPER_PAGE_KEY = "operPage";
    private static final String TASK_TAGS_KEY = "tags";


    private JSONObject jsonObject;

    public BaseTaskDescription() {

    }

    public BaseTaskDescription(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String getTaskOperationPage() {
        if (jsonObject == null) {
            return DEFAULT_TASK_OPERPAGE;
        }
        String result = null;
        try {
            result = jsonObject.getString(OPER_PAGE_KEY);
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).warn("BaseTaskDescription get task OperationPage fail", e);
        }
        if ((result == null) || result.trim().equals("")) {
            result = DEFAULT_TASK_OPERPAGE;
        }
        return result;
    }

    @Override
    public List<String> getTags() {
        if (jsonObject == null) {
            return null;
        }
        if (!jsonObject.has(TASK_TAGS_KEY)){
            return new ArrayList<String>(0);
        }

        List<String> result = new ArrayList<String>();

        try {
            JSONArray array = jsonObject.getJSONArray(TASK_TAGS_KEY);
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getBuninessKey() {
        return getStringValue(BUNINESS_KEY);
    }

    @Override
    public String getBusinessName() {
        return getStringValue(BUNINESS_NAME_KEY);
    }

    @Override
    public String getDescription() {
        return getStringValue(DESCRIPTION_NAME_KEY);
    }

    @Override
    public String getStringValue(String key) {
        if (jsonObject == null) {
            return null;
        }
        if (!jsonObject.has(key)) {
            return "";
        }

        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).warn("BaseTaskDescription get task Description fail", e);
            return null;
        }
    }

    @Override
    @Deprecated
    public String getValue(String key) {
        if (jsonObject == null) {
            return null;
        }
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).warn("BaseTaskDescription get task Description fail", e);
            return null;
        }
    }

}
