package com.dgsoft.common.system.business;

import com.dgsoft.common.system.model.BusinessDefine;
import org.jboss.seam.ScopeType;
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

    private int level;

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
}
