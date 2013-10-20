package com.dgsoft.common.helper;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/19/13
 * Time: 5:37 PM
 */
@Name("actionExecuteState")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ActionExecuteState {

    private String lastState = "";

    public String getLastState() {
        return lastState;
    }

    public void setLastState(String lastState) {
        this.lastState = lastState;
    }

    public void clearState(){
        lastState = "";
    }

    public void setState(String state){
        this.lastState = state;
    }

    public void actionExecute(){
        lastState = "success";
    }

}
