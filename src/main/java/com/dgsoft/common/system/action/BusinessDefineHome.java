package com.dgsoft.common.system.action;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.common.system.model.SimpleVarSubscribe;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/23/13
 * Time: 2:39 PM
 */

@Name("businessDefineHome")
public class BusinessDefineHome extends SystemEntityHome<BusinessDefine> {

    @In
    private FacesMessages facesMessages;

    public void verifyIdAvailable(ValueChangeEvent e) {
        String id = (String) e.getNewValue();
        if (!isIdAvailable(id)) {
            facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", id);
        }
    }

    @Override
    protected boolean verifyPersistAvailable() {
        String newId = this.getInstance().getId();
        if (!isIdAvailable(newId)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "fieldConflict", newId);
            return false;
        } else
            return true;

    }

    public boolean isIdAvailable(String newId) {
        return getEntityManager().createQuery("select bd from BusinessDefine bd where bd.id = ?1").setParameter(1, newId).getResultList().size() == 0;
    }

    public List<SimpleVarSubscribe> getSimpleVarDefineList(String version, String taskName) {
        //if (isIdDefined()){
        List<SimpleVarSubscribe> result = new ArrayList<SimpleVarSubscribe>();
        for (SimpleVarSubscribe simpleVarSubscribe : getInstance().getSimpleVarSubscribes()) {
            if (simpleVarSubscribe.getWfVer().equals(version) && simpleVarSubscribe.getWfTask().equals(taskName)) {
                result.add(simpleVarSubscribe);
            }
        }
        Collections.sort(result, OrderBeanComparator.getInstance());
        return result;
        //}else{
        //    throw new IllegalStateException("BusinessDefineHome id not Defined");
        //}
    }
}
