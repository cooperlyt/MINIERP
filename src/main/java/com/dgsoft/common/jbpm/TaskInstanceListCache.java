package com.dgsoft.common.jbpm;

import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/11/14
 * Time: 10:12 PM
 */
public abstract class TaskInstanceListCache {

    protected abstract Set<TaskInstance> getCurrTaskInstances();

    private Set<Long> taskInstanceIds;

    private Set<Long> newTaskInstanceIds = new HashSet<Long>();

    private List<String> taskNoticeMsgs = new ArrayList<String>();

    private Set<TaskInstance> getTaskInstances() {

        Set<TaskInstance> result = getCurrTaskInstances();


        if (taskInstanceIds == null){
            taskInstanceIds = new HashSet<Long>();
        }else{


            Set<Long> prepareIds = new HashSet<Long>();

            for (TaskInstance taskInstance: result){
                if (!taskInstanceIds.contains(taskInstance.getId())){
                    prepareIds.add(taskInstance.getId());

                    //TODO messages details
                    taskNoticeMsgs.add(taskInstance.getName());
                }
            }

            if (!prepareIds.isEmpty()){
                newTaskInstanceIds.clear();
                newTaskInstanceIds.addAll(prepareIds);

            }
        }

        taskInstanceIds.clear();
        for (TaskInstance taskInstance: result){
            taskInstanceIds.add(taskInstance.getId());
        }

        return result;
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

    public boolean isNewTask(Long taskId){
        return newTaskInstanceIds.contains(taskId);
    }

    public boolean isHaveNotice(){
        getTaskInstances();
        return !taskNoticeMsgs.isEmpty();
    }

    public String getTaskNoticeMsg(){
        if (taskNoticeMsgs.isEmpty()){
            return null;
        }else{
            return taskNoticeMsgs.remove(0);
        }
    }

}
