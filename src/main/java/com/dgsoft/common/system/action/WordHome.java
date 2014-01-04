package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.Word;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-19
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */


@Name("wordHome")
public class WordHome extends SystemEntityHome<Word> {

    @In
    private FacesMessages facesMessages;

    public void verifyIdAvailableaa(ValueChangeEvent e) {
        String name = (String) e.getNewValue();
        if (!isNameAvailable(name)) {
            log.info("add confirm message");
            facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", name);
        }
    }

    @Override
    protected boolean verifyPersistAvailable(){
        String name = this.getInstance().getValue();
        if (!isNameAvailable(name)){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"fieldConflict", name);
            return false;
        }else
            return true;

    }

    public boolean isNameAvailable(String name) {
        return getEntityManager().createQuery("select w from Word w where w.value = ?1").setParameter(1, name).getResultList().size() == 0;
    }



}
