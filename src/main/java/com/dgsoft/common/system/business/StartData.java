package com.dgsoft.common.system.business;

import com.dgsoft.common.system.NumberBuilder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/23/13
 * Time: 1:49 PM
 */
@Name("startData")
@Scope(ScopeType.CONVERSATION)
public class StartData {

    @In
    private NumberBuilder numberBuilder;

    private int level;

    private String businessKey;

    private String description;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public void generateKey(){
        businessKey = numberBuilder.getDateNumber("businessKeyCode");
    }

    public void generateKey(String prefix){
        businessKey = prefix + numberBuilder.getDateNumber("businessKeyCode");
    }
}
