package com.dgsoft.common.system.business;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.transaction.Transaction;
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

    protected abstract String initTask();


    public String complete(){

        return completeTask();
    }


    @Transactional
    public String init(){

        return initTask();
    }

}
