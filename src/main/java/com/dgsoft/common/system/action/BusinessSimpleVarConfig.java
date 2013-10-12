package com.dgsoft.common.system.action;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.model.SimpleVarDefine;
import com.dgsoft.common.system.model.SimpleVarSubscribe;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jbpm.graph.def.ProcessDefinition;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/27/13
 * Time: 1:55 PM
 */

@Name("businessSimpleVarConfig")
@Scope(ScopeType.CONVERSATION)
public class BusinessSimpleVarConfig extends BusinessSubscribeConfig {

    @Logger
    private Log log;

    private BusinessDefineHome businessDefineHome;

    @Override
    public BusinessDefineHome getBusinessDefineHome() {
        return businessDefineHome;
    }

    private FacesMessages facesMessages;

    @In
    public void setFacesMessages(FacesMessages facesMessages) {
        this.facesMessages = facesMessages;
    }

    @Override
    protected FacesMessages getFacesMessages() {
        return facesMessages;
    }

    @In(create = true)
    public void setBusinessDefineHome(BusinessDefineHome businessDefineHome) {
        this.businessDefineHome = businessDefineHome;
    }

    @DataModel(scope = ScopeType.PAGE)
    private List<SimpleVarSubscribe> selectedSimpleVarList = new ArrayList<SimpleVarSubscribe>();

    public void taskSimpleVarChange() {
        log.debug("taskSimpleVarChange:" + selectedTask);
        selectedSimpleVarList.clear();
        selectedSimpleVarList.addAll(businessDefineHome.getSimpleVarDefineList(selectedVersion, selectedTask));
    }

    @Transactional
    public void addSimpleVarSubscribe() {
        if (newSimpleVarSubscribe != null) {
            getInstance().getSimpleVarSubscribes().add(newSimpleVarSubscribe);
            newSimpleVarSubscribe = null;
            taskSimpleVarChange();
            simpleVarState = "added";
        }
    }

    @In(required = false)
    private SimpleVarList simpleVarList;

    public void addSimpleVar() {
        SimpleVarDefine simpleVarDefine = simpleVarList.getDataModelSelection();
        if (simpleVarIsSubscribed(simpleVarDefine.getId())) return;
        createSimpleVarInstance();
        newSimpleVarSubscribe.setSimpleVarDefine(simpleVarDefine);
    }

    public void setNewSimpleVarId(String id) {
        if (simpleVarIsSubscribed(id)) return;
        if (id == null || id.equals("")) {
            if (getEntityManager().contains(newSimpleVarSubscribe.getSimpleVarDefine())) {
                newSimpleVarSubscribe.setSimpleVarDefine(new SimpleVarDefine());
            } else {
                newSimpleVarSubscribe.getSimpleVarDefine().setId(id);
            }
        } else {
            SimpleVarDefine simpleVarDefine = getSimpleVarById(id);
            if (simpleVarDefine != null) {
                newSimpleVarSubscribe.setSimpleVarDefine(simpleVarDefine);
            } else if (getEntityManager().contains(newSimpleVarSubscribe.getSimpleVarDefine())) {
                newSimpleVarSubscribe.setSimpleVarDefine(new SimpleVarDefine());
            } else {
                newSimpleVarSubscribe.getSimpleVarDefine().setId(id);
            }
        }
    }

