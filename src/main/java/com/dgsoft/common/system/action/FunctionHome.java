package com.dgsoft.common.system.action;


import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.SystemEntityQuery;
import com.dgsoft.common.system.model.Function;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;

@Name("functionHome")
public class FunctionHome extends SystemEntityHome<Function> {

	/**
	 * 
	 */
    @In
    private FacesMessages facesMessages;

	private static final long serialVersionUID = -240634452292133862L;

    @In (required = false)
    private FuncCategoryHome funcCategoryHome;

    @In (create = true)
    private SystemEntityQuery funcListCount;

    public void addFuncCategoryInfo(){
       if (getInstance() !=null){
           getInstance().setFuncCategory(funcCategoryHome.getInstance());
           long count = (Long)funcListCount.getSingleResult();
           getInstance().setPriority((int)count);
       }
    }


    public void verifyIdAvailableaa(ValueChangeEvent e) {
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
        return getEntityManager().createQuery("select f from Function f where f.name = ?1").setParameter(1, name).getResultList().size() == 0;
    }


	
	
	

}
