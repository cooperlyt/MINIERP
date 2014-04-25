package com.dgsoft.common.jbpm;

import com.dgsoft.common.exception.ProcessDefineException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/11/14
 * Time: 10:12 PM
 */

public abstract class TaskInstanceListCache {

    protected abstract Set<TaskInstance> searchTaskInstances();

    private HashMap<TaskInstance,TaskDescription> taskInstances;

    private Set<Long> taskInstanceIds;

    private Set<Long> newTaskInstanceIds = new HashSet<Long>();

    private List<String> taskNoticeMsgs = new ArrayList<String>();

    @Create
    public void register() {
        ((BpmTaskChangePublish) Component.getInstance(BpmTaskChangePublish.class, ScopeType.APPLICATION, true)).subscribe(this);
    }

    @Destroy
    public void unRegister(){
        ((BpmTaskChangePublish) Component.getInstance(BpmTaskChangePublish.class, ScopeType.APPLICATION, true)).unSubscribe(this);
    }

    private Set<TaskInstance> getTaskInstances() {
        initTaskInstances();
        return taskInstances.keySet();
    }

    private Map<TaskInstance,TaskDescription> getTaskInstanceMap(){
        initTaskInstances();
        return taskInstances;
    }

    protected TaskDescription genTaskDescription(TaskInstance taskInstance){
        String taskJSONDescription = taskInstance.getDescription();
        if (taskJSONDescription == null || taskJSONDescription.trim().equals("")) {
            return new BaseTaskDescription();
        }
        try {

            return new OldBussOper().bindRelation(taskInstance,new BaseTaskDescription(new JSONObject(taskJSONDescription))) ;
        } catch (JSONException e) {
            Logging.getLog(this.getClass()).error("jbpm process Define error task Description JSON ERROR", e);
            throw new ProcessDefineException("jbpm process Define error task Description JSON ERROR");
        }
    }

    @Deprecated
    private static class OldBussOper implements InvocationHandler {

        private TaskInstance taskInstance;

        private TaskDescription description1;

        private TaskDescription bindRelation(TaskInstance taskInstance, TaskDescription description1) {
            this.taskInstance = taskInstance;
            this.description1 = description1;
            return (TaskDescription) Proxy.newProxyInstance(description1.getClass().getClassLoader(), description1.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getBuninessKey")) {
                String result = description1.getBuninessKey();
                if ((result == null) || "".equals(result)) {
                    return taskInstance.getProcessInstance().getKey();
                } else return result;
            }
            if (method.getName().equals("getBusinessName")) {
                String result = description1.getBusinessName();
                if ((result == null) || "".equals(result)) {
                    return taskInstance.getVariable("businessName");
                } else return result;
            }
            if (method.getName().equals("getDescription")) {
                String result = description1.getDescription();
                if ((result == null) || "".equals(result)) {
                    return taskInstance.getVariable("businessDescription");
                } else return result;
            }

            return method.invoke(description1, args);
        }
    }

    private void initTaskInstances() {
        if (taskInstances == null) {
            Logging.getLog(this.getClass()).debug("init getTaskInstances");

            //taskInstances = searchTaskInstances();

            taskInstances = new HashMap<TaskInstance, TaskDescription>();
            for (TaskInstance ti: searchTaskInstances()){
                taskInstances.put(ti, genTaskDescription(ti));
            }

            if (taskInstanceIds == null) {
                taskInstanceIds = new HashSet<Long>();
            } else {


                Set<Long> prepareIds = new HashSet<Long>();

                for (TaskInstance taskInstance : taskInstances.keySet()) {
                    if (!taskInstanceIds.contains(taskInstance.getId())) {
                        prepareIds.add(taskInstance.getId());

                        //TODO messages details
                        taskNoticeMsgs.add(taskInstance.getName());
                    }
                }

                if (!prepareIds.isEmpty()) {
                    newTaskInstanceIds.clear();
                    newTaskInstanceIds.addAll(prepareIds);

                }
            }

            taskInstanceIds.clear();
            for (TaskInstance taskInstance : taskInstances.keySet()) {
                taskInstanceIds.add(taskInstance.getId());
            }
        }
    }

    @BypassInterceptors
    public void refresh() {
        taskInstances = null;
    }

    public List<Map.Entry<TaskInstance,TaskDescription>> getTaskInstancePriorityEntryList() {

        List<Map.Entry<TaskInstance,TaskDescription>> result = new ArrayList<Map.Entry<TaskInstance, TaskDescription>>(getTaskInstanceMap().entrySet()) ;
        Collections.sort(result, new Comparator<Map.Entry<TaskInstance,TaskDescription>>() {
            @Override
            public int compare(Map.Entry<TaskInstance,TaskDescription> o1, Map.Entry<TaskInstance,TaskDescription> o2) {
                int cResult = new Integer(o1.getKey().getPriority()).compareTo(o2.getKey().getPriority());
                if (cResult == 0){
                    return o2.getKey().getCreate().compareTo(o1.getKey().getCreate());
                }else{
                    return cResult;
                }
            }
        });
        return result;
    }


    public List<TaskInstance> getTaskInstancePriorityList() {

        List<TaskInstance> result = new ArrayList<TaskInstance>(getTaskInstances());
        Collections.sort(result, new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                int cResult = new Integer(o1.getPriority()).compareTo(o2.getPriority());
                if (cResult == 0){
                    return o2.getCreate().compareTo(o1.getCreate());
                }else{
                    return cResult;
                }
            }
        });
        return result;
    }


    public List<Map.Entry<TaskInstance,TaskDescription>> getTaskInstanceCreateEntryList() {
        List<Map.Entry<TaskInstance,TaskDescription>> result = new ArrayList<Map.Entry<TaskInstance, TaskDescription>>(getTaskInstanceMap().entrySet()) ;

        Collections.sort(result, new Comparator<Map.Entry<TaskInstance,TaskDescription>>() {
            @Override
            public int compare(Map.Entry<TaskInstance,TaskDescription> o1, Map.Entry<TaskInstance,TaskDescription> o2) {
                return o2.getKey().getCreate().compareTo(o1.getKey().getCreate());
            }
        });
        return result;
    }

    public List<TaskInstance> getTaskInstanceCreateList() {
        List<TaskInstance> result = new ArrayList<TaskInstance>(getTaskInstances());
        Collections.sort(result, new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                return o2.getCreate().compareTo(o1.getCreate());
            }
        });
        return result;
    }

    @BypassInterceptors
    public boolean isNewTask(Long taskId) {
        return newTaskInstanceIds.contains(taskId);
    }

    public boolean isHaveNotice() {
        initTaskInstances();
        return !taskNoticeMsgs.isEmpty();
    }

    @BypassInterceptors
    public String getTaskNoticeMsg() {
        if (taskNoticeMsgs.isEmpty()) {
            return null;
        } else {
            return taskNoticeMsgs.remove(0);
        }
    }

}
