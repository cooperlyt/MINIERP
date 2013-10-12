package com.dgsoft.common.jbpm;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.bpm.ResumeProcess;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

/**
 * Created by IntelliJ IDEA.
 * User: cooper
 * Date: 8/24/11
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("processInstanceImg")
@Install(precedence = 11, dependencies = "org.jboss.seam.bpm.jbpm")
public class ProcessInstanceImg  extends TokenImg{

    @In(create = true)
    private ProcessInstance processInstance;

    @Unwrap
    @ResumeProcess
    public byte[] getProcessInstanceImg(){
        return super.getImage();
    }

    @Override
    protected ProcessDefinition getProcessDefinition() {
        return processInstance.getProcessDefinition();
    }

    @Override
    protected Token getCurrentToken() {
        return processInstance.getRootToken();
    }

    @Override
    protected boolean drawChild() {
        return true;
    }
}
