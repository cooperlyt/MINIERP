package com.dgsoft.common.system.business;

import org.jboss.seam.annotations.In;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:56 AM
 */
public abstract class TaskHandle implements Serializable{

    @In
    protected TaskInstance taskInstance;

    protected abstract String completeTask();

    protected abstract String initTask();

    public final String complete(){

        return completeTask();
    }


    public final String init(){

        return initTask();
    }

}
