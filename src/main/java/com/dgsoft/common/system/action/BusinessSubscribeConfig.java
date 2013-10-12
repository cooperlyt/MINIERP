package com.dgsoft.common.system.action;

import com.dgsoft.common.system.model.BusinessDefine;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jbpm.graph.def.ProcessDefinition;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/27/13
 * Time: 2:05 PM
 */
public abstract class BusinessSubscribeConfig {

    protected String selectedVersion;

    protected String selectedTask;

    public String getSelectedVersion() {
        return selectedVersion;
    }

    public void setSelectedVersion(String selectedVersion) {
        this.selectedVersion = selectedVersion;
    }

    public String getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(String selectedTask) {
        this.selectedTask = selectedTask;
    }

    public List<String> getVersionList() {
        List<String> result = new ArrayList<String>();
        if (getInstance().getWfName() == null && getInstance().getWfName().trim().equals("")) {
            getFacesMessages().add(StatusMessage.Severity.WARN, "not type in wfName");
        } else {
            for (Object pd : ManagedJbpmContext.instance().getGraphSession().findAllProcessDefinitionVersions(getInstance().getWfName())) {
                result.add(String.valueOf(((ProcessDefinition) pd).getVersion()));
            }
        }
        return result;
    }

    private List<String> defineTaskList;

    public List<String> getDefineTaskList() {
        return defineTaskList;
    }

    public void selectedVersionChange(ValueChangeEvent e) {
        if (getInstance().getWfName() == null && getInstance().getWfName().trim().equals("")) {
            getFacesMessages().add(StatusMessage.Severity.WARN, "not type in wfName");
            selectedTask = "";
            defineTaskList = new ArrayList<String>(0);
        } else {
            selectedVersion = (String) e.getNewValue();
            defineTaskList = new ArrayList<String>(ManagedJbpmContext.instance().getGraphSession().findProcessDefinition(getInstance().getWfName(), Integer.parseInt(selectedVersion)).getTaskMgmtDefinition().getTasks().keySet());
            if (defineTaskList.isEmpty()) {
                selectedTask = "";
            } else {
                selectedTask = defineTaskList.get(0);
                selectVersionChanged();
            }
        }
    }

    protected EntityManager getEntityManager() {
        return getBusinessDefineHome().getEntityManager();
    }

    protected BusinessDefine getInstance() {
        return getBusinessDefineHome().getInstance();
    }

    protected abstract void selectVersionChanged();

    protected abstract BusinessDefineHome getBusinessDefineHome();

    protected abstract FacesMessages getFacesMessages();
}
