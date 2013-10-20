package com.dgsoft.erp.action;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.FormatDefine;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/24/13
 * Time: 2:45 PM
 */
@Name("resHome")
public class ResHome extends ErpEntityHome<Res> {

    @In(create = true)
    private ResCategoryHome resCategoryHome;

    @In
    private FacesMessages facesMessages;

    @In
    private ActionExecuteState actionExecuteState;

    @DataModel(scope = ScopeType.PAGE)
    private List<FormatDefine> formatDefineList = new ArrayList<FormatDefine>();

    @DataModelSelection
    private FormatDefine formatDefine;

    private FormatDefine newFormatDefine = new FormatDefine();

    public FormatDefine getNewFormatDefine() {
        return newFormatDefine;
    }

    public void setNewFormatDefine(FormatDefine newFormatDefine) {
        this.newFormatDefine = newFormatDefine;
    }

    public void addFormatDefine() {
        newFormatDefine.setRes(getInstance());
        if (!newFormatDefine.getDataType().equals(FormatDefine.FormatType.WORD)) {
            newFormatDefine.setWordCategory(null);
        }
        OrderBeanComparator.addToLast(newFormatDefine, formatDefineList);
        newFormatDefine = new FormatDefine();
        actionExecuteState.setLastState("success");
    }

    @Factory(value = "formatTypes", scope = ScopeType.SESSION, autoCreate = true)
    public FormatDefine.FormatType[] getFormatTypes() {
        return FormatDefine.FormatType.values();
    }

    @Override
    protected void initInstance() {
        super.initInstance();
        formatDefineList = new ArrayList<FormatDefine>(getInstance().getFormatDefines());
        Collections.sort(formatDefineList, OrderBeanComparator.getInstance());
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            getInstance().getFormatDefines().clear();
            getInstance().getFormatDefines().addAll(formatDefineList);
            getInstance().setResCategory(resCategoryHome.getInstance());
        }
        return true;
    }

    public void deleteFormatDefine() {
        formatDefineList.remove(formatDefine);
    }

    public void upFormatDefine() {
        OrderBeanComparator.up(formatDefine, formatDefineList);
    }

    public void downFormatDefine() {
        OrderBeanComparator.down(formatDefine, formatDefineList);
    }

    public void unitGroupSelectListener(){
        if (getInstance().getUnitGroup() != null){
           for (ResUnit resUnit: getInstance().getUnitGroup().getResUnits()){
               if (resUnit.getConversionRate().intValue() == 1){
                   getInstance().setResUnitByMasterUnit(resUnit);
                   getInstance().setResUnitByInDefault(resUnit);
                   getInstance().setResUnitByOutDefault(resUnit);
                   return;
               }
           }
        }
    }

    @Override
    protected Res createInstance(){
        return new Res(true);
    }

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
        return getEntityManager().createQuery("select res from Res res where res.id = ?1").setParameter(1, newId).getResultList().size() == 0;
    }

}
