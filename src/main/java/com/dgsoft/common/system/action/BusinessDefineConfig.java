package com.dgsoft.common.system.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/25/13
 * Time: 1:28 PM
 */

@Name("businessDefineConfig")
public class BusinessDefineConfig {

    @RequestParameter
    private String businessDefineId;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    @Begin(pageflow = "business-config" ,flushMode = FlushModeType.MANUAL)
    public void editBusinessDefine(){
        businessDefineHome.setId(businessDefineId);
    }

    @Begin(pageflow = "business-config",flushMode = FlushModeType.MANUAL)
    public void addBusinessDefine(){
        businessDefineHome.clearInstance();
    }


    @Factory(value = "jpdlNameList", scope = ScopeType.CONVERSATION)
    public List<String> getJpdlNameList() {
        List<String> result = new ArrayList<String>();
        for (Object pd : ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinitions()) {
            result.add(String.valueOf(((ProcessDefinition) pd).getName()));
        }
        return result;
    }

}
