package com.dgsoft.common.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/11/14
 * Time: 10:12 PM
 */
public abstract class TaskInstanceListCache {

    protected abstract Set<TaskInstance> searchTaskInstances();

    private Set<TaskInstance> taskInstances;

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
        return taskInstances;
    }

    private void initTaskInstances() {
        if (taskInstances == null) {
            Logging.getLog(this.getClass()).debug("init getTaskInstances");

            taskInstances = searchTaskInstances();


            if (taskInstanceIds == null) {
                taskInstanceIds = new HashSet<Long>();
            } else {


                Set<Long> prepareIds = new HashSet<Long>();

                for (TaskInstance taskInstance : taskInstances) {
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
            for (TaskInstance taskInstance : taskInstances) {
                taskInstanceIds.add(taskInstance.getId());
            }
        }
    }

    public void refresh() {
        taskInstances = null;
    }


    public List<TaskInstance> getTaskInstancePriorityList() {

        List<TaskInstance> result = new ArrayList<TaskInstance>(getTaskInstances());
        Collections.sort(result, new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                return new Integer(o1.getPriority()).compareTo(o2.getPriority());
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

    public boolean isNewTask(Long taskId) {
        return newTaskInstanceIds.contains(taskId);
    }

    public boolean isHaveNotice() {
        initTaskInstances();
        return !taskNoticeMsgs.isEmpty();
    }

    public String getTaskNoticeMsg() {
        if (taskNoticeMsgs.isEmpty()) {
            return null;
        } else {
            return taskNoticeMsgs.remove(0);
        }
    }

}
