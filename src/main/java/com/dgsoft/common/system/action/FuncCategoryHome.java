package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.SystemEntityQuery;
import com.dgsoft.common.system.model.FuncCategory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;


@Name("funcCategoryHome")
public class FuncCategoryHome extends SystemEntityHome<FuncCategory> {

    /**
     *
     */
    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private SystemEntityQuery funcCategoryListCount;


    private static final long serialVersionUID = 8256615696867291285L;



    @Override
    protected FuncCategory createInstance(){
        FuncCategory result = super.createInstance();
        if (result!=null){
            long count = (Long)funcCategoryListCount.getSingleResult();
            result.setPriority((int)count);
        }
        return  result;
    }


    public void verifyIdAvailable(ValueChangeEvent e) {
        String name = (String) e.getNewValue();
        if (!isNameAvailable(name)) {
            log.info("add confirm message");
            facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", name);
        }
    }

    @Override
    protected boolean verifyPersistAvailable(){
        String name = this.getInstance().getName();
        if (!isNameAvailable(name)){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"fieldConflict", name);
            return false;
        }else
            return true;

    }

    public boolean isNameAvailable(String name) {
        return getEntityManager().createQuery("select fc from FuncCategory fc where fc.name = ?1").setParameter(1, name).getResultList().size() == 0;
    }





}