    private boolean simpleVarIsSubscribed(String id) {
        for (SimpleVarSubscribe simpleVarSubscribe : getInstance().getSimpleVarSubscribes()) {
            if (simpleVarSubscribe.getSimpleVarDefine().getId().equals(id) &&
                    simpleVarSubscribe.getWfTask().equals(selectedTask) && simpleVarSubscribe.getWfVer().equals(selectedVersion)) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "subscribe_var_is_exists", id);
                return true;
            }
        }
        return false;
    }

    public String getNewSimpleVarId() {
        return newSimpleVarSubscribe.getSimpleVarDefine().getId();
    }

    public boolean isCanChangeSimpleVar() {
        return newSimpleVarSubscribe.getSimpleVarDefine().getId() != null &&
                !newSimpleVarSubscribe.getSimpleVarDefine().getId().trim().equals("") &&
                !getEntityManager().contains(newSimpleVarSubscribe.getSimpleVarDefine());
    }

    private SimpleVarDefine getSimpleVarById(String id) {
        try {
            return (SimpleVarDefine) getEntityManager().createQuery("select sv from SimpleVarDefine sv where sv.id = ?1").setParameter(1, id).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private String simpleVarState;

    public String getSimpleVarState() {
        return simpleVarState;
    }

    private SimpleVarSubscribe newSimpleVarSubscribe;

    public SimpleVarSubscribe getNewSimpleVarSubscribe() {
        return newSimpleVarSubscribe;
    }

    public void setNewSimpleVarSubscribe(SimpleVarSubscribe newSimpleVarSubscribe) {
        this.newSimpleVarSubscribe = newSimpleVarSubscribe;
    }

    public void createSimpleVarInstance() {
        int priority = 1;
        for (SimpleVarSubscribe simpleVarSubscribe : getInstance().getSimpleVarSubscribes()) {
            if (simpleVarSubscribe.getPriority() >= priority) {
                priority = simpleVarSubscribe.getPriority() + 1;
            }
        }
        log.debug("create new Simple Var- selectedTask:" + selectedTask + "|selectedVersion:" + selectedVersion);
        newSimpleVarSubscribe = new SimpleVarSubscribe(getInstance(), new SimpleVarDefine(), selectedTask, selectedVersion, priority);
        simpleVarState = "create";
    }

    public boolean isWordTypeVar() {
        return (newSimpleVarSubscribe.getSimpleVarDefine().getType() != null && newSimpleVarSubscribe.getSimpleVarDefine().getType().equals(SimpleVarDefine.VarType.WORD));
    }

    @Factory(value = "varTypes", scope = ScopeType.SESSION)
    public SimpleVarDefine.VarType[] getParamTypes() {
        return SimpleVarDefine.VarType.values();
    }

    @DataModelSelection
    private SimpleVarSubscribe selectSimpleVar;

    public void upSimpleVar() {
        OrderBeanComparator.up(selectSimpleVar, selectedSimpleVarList);
    }

    public void downSimpleVar() {
        OrderBeanComparator.down(selectSimpleVar, selectedSimpleVarList);
    }

    public void deleteSelectSimpleVar() {
        getEntityManager().remove(selectSimpleVar);
        selectedSimpleVarList.remove(selectSimpleVar);
        getInstance().getSimpleVarSubscribes().remove(selectSimpleVar);
    }

    public void copySimpleVarToLaster() {
        ProcessDefinition lasterPD = ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinition(getInstance().getWfName());

        Set<String> lasterTaskNames = lasterPD.getTaskMgmtDefinition().getTasks().keySet();

        Map<String, Set<String>> lasterSimpleVars = new HashMap<String, Set<String>>();
        for (SimpleVarSubscribe svs : getInstance().getSimpleVarSubscribes()) {
            if (svs.getWfVer().equals(String.valueOf(lasterPD.getVersion()))) {
                Set<String> simpleVars = lasterSimpleVars.get(svs.getWfTask());
                if (simpleVars == null) {
                    simpleVars = new HashSet<String>();
                    lasterSimpleVars.put(svs.getWfTask(), simpleVars);
                }
                simpleVars.add(svs.getSimpleVarDefine().getId());
            }
        }


        Set<SimpleVarSubscribe> newSimpleVarSubscribes = new HashSet<SimpleVarSubscribe>();
        for (SimpleVarSubscribe svs : getInstance().getSimpleVarSubscribes()) {
            if (svs.getWfVer().equals(selectedVersion) && lasterTaskNames.contains(svs.getWfTask())) {
                Set<String> simpleIds = lasterSimpleVars.get(svs.getWfTask());
                if (simpleIds == null || !simpleIds.contains(svs.getSimpleVarDefine().getId())) {
                    newSimpleVarSubscribes.add(new SimpleVarSubscribe(getInstance(), svs.getWfTask(), String.valueOf(lasterPD.getVersion()), svs));

                }
            }
        }
        getInstance().getSimpleVarSubscribes().addAll(newSimpleVarSubscribes);
    }

    @Override
    protected void selectVersionChanged() {
        taskSimpleVarChange();
    }
}
