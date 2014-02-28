package com.dgsoft.common.system.business;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:56 AM
 */
@Scope(ScopeType.CONVERSATION)
public abstract class TaskHandle implements Serializable{

    @In
    protected TaskInstance taskInstance;

    @In
    protected FacesMessages facesMessages;

    protected abstract String completeTask();


    protected abstract void initTask();


    public String getTaskName(){
        return taskInstance.getName();
    }

    @Transactional
    public String complete(){

        return completeTask();
    }

    @Create
    @Transactional
    public void init(){
        Logging.getLog(getClass()).debug("task is init");
        initTask();
    }

}
