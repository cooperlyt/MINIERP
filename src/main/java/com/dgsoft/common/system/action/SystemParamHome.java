package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.SystemParam;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/15/13
 * Time: 8:15 AM
 */

@Name("systemParamHome")
public class SystemParamHome extends SystemEntityHome<SystemParam> {


    @In
    private FacesMessages facesMessages;

    @Factory(value = "paramTypes", scope = ScopeType.SESSION)
    public SystemParam.ParamType[] getParamTypes() {
        return SystemParam.ParamType.values();
    }


    public void verifyIdAvailable(ValueChangeEvent e) {
        String id = (String) e.getNewValue();
        if (!isIdAvailable(id)) {
            log.info("add confirm message");
           facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", id);
        }
    }

    @Override
    protected boolean verifyPersistAvailable(){
        String newId = this.getInstance().getId();
        if (!isIdAvailable(newId)){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"fieldConflict", newId);
            return false;
        }else
            return true;

    }

    public boolean isIdAvailable(String newId) {
        return getEntityManager().createQuery("select sp from SystemParam sp where sp.id = ?1").setParameter(1, newId).getResultList().size() == 0;
    }

}
