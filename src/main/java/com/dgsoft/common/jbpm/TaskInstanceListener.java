package com.dgsoft.common.jbpm;


import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.log.Log;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/31/13
 * Time: 3:57 PM
 */


public abstract class TaskInstanceListener {

    private List<TaskInstance> taskInstanceList;

    protected abstract List<TaskInstance> queryTaskList();

    protected List<Long> newTaskIds = new ArrayList<Long>();

    @Logger
    protected Log log;

    @Create
    public void init() {
        ((BpmTaskChangePublish) Component.getInstance("bpmTaskChangePublish", ScopeType.APPLICATION, true, true)).subscribe(this);
        refresh();
    }

    @Destroy
    public void destroy() {
        ((BpmTaskChangePublish) Component.getInstance("bpmTaskChangePublish", ScopeType.APPLICATION, true, true)).unSubscribe(this);
    }

    public void refresh() {
        newTaskIds.clear();
        taskInstanceList = queryTaskList();
    }

    private boolean compareTaskInstances(List<TaskInstance> newTasks, List<TaskInstance> tasks) {

        boolean result = false;
        if (newTasks.size() != tasks.size()) {
            result = true;
        }

        if (!result)
            for (TaskInstance task : tasks) {
                boolean found = false;
                for (TaskInstance newTask : newTasks) {
                    if (newTask.getId() == task.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    result = true;
                    break;
                }
            }

        if (result) {
            newTaskIds.clear();
            for (TaskInstance newTask : newTasks) {
                boolean found = false;
                for (TaskInstance task : tasks) {
                    if (newTask.getId() == task.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newTaskIds.add(new Long(newTask.getId()));
                }
            }
        }
        return result;
    }

    private void compareTaskInstances() {

        List<TaskInstance> newTasks = queryTaskList();
        boolean result = compareTaskInstances(newTasks, taskInstanceList);
        if (result) {
            taskInstanceList = newTasks;
            sendTaskChangeMessage();
            if (!newTaskIds.isEmpty()) {
                sendNewTaskMessage();
            }
        }

    }

    protected abstract void sendNewTaskMessage();

    protected abstract void  sendTaskChangeMessage();

    public boolean isNewTask(Long taskId) {
        return newTaskIds.contains(taskId);
    }

    public boolean isHaveNewTask(){
        return !newTaskIds.isEmpty();
    }

    public void checkTask() {
        log.debug("-------check Task:" + this);
        compareTaskInstances();
    }

    public List<TaskInstance> getTaskInstancePriorityList() {

        List<TaskInstance> result = new ArrayList<TaskInstance>(taskInstanceList);
        Collections.sort(result, new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                return new Integer(o1.getPriority()).compareTo(o2.getPriority());
            }
        });
        return result;
    }


    public List<TaskInstance> getTaskInstanceCreateList() {
        List<TaskInstance> result = new ArrayList<TaskInstance>(taskInstanceList);
        Collections.sort(result, new Comparator<TaskInstance>() {
            @Override
            public int compare(TaskInstance o1, TaskInstance o2) {
                return o2.getCreate().compareTo(o1.getCreate());
            }
        });
        return result;
    }


}
